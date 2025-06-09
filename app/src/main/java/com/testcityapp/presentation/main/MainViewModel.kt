package com.testcityapp.presentation.main

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.DefaultLifecycleObserver
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.viewModelScope
import androidx.work.Data
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkManager
import com.testcityapp.domain.repository.CityRepository
import com.testcityapp.domain.usecase.GetCityEmissionsUseCase
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
    private val cityRepository: CityRepository
) : AndroidViewModel(application), DefaultLifecycleObserver {

    val emissions = getCityEmissionsUseCase().stateIn(
        viewModelScope,
        SharingStarted.WhileSubscribed(1000),
        emptyList()
    )


    fun startProducing() {
        cityRepository.startProducing()
    }

    fun stopProducing() {
        cityRepository.stopProducing()
    }

    fun scheduleWelcomeToast(cityName: String) {
        val workData = Data.Builder()
            .putString(WelcomeWorker.KEY_CITY_NAME, cityName)
            .build()

        val welcomeWorkRequest = OneTimeWorkRequestBuilder<WelcomeWorker>()
            .setInputData(workData)
            .setInitialDelay(2, TimeUnit.SECONDS)
            .build()

        WorkManager.getInstance(application).enqueue(welcomeWorkRequest)
    }

    override fun onCleared() {
        super.onCleared()
        stopProducing()
    }

    override fun onResume(owner: LifecycleOwner) {
        startProducing()
    }

    override fun onPause(owner: LifecycleOwner) {
        stopProducing()
    }
}