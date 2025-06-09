package com.testcityapp.presentation.main

import android.app.Application
import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.lifecycle.LifecycleOwner
import androidx.work.OneTimeWorkRequest
import androidx.work.WorkManager
import com.testcityapp.domain.model.CityEmission
import com.testcityapp.domain.usecase.CityRepository
import com.testcityapp.domain.usecase.GetCityEmissionsUseCase
import io.mockk.every
import io.mockk.justRun
import io.mockk.mockk
import io.mockk.mockkStatic
import io.mockk.slot
import io.mockk.verify
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.flowOf
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
    private lateinit var useCase: GetCityEmissionsUseCase
    private lateinit var workManager: WorkManager
    private lateinit var viewModel: MainViewModel
    private lateinit var lifecycleOwner: LifecycleOwner
    
    private val testDispatcher = UnconfinedTestDispatcher()
    
    @Before
    fun setUp() {
        Dispatchers.setMain(testDispatcher)
        
        // Set up mocks
        application = mockk(relaxed = true)
        // Use domain interface instead of implementation
        repository = mockk<CityRepository>(relaxed = true)
        workManager = mockk(relaxed = true)
        lifecycleOwner = mockk(relaxed = true)
        
        // Mock WorkManager.getInstance
        mockkStatic(WorkManager::class)
        every { WorkManager.getInstance(any<Application>()) } returns workManager
        
        // Create the use case with repository that returns test data
        val testEmissions = listOf(
            CityEmission(id = 1, city = "New York", color = "Blue", timestamp = LocalDateTime.now()),
            CityEmission(id = 2, city = "Los Angeles", color = "Red", timestamp = LocalDateTime.now())
        )
        every { repository.getCityEmissions() } returns flowOf(testEmissions)
        
        useCase = GetCityEmissionsUseCase(repository)
        
        // Create the view model with domain interface
        viewModel = MainViewModel(application, useCase, repository)
    }
    
    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }
    
    @Test
    fun `test startProducing calls repository`() {
        // When
        viewModel.startProducing()
        
        // Then
        verify { repository.startProducing() }
    }
    
    @Test
    fun `test stopProducing calls repository`() {
        // When
        viewModel.stopProducing()
        
        // Then
        verify { repository.stopProducing() }
    }
    

    @Test
    fun `test lifecycle callbacks call appropriate methods`() {
        // When
        viewModel.onResume(lifecycleOwner)
        
        // Then
        verify { repository.startProducing() }
        
        // When
        viewModel.onPause(lifecycleOwner)
        
        // Then
        verify { repository.stopProducing() }
    }
    
    @Test
    fun `test emissions StateFlow contains data from repository`() = runTest {
        // With UnconfinedTestDispatcher, we need to run in a coroutine context to ensure StateFlow collects
        
        // Allow time for the StateFlow to collect initial values
        testDispatcher.scheduler.advanceUntilIdle()
        
        // Then - Verify that the emissions StateFlow has the correct data
        val currentEmissions = viewModel.emissions.value
        assertEquals(2, currentEmissions.size)
        assertEquals("New York", currentEmissions[0].city)
        assertEquals("Los Angeles", currentEmissions[1].city)
    }
    
    @Test
    fun `test onCleared calls stopProducing`() {
        // When
        viewModel.onCleared()
        
        // Then
        verify(exactly = 1) { repository.stopProducing() }
    }
}
