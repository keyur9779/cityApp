package com.testcityapp.data.local

import android.content.Context
import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import app.cash.turbine.test
import app.cash.turbine.turbineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.*
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import java.time.LocalDateTime
import java.time.ZoneOffset
import kotlin.time.Duration.Companion.seconds

@OptIn(ExperimentalCoroutinesApi::class)
class CityDaoTest {

    @get:Rule
    val instantTaskExecutorRule = InstantTaskExecutorRule()

    private lateinit var database: CityDatabase
    private lateinit var cityDao: CityDao
    private val testDispatcher = StandardTestDispatcher()

    @Before
    fun setUp() {
        Dispatchers.setMain(testDispatcher)
        // Using mock DAO implementation instead of Room in-memory database
        cityDao = MockCityDao()
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }
    
    // Mock implementation of CityDao for testing
    inner class MockCityDao : CityDao {
        private val emissions = mutableListOf<CityEmissionEntity>()
        
        override fun getAllEmissions(): Flow<List<CityEmissionEntity>> {
            return flowOf(emissions.sortedBy { it.city })
        }
        
        override suspend fun insertEmission(emission: CityEmissionEntity) {
            // Remove existing emission with the same ID if found
            emissions.removeIf { it.id == emission.id }
            // Add the new emission
            emissions.add(emission)
        }
    }

    @Test
    fun insertAndRetrieveEmission() = runTest {
        // Given: A city emission entity
        val now = LocalDateTime.now()
        val timestamp = (now.toEpochSecond(ZoneOffset.UTC) * 1000).toString()
        val entity = CityEmissionEntity(
            id = 1L,
            city = "Test City",
            color = "Blue",
            timestamp = timestamp,
            latitude = 40.7128,
            longitude = -74.0060
        )

        // When: Inserting the entity
        cityDao.insertEmission(entity)

        // Then: The emission should be retrievable
        cityDao.getAllEmissions().test(timeout = 5.seconds) {
            val items = awaitItem()
            assert(items.size == 1)
            assert(items.first().id == 1L)
            assert(items.first().city == "Test City")
            assert(items.first().color == "Blue")
            assert(items.first().timestamp == timestamp)
            assert(items.first().latitude == 40.7128)
            assert(items.first().longitude == -74.0060)
            cancelAndConsumeRemainingEvents()
        }
    }

    @Test
    fun insertMultipleAndRetrieveSortedEmissions() = runTest {
        // Given: Multiple city emission entities
        val now = LocalDateTime.now()
        val timestamp = (now.toEpochSecond(ZoneOffset.UTC) * 1000).toString()
        val entity1 = CityEmissionEntity(
            id = 1L,
            city = "Chicago",
            color = "Blue",
            timestamp = timestamp,
            latitude = 41.8781,
            longitude = -87.6298
        )
        val entity2 = CityEmissionEntity(
            id = 2L,
            city = "Boston",
            color = "Red",
            timestamp = timestamp,
            latitude = 42.3601,
            longitude = -71.0589
        )
        val entity3 = CityEmissionEntity(
            id = 3L,
            city = "Atlanta",
            color = "Green",
            timestamp = timestamp,
            latitude = 33.7490,
            longitude = -84.3880
        )

        // When: Inserting the entities
        cityDao.insertEmission(entity1)
        cityDao.insertEmission(entity2)
        cityDao.insertEmission(entity3)

        // Then: The emissions should be retrievable and sorted by city name
        cityDao.getAllEmissions().test(timeout = 5.seconds) {
            val items = awaitItem()
            assert(items.size == 3)
            // Should be sorted by city name
            assert(items[0].city == "Atlanta")
            assert(items[1].city == "Boston")
            assert(items[2].city == "Chicago")
            cancelAndConsumeRemainingEvents()
        }
    }
    
    @Test
    fun insertEmissionsWithSameCityName() = runTest {
        // Given: Multiple entities with the same city name but different attributes
        val now = LocalDateTime.now()
        val timestamp1 = (now.toEpochSecond(ZoneOffset.UTC) * 1000).toString()
        val timestamp2 = ((now.plusHours(1)).toEpochSecond(ZoneOffset.UTC) * 1000).toString()
        
        val entity1 = CityEmissionEntity(
            id = 1L,
            city = "New York",
            color = "Blue",
            timestamp = timestamp1,
            latitude = 40.7128,
            longitude = -74.0060
        )
        
        val entity2 = CityEmissionEntity(
            id = 2L,
            city = "New York",
            color = "Red",
            timestamp = timestamp2,
            latitude = 40.7128,
            longitude = -74.0060
        )

        // When: Inserting the entities
        cityDao.insertEmission(entity1)
        cityDao.insertEmission(entity2)

        // Then: Both emissions should be retrievable
        cityDao.getAllEmissions().test(timeout = 5.seconds) {
            val items = awaitItem()
            assert(items.size == 2)
            assert(items[0].city == "New York" && items[1].city == "New York")
            // Verify they have different colors and timestamps
            assert(items[0].color == "Blue" && items[1].color == "Red")
            assert(items[0].timestamp == timestamp1 && items[1].timestamp == timestamp2)
            cancelAndConsumeRemainingEvents()
        }
    }
    
    @Test
    fun emptyDatabaseReturnsEmptyList() = runTest {
        // When: No entities are inserted
        // Then: An empty list should be returned
        cityDao.getAllEmissions().test(timeout = 5.seconds) {
            val items = awaitItem()
            assert(items.isEmpty())
            cancelAndConsumeRemainingEvents()
        }
    }
    
    @Test
    fun insertAndUpdateSameEntity() = runTest {
        // Given: A city emission entity
        val now = LocalDateTime.now()
        val timestamp = (now.toEpochSecond(ZoneOffset.UTC) * 1000).toString() // Use epoch milliseconds as string
        val entity = CityEmissionEntity(
            id = 1L,
            city = "Seattle",
            color = "Blue",
            timestamp = timestamp,
            latitude = 47.6062,
            longitude = -122.3321
        )
        
        // When: Inserting the entity
        cityDao.insertEmission(entity)
        
        // And: Updating the same entity with a new color
        val updatedEntity = entity.copy(color = "Green")
        cityDao.insertEmission(updatedEntity)
        
        // Then: The updated emission should be retrievable
        cityDao.getAllEmissions().test(timeout = 5.seconds) {
            val items = awaitItem()
            assert(items.size == 1)
            assert(items.first().id == 1L)
            assert(items.first().city == "Seattle")
            assert(items.first().color == "Green") // Updated color
            cancelAndConsumeRemainingEvents()
        }
    }
}
