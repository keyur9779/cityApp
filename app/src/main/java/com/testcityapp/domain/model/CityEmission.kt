package com.testcityapp.domain.model

import androidx.compose.ui.graphics.Color
import java.time.LocalDateTime

data class CityEmission(
    val id: Long = 0,
    val city: String,
    val color: String, // Original color name
    val displayColor: Color = Color.Gray, // UI Color object
    val timestamp: LocalDateTime = LocalDateTime.now(),
    val latitude: Double = 0.0,
    val longitude: Double = 0.0
)