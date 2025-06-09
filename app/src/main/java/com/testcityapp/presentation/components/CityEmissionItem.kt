package com.testcityapp.presentation.components

import android.util.Log
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.testcityapp.domain.model.CityEmission
import java.time.format.DateTimeFormatter

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CityEmissionItem(
    emission: CityEmission,
    onItemClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val textColor = when (emission.color.lowercase()) {
        "yellow" -> Color.Yellow
        "white" -> Color.White
        "green" -> Color.Green
        "blue" -> Color.Blue
        "red" -> Color.Red
        "black" -> Color.Black
        else -> MaterialTheme.colorScheme.onSurface
    }
    
    Card(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp),
        elevation = CardDefaults.cardElevation(4.dp),
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .clickable{
                    Log.d("Navigating", "Clicked on emission: ${emission.city}")
                    onItemClick()
                }
                .padding(16.dp)
        ) {
            // City name with the emitted color
            Text(
                text = emission.city,
                color = textColor,
                fontWeight = FontWeight.Bold,
                style = MaterialTheme.typography.titleLarge
            )
            
            // Date and time of emission
            Text(
                text = emission.timestamp.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")),
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.padding(top = 4.dp)
            )
        }
    }
}
