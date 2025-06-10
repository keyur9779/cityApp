package com.testcityapp.presentation.details

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.ui.text.font.FontWeight
import com.testcityapp.presentation.components.AppBodyText
import com.testcityapp.presentation.components.AppText
import com.testcityapp.presentation.components.AppTitleText
import androidx.compose.runtime.Composable
import com.testcityapp.core.utils.toFormattedString
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.compose.GoogleMap
import com.google.maps.android.compose.Marker
import com.google.maps.android.compose.MarkerState
import com.google.maps.android.compose.rememberCameraPositionState
import com.testcityapp.domain.model.CityEmission

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DetailsScreen(
    emission: CityEmission,
    isInSplitView: Boolean = false,
    modifier: Modifier = Modifier
) {

    // For text color, use white on dark backgrounds and black on light backgrounds
    val textColor = remember(emission.displayColor) {
        when (emission.displayColor) {
            Color.White, Color.Yellow, Color.Green -> Color.Black
            else -> Color.White
        }
    }
    
    val cityLocation = remember(emission.latitude, emission.longitude) {
        LatLng(emission.latitude, emission.longitude)
    }
    
    // Remember camera position but update it when the emission changes
    val cameraPositionState = rememberCameraPositionState {
        position = CameraPosition.fromLatLngZoom(cityLocation, 10f)
    }
    
    // Update camera position when emission changes
    LaunchedEffect(key1 = emission.id) {
        cameraPositionState.animate(
            update =CameraUpdateFactory.newLatLngZoom(cityLocation, 10f)
        )
    }
    
    Scaffold(
        modifier = modifier.fillMaxSize(),
        topBar = {
            TopAppBar(
                title = { 
                    Column {
                        // Optional indicator text when in split view
                        if (isInSplitView) {
                            AppText(
                                text = "SELECTED CITY",
                                style = MaterialTheme.typography.labelSmall,
                                color = if (textColor == Color.White) 
                                    Color.White.copy(alpha = 0.7f) 
                                else 
                                    Color.Black.copy(alpha = 0.7f)
                            )
                        }
                        
                        AppTitleText(
                            text = emission.city,
                            color = textColor
                        )
                    }
                },
                colors = TopAppBarDefaults.mediumTopAppBarColors(
                    containerColor = emission.displayColor
                )
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            // Map view taking 70% of the space
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .weight(0.7f)
            ) {
                GoogleMap(
                    modifier = Modifier.fillMaxSize(),
                    cameraPositionState = cameraPositionState
                ) {
                    Marker(
                        state = MarkerState(position = cityLocation),
                        title = emission.city
                    )
                }
            }
            
            // City details taking 30% of the space
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .weight(0.3f)
                    .padding(16.dp)
            ) {
                // City info card
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.surfaceVariant
                    )
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp)
                    ) {
                        AppTitleText(text = "City Details")
                        
                        Spacer(modifier = Modifier.height(8.dp))
                        
                        Row(
                            horizontalArrangement = Arrangement.SpaceBetween,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            AppBodyText(text = "City:")
                            AppText(
                                text = emission.city,
                                style = MaterialTheme.typography.bodyLarge,
                                fontWeight = FontWeight.Bold
                            )
                        }
                        
                        Spacer(modifier = Modifier.height(4.dp))
                        
                        Row(
                            horizontalArrangement = Arrangement.SpaceBetween,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            AppBodyText(text = "Color:")
                            AppText(
                                text = emission.color,
                                style = MaterialTheme.typography.bodyLarge,
                                color = textColor
                            )
                        }
                        
                        Spacer(modifier = Modifier.height(4.dp))
                        
                        Row(
                            horizontalArrangement = Arrangement.SpaceBetween,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            AppBodyText(text = "Coordinates:")
                            AppText(
                                text = "(${emission.latitude}, ${emission.longitude})",
                                style = MaterialTheme.typography.bodyMedium
                            )
                        }
                        
                        Spacer(modifier = Modifier.height(4.dp))
                        
                        Row(
                            horizontalArrangement = Arrangement.SpaceBetween,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            AppBodyText(text = "Time:")
                            AppText(
                                text = emission.timestamp.toFormattedString(),
                                style = MaterialTheme.typography.bodyMedium
                            )
                        }
                    }
                }
            }
        }
    }
}
