package com.testcityapp.presentation.details

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import com.testcityapp.presentation.components.AppTitleText
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
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
    modifier: Modifier = Modifier
) {
    // Convert the color string to Color object
    val backgroundColor = remember(emission.color) {
        when (emission.color.lowercase()) {
            "yellow" -> Color.Yellow
            "white" -> Color.White
            "green" -> Color.Green
            "blue" -> Color.Blue
            "red" -> Color.Red
            "black" -> Color.Black
            else -> Color.Gray
        }
    }
    
    // For text color, use white on dark backgrounds and black on light backgrounds
    val textColor = remember(backgroundColor) {
        when (backgroundColor) {
            Color.White, Color.Yellow, Color.Green -> Color.Black
            else -> Color.White
        }
    }
    
    val cityLocation = remember(emission.latitude, emission.longitude) {
        LatLng(emission.latitude, emission.longitude)
    }
    
    val cameraPositionState = rememberCameraPositionState {
        position = CameraPosition.fromLatLngZoom(cityLocation, 10f)
    }
    
    Scaffold(
        modifier = modifier.fillMaxSize(),
        topBar = {
            TopAppBar(
                title = { 
                    AppTitleText(
                        text = emission.city,
                        color = textColor
                    ) 
                },
                colors = TopAppBarDefaults.mediumTopAppBarColors(
                    containerColor = backgroundColor
                )
            )
        }
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
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
    }
}
