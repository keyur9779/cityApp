package com.testcityapp.presentation.components

import android.util.Log
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.testcityapp.presentation.components.CityCard
import com.testcityapp.domain.model.CityEmission
import java.time.format.DateTimeFormatter

@Composable
fun CityEmissionItem(
    emission: CityEmission,
    onItemClick: () -> Unit,
    isSelected: Boolean = false,
    modifier: Modifier = Modifier
) {

    // Change backgroundColor based on selection state
    val backgroundColor = if (isSelected) {
        MaterialTheme.colorScheme.primaryContainer
    } else {
        MaterialTheme.colorScheme.surface
    }
    
    // Add a subtle border for selected items
    val elevation = if (isSelected) 8.dp else 4.dp
    
    CityCard(
        title = emission.city,
        subtitle = emission.timestamp.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")),
        contentColor = emission.displayColor,
        backgroundColor = backgroundColor,
        elevation = elevation,
        onClick = {
            Log.d("Navigating", "Clicked on emission: ${emission.city}")
            onItemClick()
        },
        modifier = modifier
    )
}
