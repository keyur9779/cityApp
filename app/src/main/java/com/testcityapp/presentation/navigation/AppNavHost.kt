package com.testcityapp.presentation.navigation

import android.util.Log
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.testcityapp.core.utils.isSideBySideMode
import com.testcityapp.presentation.details.DetailsScreen
import com.testcityapp.presentation.main.MainScreen
import com.testcityapp.presentation.main.MainViewModel
import com.testcityapp.presentation.splash.SplashScreen

@Composable
fun AppNavHost(
    navController: NavHostController = rememberNavController(),
    startDestination: String = AppRoute.Splash.route
) {
    val mainViewModel: MainViewModel = hiltViewModel()
    val emissions by mainViewModel.emissions.collectAsState()
    val currentBackStackEntry by navController.currentBackStackEntryAsState()

    // Observe lifecycle to control emission production
    val lifecycleOwner = LocalLifecycleOwner.current
    DisposableEffect(lifecycleOwner) {
        lifecycleOwner.lifecycle.addObserver(mainViewModel)
        onDispose {
            lifecycleOwner.lifecycle.removeObserver(mainViewModel)
        }
    }

    NavHost(
        navController = navController,
        startDestination = startDestination
    ) {
        composable(AppRoute.Splash.route) {
            SplashScreen(
                onNavigateToMain = {
                    navController.navigate(AppRoute.Main.route) {
                        popUpTo(AppRoute.Splash.route) { inclusive = true }
                    }
                }
            )

        }

        composable(AppRoute.Main.route) {
            MainScreen(
                emissions = emissions,
                onEmissionClick = { emission ->
                    // Debug log to check emission ID
                    Log.d(
                        "Navigating",
                        " to details for emission: ${emission.id}, city: ${emission.city}"
                    )

                    // Skip navigation in side-by-side mode to avoid creating duplicate layouts

                    navController.navigate(AppRoute.Details.createRoute(emission.id))
                }

            )
        }

        composable(
            route = AppRoute.Details.route,
            arguments = listOf(navArgument("emissionId") { type = NavType.LongType })
        ) { backStackEntry ->
            val emissionId = backStackEntry.arguments?.getLong("emissionId") ?: 0
            val emission = emissions.find { it.id == emissionId }
            Log.d("Navigating", " to details for emission: ${emission}, city: -----")

            emission?.let {
                // Schedule WorkManager toast

                // Create a persistent state that survives recompositions
                val selectedEmissionState = remember {
                    mutableStateOf(emission)
                }

                // Update the selected emission when the navigation argument changes
                LaunchedEffect(emission) {
                    selectedEmissionState.value = emission
                    mainViewModel.scheduleWelcomeToast(emission.city)
                }

                // Use our adaptive layout for better responsiveness
                if (isSideBySideMode()) {
                    Row(modifier = Modifier.fillMaxSize()) {
                        // Master view (list of cities)
                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .fillMaxSize()
                        ) {
                            MainScreen(
                                emissions = emissions,
                                onEmissionClick = { clickedEmission ->
                                    // Find the emission and update the state
                                    val targetEmission =
                                        emissions.find { it.id == clickedEmission.id }
                                    if (targetEmission != null) {
                                        selectedEmissionState.value = targetEmission
                                        // Log selection without navigation
                                        Log.d(
                                            "SplitView",
                                            "Selected emission: ${targetEmission.city}"
                                        )
                                    }
                                },
                                // Add additional parameter to highlight the currently selected item
                                selectedEmissionId = selectedEmissionState.value.id,
                                modifier = Modifier.fillMaxSize()
                            )
                        }

                        // Add a visual separator between panels
                        com.testcityapp.presentation.components.VerticalDivider()

                        // Detail view (map and city details)
                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .fillMaxSize()
                        ) {
                            DetailsScreen(
                                emission = selectedEmissionState.value,
                                isInSplitView = true,
                                modifier = Modifier.fillMaxSize()
                            )
                        }
                    }
                } else {
                    DetailsScreen(emission = emission)
                }
            }
        }
    }
}
