package com.testcityapp.data.repository

import androidx.compose.ui.graphics.Color
import app.cash.turbine.test
import com.testcityapp.data.local.CityDao
import com.testcityapp.data.local.CityEmissionEntity
import com.testcityapp.data.producer.CityEmissionProducer
import com.testcityapp.domain.model.CityEmission
import com.testcityapp.data.repository.CityRepository
import kotlinx.coroutines.runBlocking
import io.mockk.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.*
import org.junit.After
import org.junit.Before
import org.junit.Test
import java.time.LocalDateTime
import java.time.ZoneOffset
import kotlin.time.Duration.Companion.seconds

@OptIn(ExperimentalCoroutinesApi::class)
class CityRepositoryImplTest {

    // Subject under test
    private lateinit var repository: CityRepository

    // Dependencies
    private lateinit var cityDao: CityDao
    private lateinit var emissionProducer: CityEmissionProducer
    
    // Test scheduler for controlling virtual time
    private val testScheduler = TestCoroutineScheduler()
    private val testDispatcher = StandardTestDispatcher(testScheduler)
    
    @Before
    fun setUp() {
        Dispatchers.setMain(testDispatcher)
        
        // Create mocks
        cityDao = mockk(relaxed = true)
        emissionProducer = mockk()
        
        // Create the repository with the mocks and explicitly set the test dispatcher
        repository = CityRepositoryImpl(cityDao, emissionProducer, testDispatcher)
    }
    
    @After
    fun tearDown() {
        Dispatchers.resetMain()
        clearAllMocks()
    }

    @Test
    fun `getCityEmissions maps entities to domain models correctly`() = runTest(testScheduler) {
        // Given - Set up test data
        val now = LocalDateTime.now()
        val timestamp = (now.toEpochSecond(ZoneOffset.UTC) * 1000).toString()
        
        val entities = listOf(
            CityEmissionEntity(
                id = 1,
                city = "New York",
                color = "Blue",
                timestamp = timestamp,
                latitude = 40.7128,
                longitude = -74.0060
            ),
            CityEmissionEntity(
                id = 2,
                city = "Los Angeles",
                color = "Red",
                timestamp = timestamp,
                latitude = 34.0522,
                longitude = -118.2437
            )
        )
        
        // Mock the DAO to return our test entities
        every { cityDao.getAllEmissions() } returns flowOf(entities)
        
        // When & Then - Collect emissions and verify mapping
        repository.getCityEmissions().test(timeout = 5.seconds) {
            val emissions = awaitItem()
            assert(emissions.size == 2)
            
            with(emissions[0]) {
                assert(id == 1L)
                assert(city == "New York")
                assert(color == "Blue")
                // Verify timestamp conversion is correct (allowing for small precision loss)
                val emissionEpochSeconds = this.timestamp.toEpochSecond(ZoneOffset.UTC)
                val expectedEpochSeconds = now.toEpochSecond(ZoneOffset.UTC)
                assert(Math.abs(emissionEpochSeconds - expectedEpochSeconds) < 2) // Allow 2 seconds difference due to possible precision issues
                assert(latitude == 40.7128)
                assert(longitude == -74.0060)
            }
            
            with(emissions[1]) {
                assert(id == 2L)
                assert(city == "Los Angeles")
                assert(color == "Red")
            }
            
            cancelAndConsumeRemainingEvents()
        }
    }

    @Test
    fun `startProducing collects emissions and inserts them into database`() = runTest(testScheduler) {
        // Given - A sequence of emissions from the producer
        val emission1 = CityEmission(
            city = "Chicago",
            color = "Green",
            displayColor = Color.Green,
            timestamp = LocalDateTime.now(),
            latitude = 41.8781,
            longitude = -87.6298
        )
        
        val emission2 = CityEmission(
            city = "Miami",
            color = "Blue",
            displayColor = Color.Blue,
            timestamp = LocalDateTime.now(),
            latitude = 25.7617,
            longitude = -80.1918
        )
        
        // Mock the producer to emit our test emissions
        every { emissionProducer.produceEmissions() } returns flow {
            emit(emission1)
            emit(emission2)
        }
        
        // Mock the dao to return null for getEmissionByCity (indicating cities don't exist)
        coEvery { cityDao.getEmissionByCity(any()) } returns null
        
        // Capture inserted entities for verification
        val insertSlot = slot<CityEmissionEntity>()
        val capturedInserts = mutableListOf<CityEmissionEntity>()
        
        coEvery { cityDao.insertEmission(capture(insertSlot)) } coAnswers {
            capturedInserts.add(insertSlot.captured)
        }
        
        // When - Start producing
        repository.startProducing()
        
        // Then - Advance time to allow flow collection to happen
        testScheduler.advanceUntilIdle()
        
        // Verify the inserted entities match the expected data
        assertEquals(2, capturedInserts.size)
        assertEquals("Chicago", capturedInserts[0].city)
        assertEquals("Green", capturedInserts[0].color)
        assertEquals("Miami", capturedInserts[1].city)
        assertEquals("Blue", capturedInserts[1].color)
    }

    @Test
    fun `stopProducing cancels the collection of emissions`() = runTest(testScheduler) {
        // Given - A never-ending flow of emissions and a capture for the insertEmission calls
        val capturedEntities = mutableListOf<CityEmissionEntity>()
        val entitySlot = slot<CityEmissionEntity>()

        every { emissionProducer.produceEmissions() } returns flow {
            while (true) {
                emit(CityEmission(city = "Test City", color = "Blue", displayColor = Color.Blue))
                kotlinx.coroutines.delay(1000)
            }
        }
        
        coEvery { cityDao.insertEmission(capture(entitySlot)) } coAnswers {
            capturedEntities.add(entitySlot.captured)
        }
        
        // When - Start producing and then stop after some time
        repository.startProducing()
        testScheduler.advanceTimeBy(500)
        repository.stopProducing()
        
        // Then - Advance more time to ensure no more processing happens
        testScheduler.advanceTimeBy(5000)
        
        // If the job was cancelled properly, we should have at most one emission captured
        // since we're advancing by 500ms and the delay in the flow is 1000ms
        assert(capturedEntities.size <= 1) { "Expected at most 1 emission but got ${capturedEntities.size}" }
    }
    
    @Test
    fun `repository handles database errors gracefully`() = runTest(testScheduler) {
        // Given - Producer emits valid data but DAO throws an exception
        val emission = CityEmission(
            city = "Houston",
            color = "Orange",
            displayColor = Color.Yellow,  // Closest match to Orange
            timestamp = LocalDateTime.now(),
            latitude = 29.7604,
            longitude = -95.3698
        )

        every { emissionProducer.produceEmissions() } returns flow {
            emit(emission)
        }
        
        // Mock the DAO to throw an exception when insertEmission is called
        coEvery { cityDao.getEmissionByCity(any()) } returns null
        coEvery { cityDao.insertEmission(any()) } throws RuntimeException("Database error")
        
        // When - Start producing - this should NOT throw an exception as the repository should catch it
        repository.startProducing()
            
        // Then - Advance time to allow flow collection to happen
        testScheduler.advanceUntilIdle()
            
        // Verify the method was called - the repository should have tried to insert
        // but caught the exception
        coVerify { cityDao.insertEmission(any()) }
    }

    @Test
    fun `insertEmission updates existing city rather than inserting duplicate`() = runTest(testScheduler) {
        // Given - An existing city emission
        val existingEmission = CityEmissionEntity(
            id = 1,
            city = "Chicago",
            color = "Green",
            timestamp = "1654344000000", // Some timestamp
            latitude = 41.8781,
            longitude = -87.6298
        )
        
        // A new emission for the same city but with updated details
        val newEmission = CityEmission(
            city = "Chicago", // Same city name
            color = "Blue", // Different color
            displayColor = Color.Blue,
            timestamp = LocalDateTime.now(),
            latitude = 41.8800, // Slightly different coordinates
            longitude = -87.6400
        )
        
        // Mock dao to return the existing emission when queried
        coEvery { cityDao.getEmissionByCity("Chicago") } returns existingEmission
        
        // Capture update calls
        val updateSlot = slot<CityEmissionEntity>()
        coEvery { cityDao.updateEmission(capture(updateSlot)) } just Runs
        
        // When - Insert the new emission
        runBlocking {
            repository.insertEmission(newEmission)
        }
        
        // Then - Verify that update was called, not insert
        coVerify(exactly = 0) { cityDao.insertEmission(any()) }
        coVerify(exactly = 1) { cityDao.updateEmission(any()) }
        
        // Verify the update contains the correct data
        val updatedEmission = updateSlot.captured
        assertEquals(1L, updatedEmission.id) // ID should remain the same
        assertEquals("Chicago", updatedEmission.city) // City should remain the same
        assertEquals("Blue", updatedEmission.color) // Color should be updated
        assertEquals(41.8800, updatedEmission.latitude) // Coordinates should be updated
        assertEquals(-87.6400, updatedEmission.longitude)
    }
    
    @Test
    fun `complete flow test for duplicate city prevention`() = runTest(testScheduler) {
        // Given - A series of emissions for the same city with different details
        val city = "San Francisco"
        val timestamp1 = LocalDateTime.now()
        val timestamp2 = timestamp1.plusHours(2)
        
        // First emission - this should be inserted as new
        val emission1 = CityEmission(
            city = city,
            color = "Green",
            displayColor = Color.Green,
            timestamp = timestamp1,
            latitude = 37.7749,
            longitude = -122.4194
        )
        
        // Second emission for the same city - this should update the existing record
        val emission2 = CityEmission(
            city = city, 
            color = "Blue",
            displayColor = Color.Blue,
            timestamp = timestamp2,
            latitude = 37.7750, // slightly different coords
            longitude = -122.4195
        )
        
        // Initial state: No existing emission for this city
        coEvery { cityDao.getEmissionByCity(city) } returns null
        
        // First insertion - should be a new record
        repository.insertEmission(emission1)
        
        // Verify insert was called
        coVerify(exactly = 1) { cityDao.insertEmission(any()) }
        coVerify(exactly = 0) { cityDao.updateEmission(any()) }
        
        // Setup for second emission - now there is an existing record
        val existingEntity = CityEmissionEntity(
            id = 1,
            city = city,
            color = "Green",
            timestamp = (timestamp1.toEpochSecond(ZoneOffset.UTC) * 1000).toString(),
            latitude = 37.7749,
            longitude = -122.4194
        )
        
        // Update the mock to return the existing entity
        coEvery { cityDao.getEmissionByCity(city) } returns existingEntity
        
        // Prepare to capture what's passed to updateEmission
        val updateSlot = slot<CityEmissionEntity>()
        coEvery { cityDao.updateEmission(capture(updateSlot)) } just Runs
        
        // Clear previous verification counts only
        clearMocks(cityDao, verificationMarks = true, answers = false)
        
        // The second emission - should update the existing record
        repository.insertEmission(emission2)
        
        // Verify update was called instead of insert
        coVerify(exactly = 0) { cityDao.insertEmission(any()) }
        coVerify(exactly = 1) { cityDao.updateEmission(any()) }
        
        // Verify the updated entity has the new values while preserving the id
        with(updateSlot.captured) {
            assertEquals(1, id) // ID preserved
            assertEquals(city, this.city) // City name preserved
            assertEquals("Blue", color) // Updated color
            assertEquals(37.7750, latitude) // Updated coordinates
            assertEquals(-122.4195, longitude)
            // Timestamp should be the new one converted to milliseconds string
            assertEquals(
                (timestamp2.toEpochSecond(ZoneOffset.UTC) * 1000).toString(),
                this.timestamp
            )
        }
    }
    
    // Helper function for more readable assertions
    private fun assertEquals(expected: Any?, actual: Any?) {
        // For Long vs Int comparison which might be causing the issue
        if (expected is Number && actual is Number) {
            assert(expected.toLong() == actual.toLong()) { "Expected $expected but was $actual" }
        } else {
            assert(expected == actual) { "Expected $expected but was $actual" }
        }
    }
}
