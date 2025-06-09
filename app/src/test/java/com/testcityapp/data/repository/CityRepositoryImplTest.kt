package com.testcityapp.data.repository

import com.testcityapp.data.local.CityDao
import com.testcityapp.data.local.CityEmissionEntity
import com.testcityapp.data.producer.CityEmissionProducer
import com.testcityapp.domain.model.CityEmission
import io.mockk.coVerify
import io.mockk.every
import io.mockk.mockk
import io.mockk.slot
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.toList
import kotlinx.coroutines.runBlocking
import org.junit.Assert.assertEquals
import org.junit.Test
import java.time.LocalDateTime

class CityRepositoryImplTest {

    private val cityDao = mockk<CityDao>(relaxed = true)
    private val emissionProducer = mockk<CityEmissionProducer>()
    private val repository = CityRepositoryImpl(cityDao, emissionProducer)

    @Test
    fun `test getCityEmissions maps entities to domain models correctly`() = runBlocking {
        // Given
        val now = LocalDateTime.now()
        val timestamp = (now.toEpochSecond(java.time.ZoneOffset.UTC) * 1000).toString()
        
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
        
        // Mock the DAO to return our entities
        val entityFlow = MutableStateFlow(entities)
        every { cityDao.getAllEmissions() } returns entityFlow
        
        // When
        val emissions = repository.getCityEmissions().toList().first()
        
        // Then
        assertEquals(2, emissions.size)
        assertEquals("New York", emissions[0].city)
        assertEquals("Blue", emissions[0].color)
        assertEquals(40.7128, emissions[0].latitude, 0.0001)
        assertEquals(-74.0060, emissions[0].longitude, 0.0001)
    }

    @Test
    fun `test startProducing collects from producer and inserts emissions`() = runBlocking {
        // Given
        val emission = CityEmission(
            city = "New York",
            color = "Blue",
            timestamp = LocalDateTime.now(),
            latitude = 40.7128,
            longitude = -74.0060
        )
        
        // Mock the producer to emit one emission
        every { emissionProducer.produceEmissions() } returns flow { 
            emit(emission) 
        }
        
        // Capture the entity being inserted
        val entitySlot = slot<CityEmissionEntity>()
        
        // When
        repository.startProducing()
        
        // Then: verify an entity was inserted
        // Note: This is a simplified test as we can't easily verify
        // the exact values due to the background coroutine 
        coVerify { cityDao.insertEmission(capture(entitySlot)) }
    }
}
