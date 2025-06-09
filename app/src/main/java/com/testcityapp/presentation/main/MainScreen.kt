package com.testcityapp.presentation.main

import android.util.Log
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import com.testcityapp.presentation.components.AppTitleText
import com.testcityapp.presentation.components.LoadingContent
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.testcityapp.domain.model.CityEmission
import com.testcityapp.presentation.components.AppTitleText
import com.testcityapp.presentation.components.CityEmissionItem

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainScreen(
    emissions: List<CityEmission>,
    onEmissionClick: (CityEmission) -> Unit,
    selectedEmissionId: Long = -1,  // Default to -1 when no selection
    modifier: Modifier = Modifier
) {
    Scaffold(
        modifier = modifier.fillMaxSize(),
        topBar = {
            TopAppBar(
                title = { AppTitleText(text = "City Emissions") },
                colors = TopAppBarDefaults.mediumTopAppBarColors()
            )
        }
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            if (emissions.isEmpty()) {
                // Show loading indicator if no emissions yet
                LoadingContent(
                    message = "Waiting for first emission...",
                    modifier = Modifier.align(Alignment.Center)
                )
            } else {
                // Show sorted list of emissions
                LazyColumn(
                    modifier = Modifier.fillMaxWidth(),
                    contentPadding = PaddingValues(bottom = 16.dp) // Add some bottom padding
                ) {
                    items(
                        items = emissions.sortedBy { it.city },
                        key = { it.id }
                    ) { emission ->
                        // Check if this is the selected item
                        val isSelected = emission.id == selectedEmissionId
                        
                        CityEmissionItem(
                            emission = emission,
                            isSelected = isSelected,
                            onItemClick = {
                                Log.d("Navigating", "Clicked on emission: ${emission.id}, city: ${emission.city}")
                                onEmissionClick(emission)
                            }
                        )
                    }
                }
            }
        }
    }
}
