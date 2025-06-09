@file:OptIn(DelicateCoroutinesApi::class)

package com.testcityapp.core.worker

import android.content.Context
import android.util.Log
import android.widget.Toast
import androidx.work.Worker
import androidx.work.WorkerParameters
import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch

class WelcomeWorker(
    private val context: Context, workerParams: WorkerParameters
) : Worker(context, workerParams) {

    override fun doWork(): Result {
        Log.d("doWork", "doWorkdoWork")
        val cityName = inputData.getString(KEY_CITY_NAME) ?: return Result.failure()

        GlobalScope.launch(Dispatchers.Main) {
            Toast.makeText(context, "Welcome to $cityName", Toast.LENGTH_SHORT).show()
        }

        return Result.success()
    }


    companion object {
        const val KEY_CITY_NAME = "city_name"
    }
}
