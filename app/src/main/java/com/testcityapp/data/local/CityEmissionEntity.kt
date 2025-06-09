package com.testcityapp.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.testcityapp.domain.model.CityEmission
import java.time.LocalDateTime

@Entity(tableName = "emissions")
data class CityEmissionEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val city: String,
    val color: String,
    val timestamp: String, // Store as ISO string
    val latitude: Double,
    val longitude: Double
) {
    fun toDomain(): CityEmission {
        return CityEmission(
            id = id,
            city = city,
            color = color,
            timestamp = LocalDateTime.parse(timestamp),
            latitude = latitude,
            longitude = longitude
        )
    }

    companion object {
        fun fromDomain(emission: CityEmission): CityEmissionEntity {
            return CityEmissionEntity(
                id = emission.id,
                city = emission.city,
                color = emission.color,
                timestamp = emission.timestamp.toString(),
                latitude = emission.latitude,
                longitude = emission.longitude
            )
        }
    }
}