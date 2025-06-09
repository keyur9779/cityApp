package com.testcityapp.data.producer

import android.app.Application
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.ProcessLifecycleOwner
import com.testcityapp.domain.model.CityEmission
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import java.time.LocalDateTime
import javax.inject.Inject

class CityEmissionProducer @Inject constructor(
) {
    private val citiesMutable = mutableListOf("New York", "Los Angeles", "Scranton", "Philadelphia", "Nashville", "Saint Louis", "Miami")
    private val colors = listOf("Yellow", "White", "Green", "Blue", "Red", "Black")
    
    fun produceEmissions(): Flow<CityEmission> = flow {
        while (citiesMutable.isNotEmpty()) {
            // Only emit if the app is in foreground
            if (isAppInForeground()) {
                // Get a random city and remove it from the list
                val randomIndex = citiesMutable.indices.random()
                val randomCity = citiesMutable.removeAt(randomIndex)
                val randomColor = colors.random()
                
                emit(
                    CityEmission(
                        city = randomCity,
                        color = randomColor,
                        timestamp = LocalDateTime.now(),
                        latitude = getLatitudeFor(randomCity),
                        longitude = getLongitudeFor(randomCity)
                    )
                )
                delay(5000) // Wait 5 seconds before checking again
            }
        }
    }
    
    private fun isAppInForeground(): Boolean {
        return ProcessLifecycleOwner.get().lifecycle.currentState.isAtLeast(Lifecycle.State.RESUMED)
    }
    
    private fun getLatitudeFor(city: String): Double {
        return when (city) {
            "New York" -> 40.7128
            "Los Angeles" -> 34.0522
            "Scranton" -> 41.4090
            "Philadelphia" -> 39.9526
            "Nashville" -> 36.1627
            "Saint Louis" -> 38.6270
            "Miami" -> 25.7617
            else -> 0.0
        }
    }
    
    private fun getLongitudeFor(city: String): Double {
        return when (city) {
            "New York" -> -74.0060
            "Los Angeles" -> -118.2437
            "Scranton" -> -75.6624
            "Philadelphia" -> -75.1652
            "Nashville" -> -86.7816
            "Saint Louis" -> -90.1994
            "Miami" -> -80.1918
            else -> 0.0
        }
    }
}
