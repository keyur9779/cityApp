package com.testcityapp.data.repository

import androidx.compose.ui.graphics.Color
import com.testcityapp.data.local.CityDao
import com.testcityapp.data.local.CityEmissionEntity
import com.testcityapp.domain.model.CityEmission
import com.testcityapp.data.producer.CityEmissionProducer
import com.testcityapp.domain.usecase.CityRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancelChildren
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import java.time.LocalDateTime
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class CityRepositoryImpl @Inject constructor(
    private val cityDao: CityDao,
    private val producer: CityEmissionProducer
) : CityRepository {
    
    private val scope = CoroutineScope(Dispatchers.IO + SupervisorJob())
    
    override fun getCityEmissions(): Flow<List<CityEmission>> {
        return cityDao.getAllEmissions().map { entities ->
            entities.map {
                CityEmission(
                    id = it.id.toLong(),
                    city = it.city,
                    color = it.color,
                    displayColor = mapColorNameToColor(it.color),
                    timestamp = LocalDateTime.ofEpochSecond(it.timestamp.toLong() / 1000, 0, java.time.ZoneOffset.UTC),
                    latitude = it.latitude,
                    longitude = it.longitude
                ) 
            }
        }
    }
    
    private fun mapColorNameToColor(colorName: String): Color {
        return when (colorName.lowercase()) {
            "yellow" -> Color.Yellow
            "white" -> Color.White
            "green" -> Color.Green
            "blue" -> Color.Blue
            "red" -> Color.Red
            "black" -> Color.Black
            else -> Color.Gray
        }
    }
    
    private suspend fun insertEmission(emission: CityEmission) {
        // The color field is already saved in the entity;
        // displayColor is computed when retrieving from database
        cityDao.insertEmission(
            CityEmissionEntity(
                city = emission.city,
                color = emission.color,
                timestamp = (emission.timestamp.toEpochSecond(java.time.ZoneOffset.UTC) * 1000).toString(),
                latitude = emission.latitude,
                longitude = emission.longitude
            )
        )
    }
    
    override fun startProducing() {
        scope.launch {
            producer.produceEmissions().collect { emission ->
                // Enhance the emission with the display color
                val enhancedEmission = emission.copy(
                    displayColor = mapColorNameToColor(emission.color)
                )
                insertEmission(enhancedEmission)
            }
        }
    }
    
    override fun stopProducing() {
        scope.coroutineContext.cancelChildren()
    }
}
