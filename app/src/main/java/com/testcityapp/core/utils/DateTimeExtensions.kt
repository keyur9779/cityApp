package com.testcityapp.core.utils

import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

/**
 * Extension function to format a LocalDateTime to a readable date-time string.
 * Format: "yyyy-MM-dd HH:mm:ss"
 */
fun LocalDateTime.toFormattedString(): String {
    return this.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"))
}
