package com.testcityapp.presentation.main

import android.app.Application
import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.compose.ui.graphics.Color
import androidx.lifecycle.LifecycleOwner
import androidx.work.OneTimeWorkRequest
import androidx.work.WorkManager
import com.testcityapp.domain.model.CityEmission
import com.testcityapp.data.repository.CityRepository
import com.testcityapp.domain.usecase.GetCityEmissionsUseCase
import com.testcityapp.domain.usecase.StartEmissionProductionUseCase
import com.testcityapp.domain.usecase.StopEmissionProductionUseCase
import io.mockk.every
import io.mockk.justRun
import io.mockk.mockk
import io.mockk.mockkStatic
import io.mockk.slot
import io.mockk.verify
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.launch
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import java.time.LocalDateTime

@OptIn(ExperimentalCoroutinesApi::class)
class MainViewModelTest {

    @get:Rule
    val instantTaskExecutorRule = InstantTaskExecutorRule()
    
    // Test dependencies
    private lateinit var application: Application
    private lateinit var repository: CityRepository
    private lateinit var getCityEmissionsUseCase: GetCityEmissionsUseCase
    private lateinit var startEmissionProductionUseCase: StartEmissionProductionUseCase
    private lateinit var stopEmissionProductionUseCase: StopEmissionProductionUseCase
    private lateinit var workManager: WorkManager
    private lateinit var viewModel: MainViewModel
    private lateinit var lifecycleOwner: LifecycleOwner
    
    private val testDispatcher = UnconfinedTestDispatcher()
    
    @Before
    fun setUp() {
        Dispatchers.setMain(testDispatcher)
        
        // Set up mocks
        application = mockk(relaxed = true)
        repository = mockk<CityRepository>(relaxed = true)
        workManager = mockk(relaxed = true)
        lifecycleOwner = mockk(relaxed = true)
        
        // Mock WorkManager.getInstance
        mockkStatic(WorkManager::class)
        every { WorkManager.getInstance(any<Application>()) } returns workManager
        
        // Create the use cases with repository that returns test data
        val testEmissions = listOf(
            CityEmission(id = 1, city = "New York", color = "Blue", displayColor = Color.Blue, timestamp = LocalDateTime.now()),
            CityEmission(id = 2, city = "Los Angeles", color = "Red", displayColor = Color.Red, timestamp = LocalDateTime.now())
        )
        every { repository.getCityEmissions() } returns flowOf(testEmissions)
        
        getCityEmissionsUseCase = GetCityEmissionsUseCase(repository)
        startEmissionProductionUseCase = StartEmissionProductionUseCase(repository)
        stopEmissionProductionUseCase = StopEmissionProductionUseCase(repository)
        
        // Create the view model with the use cases
        viewModel = MainViewModel(application, getCityEmissionsUseCase, startEmissionProductionUseCase, stopEmissionProductionUseCase)
    }
    
    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }
    
    @Test
    fun `test startProducing calls startEmissionProductionUseCase`() {
        // Given - Mock the use case
        justRun { startEmissionProductionUseCase() }
        
        // When
        viewModel.startProducing()
        
        // Then
        verify { startEmissionProductionUseCase() }
    }
    
    @Test
    fun `test stopProducing calls stopEmissionProductionUseCase`() {
        // Given - Mock the use case
        justRun { stopEmissionProductionUseCase() }
        
        // When
        viewModel.stopProducing()
        
        // Then
        verify { stopEmissionProductionUseCase() }
    }
    

    
    @Test
    fun `test lifecycle callbacks call appropriate use cases`() {
        // Given - Mock the use cases
        justRun { startEmissionProductionUseCase() }
        justRun { stopEmissionProductionUseCase() }
        
        // When
        viewModel.onResume(lifecycleOwner)
        
        // Then
        verify { startEmissionProductionUseCase() }
        
        // When
        viewModel.onPause(lifecycleOwner)
        
        // Then
        verify { stopEmissionProductionUseCase() }
    }
    
    @Test
    fun `test emissions StateFlow contains data from repository`() = runTest {
        // We need to setup the emissions into the StateFlow before checking its value
        // Make sure our mocked repository returns the data we expect
        val testEmissions = listOf(
            CityEmission(id = 1, city = "New York", color = "Blue", displayColor = Color.Blue, timestamp = LocalDateTime.now()),
            CityEmission(id = 2, city = "Los Angeles", color = "Red", displayColor = Color.Red, timestamp = LocalDateTime.now())
        )
        every { repository.getCityEmissions() } returns flowOf(testEmissions)
        
        // Create a fresh view model to ensure we get fresh StateFlow
        val freshViewModel = MainViewModel(
            application, 
            GetCityEmissionsUseCase(repository),
            StartEmissionProductionUseCase(repository),
            StopEmissionProductionUseCase(repository)
        )
        
        // Launch a collection job in the test coroutine scope to ensure the StateFlow becomes active
        val job = launch {
            freshViewModel.emissions.collect {}
        }
        
        // Advance the scheduler to process all coroutines
        testDispatcher.scheduler.advanceUntilIdle()
        
        // Now verify the emissions StateFlow has the correct data
        val currentEmissions = freshViewModel.emissions.value
        assertEquals(2, currentEmissions.size)
        assertEquals("New York", currentEmissions[0].city)
        assertEquals("Los Angeles", currentEmissions[1].city)
        
        // Clean up
        job.cancel()
    }
    
    @Test
    fun `test onCleared calls stopEmissionProductionUseCase`() {
        // Given - Mock the use case
        justRun { stopEmissionProductionUseCase() }
        
        // When
        viewModel.onCleared()
        
        // Then
        verify { stopEmissionProductionUseCase() }
    }
}
