package com.testcityapp.presentation.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

/**
 * A custom circular progress indicator component with configurable size and color.
 *
 * @param modifier Modifier to be applied to the component
 * @param color The color of the progress indicator (defaults to the primary color from the theme)
 * @param size The size of the progress indicator
 */
@Composable
fun AppCircularProgressIndicator(
    modifier: Modifier = Modifier,
    color: Color = MaterialTheme.colorScheme.primary,
    size: Dp = 48.dp
) {
    CircularProgressIndicator(
        color = color,
        modifier = modifier.size(size)
    )
}

/**
 * A custom text component with configurable style, color, alignment, and other properties.
 *
 * @param text The text to display
 * @param modifier Modifier to be applied to the component
 * @param style The text style to apply (defaults to bodyLarge from the theme)
 * @param color The color of the text (defaults to onSurface from the theme)
 * @param fontWeight The weight of the font (optional)
 * @param textAlign The alignment of the text (optional)
 * @param maxLines Maximum number of lines (defaults to Int.MAX_VALUE)
 */
@Composable
fun AppText(
    text: String,
    modifier: Modifier = Modifier,
    style: TextStyle = MaterialTheme.typography.bodyLarge,
    color: Color = MaterialTheme.colorScheme.onSurface,
    fontWeight: FontWeight? = null,
    textAlign: TextAlign? = null,
    maxLines: Int = Int.MAX_VALUE
) {
    Text(
        text = text,
        modifier = modifier,
        style = style,
        color = color,
        fontWeight = fontWeight,
        textAlign = textAlign,
        maxLines = maxLines
    )
}

/**
 * A title text component with predefined style for titles.
 *
 * @param text The text to display
 * @param modifier Modifier to be applied to the component
 * @param color The color of the text (defaults to onSurface from the theme)
 * @param textAlign The alignment of the text (optional)
 */
@Composable
fun AppTitleText(
    text: String,
    modifier: Modifier = Modifier,
    color: Color = MaterialTheme.colorScheme.onSurface,
    textAlign: TextAlign? = null
) {
    AppText(
        text = text,
        modifier = modifier,
        style = MaterialTheme.typography.titleLarge,
        fontWeight = FontWeight.Bold,
        color = color,
        textAlign = textAlign
    )
}

/**
 * A body text component with predefined style for body text.
 *
 * @param text The text to display
 * @param modifier Modifier to be applied to the component
 * @param color The color of the text (defaults to onSurfaceVariant from the theme)
 * @param textAlign The alignment of the text (optional)
 */
@Composable
fun AppBodyText(
    text: String,
    modifier: Modifier = Modifier,
    color: Color = MaterialTheme.colorScheme.onSurfaceVariant,
    textAlign: TextAlign? = null
) {
    AppText(
        text = text,
        modifier = modifier,
        style = MaterialTheme.typography.bodyMedium,
        color = color,
        textAlign = textAlign
    )
}

/**
 * A styled error text component
 *
 * @param message The error message to display
 * @param modifier Modifier to be applied to the component
 */
@Composable
fun ErrorText(
    message: String,
    modifier: Modifier = Modifier
) {
    AppText(
        text = message,
        modifier = modifier,
        color = MaterialTheme.colorScheme.error,
        style = MaterialTheme.typography.bodyMedium,
        textAlign = TextAlign.Center
    )
}

/**
 * A loading state component that shows a circular progress indicator with an optional message
 *
 * @param message Optional message to display while loading
 * @param modifier Modifier to be applied to the component
 */
@Composable
fun LoadingContent(
    message: String? = null,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        AppCircularProgressIndicator()

        if (!message.isNullOrBlank()) {
            Spacer(modifier = Modifier.height(16.dp))
            AppBodyText(
                text = message,
                textAlign = TextAlign.Center
            )
        }
    }
}

/**
 * A card component for displaying city information with custom styling
 *
 * @param title The title/heading text for the card
 * @param subtitle Optional subtitle text
 * @param backgroundColor Background color for the card
 * @param contentColor Text color for the card content
 * @param onClick Optional click handler for the card
 * @param modifier Modifier to be applied to the component
 * @param content Composable content to be displayed within the card
 */
@Composable
fun CityCard(
    title: String,
    subtitle: String? = null,
    backgroundColor: Color = MaterialTheme.colorScheme.surface,
    contentColor: Color = MaterialTheme.colorScheme.onSurface,
    onClick: (() -> Unit)? = null,
    modifier: Modifier = Modifier,
    content: @Composable () -> Unit = {}
) {
    val cardModifier = if (onClick != null) {
        modifier.clickable { onClick() }
    } else {
        modifier
    }

    Card(
        modifier = cardModifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp),
        elevation = CardDefaults.cardElevation(4.dp),
        colors = CardDefaults.cardColors(
            containerColor = backgroundColor
        )
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            AppTitleText(
                text = title,
                color = contentColor
            )

            if (subtitle != null) {
                Spacer(modifier = Modifier.height(4.dp))
                AppBodyText(
                    text = subtitle,
                    color = contentColor.copy(alpha = 0.7f)
                )
            }

            Spacer(modifier = Modifier.height(8.dp))

            content()
        }
    }
}
