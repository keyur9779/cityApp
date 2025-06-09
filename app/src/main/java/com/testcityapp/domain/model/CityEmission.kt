package com.testcityapp.domain.model

import java.time.LocalDateTime

data class CityEmission(
    val id: Long = 0,
    val city: String,
    val color: String,
    val timestamp: LocalDateTime = LocalDateTime.now(),
    val latitude: Double = 0.0,
    val longitude: Double = 0.0
)