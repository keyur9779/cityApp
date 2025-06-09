package com.testcityapp

import android.app.Application
import dagger.hilt.android.HiltAndroidApp

@HiltAndroidApp
class TestCityApp : Application() {
    override fun onCreate() {
        instance = this
        super.onCreate()
    }

    companion object {
        // Global application context can be accessed here if needed
        // For example, you can provide a global context to other components
        lateinit var instance: TestCityApp
    }
}