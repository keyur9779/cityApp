package com.testcityapp.presentation.main

import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.DefaultLifecycleObserver
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.viewModelScope
import androidx.work.Data
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkManager
import com.testcityapp.domain.usecase.GetCityEmissionsUseCase
import com.testcityapp.domain.usecase.StartEmissionProductionUseCase
import com.testcityapp.domain.usecase.StopEmissionProductionUseCase
import com.testcityapp.core.worker.WelcomeWorker
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.stateIn
import java.util.concurrent.TimeUnit
import javax.inject.Inject

@HiltViewModel
class MainViewModel @Inject constructor(
    private val application: Application,
    getCityEmissionsUseCase: GetCityEmissionsUseCase,
    private val startEmissionProductionUseCase: StartEmissionProductionUseCase,
    private val stopEmissionProductionUseCase: StopEmissionProductionUseCase
) : AndroidViewModel(application), DefaultLifecycleObserver {

    // Track whether we're already producing emissions to prevent duplicate subscriptions
    var isProducing = false

    val emissions = getCityEmissionsUseCase().stateIn(
        viewModelScope,
        SharingStarted.WhileSubscribed(5000), // Increased timeout to handle orientation changes
        emptyList()
    )


    fun startProducing() {
        if (!isProducing) {
            Log.d("MainViewModel", "Starting emission production")
            startEmissionProductionUseCase()
            isProducing = true
        } else {
            Log.d("MainViewModel", "Emission production already active, skipping")
        }
    }

    fun stopProducing() {
        if (isProducing) {
            Log.d("MainViewModel", "Stopping emission production")
            stopEmissionProductionUseCase()
            isProducing = false
        }
    }

    fun scheduleWelcomeToast(cityName: String) {

        Log.d("MainViewModel", "Scheduling welcome toast for city: $cityName")
        val workData = Data.Builder()
            .putString(WelcomeWorker.KEY_CITY_NAME, cityName)
            .build()

        val welcomeWorkRequest = OneTimeWorkRequestBuilder<WelcomeWorker>()
            .setInputData(workData)
            .setInitialDelay(2, TimeUnit.SECONDS)
            .build()

        WorkManager.getInstance(application).enqueue(welcomeWorkRequest)
    }

    // Only start/stop on actual app lifecycle events, not on configuration changes
    override fun onResume(owner: LifecycleOwner) {
        startProducing()
    }

    override fun onPause(owner: LifecycleOwner) {
        // We don't want to stop production on configuration changes
        // Let's not call stopProducing() here, we'll handle this in onCleared()
    }
    
    // This is only called when the ViewModel is actually destroyed, not on config changes
   public override fun onCleared() {
        super.onCleared()
        stopProducing()
    }
}