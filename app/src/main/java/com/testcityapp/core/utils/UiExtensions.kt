package com.testcityapp.core.utils

import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalConfiguration
import android.content.res.Configuration

@Composable
fun isSideBySideMode(): Boolean {
    val configuration = LocalConfiguration.current
    return configuration.screenWidthDp >= 600 && configuration.orientation == Configuration.ORIENTATION_LANDSCAPE
}
