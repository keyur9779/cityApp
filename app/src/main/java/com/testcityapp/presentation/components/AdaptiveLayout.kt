package com.testcityapp.presentation.components

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.testcityapp.core.utils.isSideBySideMode

/**
 * A responsive layout component that adapts to different screen sizes and orientations.
 * In side-by-side mode (typically landscape tablets), it displays content in a Row.
 * In regular mode (typically phones or portrait orientation), it displays only the primary content.
 *
 * @param primaryContent The main content to display (usually list/master view)
 * @param secondaryContent The secondary content to display when in split view (usually detail view)
 * @param showSecondaryOnly When true, shows only the secondary content (for phone detail views)
 * @param modifier Modifier to be applied to the component
 * @param divider Optional composable to be shown as a divider between panels in side-by-side mode
 */
@Composable
fun AdaptiveLayout(
    primaryContent: @Composable () -> Unit,
    secondaryContent: @Composable () -> Unit,
    showSecondaryOnly: Boolean = false,
    modifier: Modifier = Modifier,
    divider: @Composable (() -> Unit)? = { VerticalDivider() }
) {
    if (isSideBySideMode()) {
        // Side-by-side layout for tablets in landscape
        Row(modifier = modifier.fillMaxSize()) {
            if (!showSecondaryOnly) {
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxSize()
                ) {
                    primaryContent()
                }
                
                // Add a divider between panels if provided
                divider?.invoke()
            }
            
            // Always show the secondary content in a side-by-side layout
            Box(
                modifier = Modifier
                    .weight(if (showSecondaryOnly) 2f else 1f)
                    .fillMaxSize()
            ) {
                secondaryContent()
            }
        }
    } else {
        // Regular layout for phones or portrait orientation
        Column(modifier = modifier.fillMaxSize()) {
            // Show either primary or secondary content based on parameter
            if (showSecondaryOnly) {
                secondaryContent()
            } else {
                primaryContent()
            }
        }
    }
}
