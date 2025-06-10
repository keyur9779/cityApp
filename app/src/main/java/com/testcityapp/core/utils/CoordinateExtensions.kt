package com.testcityapp.core.utils

import com.testcityapp.domain.model.CityEmission

/**
 * Extension function to format coordinates (latitude, longitude) to a readable string.
 */
fun CityEmission.formatCoordinates(): String {
    return "($latitude, $longitude)"
}

/**
 * Utility function to format any coordinates to a readable string.
 */
fun formatCoordinates(latitude: Double, longitude: Double): String {
    return "($latitude, $longitude)"
}
