package com.testcityapp.domain.usecase

import app.cash.turbine.test
import com.testcityapp.domain.model.CityEmission
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.TestScope
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test
import java.time.LocalDateTime

@OptIn(ExperimentalCoroutinesApi::class)
class GetCityEmissionsUseCaseTest {

    private lateinit var repository: CityRepository
    private lateinit var useCase: GetCityEmissionsUseCase
    private val testDispatcher = StandardTestDispatcher()
    private val testScope = TestScope(testDispatcher)

    @Before
    fun setUp() {
        repository = mockk()
        useCase = GetCityEmissionsUseCase(repository)
    }

    @Test
    fun `invoke should return emissions from repository`() = testScope.runTest {
        // Given
        val testEmissions = listOf(
            CityEmission(
                id = 1,
                city = "New York",
                color = "Blue",
                timestamp = LocalDateTime.now(),
                latitude = 40.7128,
                longitude = -74.0060
            ),
            CityEmission(
                id = 2,
                city = "San Francisco",
                color = "Red",
                timestamp = LocalDateTime.now(),
                latitude = 37.7749,
                longitude = -122.4194
            )
        )
        every { repository.getCityEmissions() } returns flowOf(testEmissions)

        // When & Then
        useCase().test {
            val emissions = awaitItem()
            assertEquals(testEmissions, emissions)
            assertEquals(2, emissions.size)
            assertEquals("New York", emissions[0].city)
            assertEquals("San Francisco", emissions[1].city)
            awaitComplete()
        }

        verify { repository.getCityEmissions() }
    }

    @Test
    fun `invoke should handle empty emissions list`() = testScope.runTest {
        // Given
        val emptyEmissions = emptyList<CityEmission>()
        every { repository.getCityEmissions() } returns flowOf(emptyEmissions)

        // When & Then
        useCase().test {
            val emissions = awaitItem()
            assertEquals(0, emissions.size)
            awaitComplete()
        }

        verify { repository.getCityEmissions() }
    }
}
