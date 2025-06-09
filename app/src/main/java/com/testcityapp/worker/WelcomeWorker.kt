package com.testcityapp.worker

import android.content.Context
import android.widget.Toast
import androidx.work.ForegroundInfo
import androidx.work.Worker
import androidx.work.WorkerParameters

class WelcomeWorker(
    private val context: Context,
    workerParams: WorkerParameters
) : Worker(context, workerParams) {

    override fun doWork(): Result {
        val cityName = inputData.getString(KEY_CITY_NAME) ?: return Result.failure()
        
        // Show toast with welcome message
        Toast.makeText(context, "Welcome to $cityName", Toast.LENGTH_SHORT).show()
        
        return Result.success()
    }


    companion object {
        const val KEY_CITY_NAME = "city_name"
    }
}
