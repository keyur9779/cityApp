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
import com.testcityapp.presentation.components.VerticalDivider
import com.testcityapp.presentation.details.DetailsScreen
import com.testcityapp.presentation.main.MainScreen
import com.testcityapp.presentation.main.MainViewModel
import com.testcityapp.presentation.splash.SplashScreen

@Composable
fun AppNavHost(
    navController: NavHostController = rememberNavController(),
    startDestination: String = AppRoute.Splash.route
) {
    // Use hiltViewModel() which is configuration-change aware
    val mainViewModel: MainViewModel = hiltViewModel()
    val emissions by mainViewModel.emissions.collectAsState()
    val currentBackStackEntry by navController.currentBackStackEntryAsState()

    // Observe lifecycle to control emission production, but only do this once at the NavHost level
    val lifecycleOwner = LocalLifecycleOwner.current
    DisposableEffect(Unit) { // Use Unit instead of lifecycleOwner to prevent recreation on config changes
        Log.d("AppNavHost", "Adding lifecycle observer to MainViewModel")
        lifecycleOwner.lifecycle.addObserver(mainViewModel)
        onDispose {
            Log.d("AppNavHost", "Removing lifecycle observer from MainViewModel")
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

                // Track if we've already scheduled the welcome toast for this emission
                val toastScheduled = remember(emission.id) { mutableStateOf(false) }
                
                // Update the selected emission when the navigation argument changes
                // Use the emission.id as the key to prevent re-running on configuration changes
                LaunchedEffect(emission.id) {
                    if (!toastScheduled.value) {
                        mainViewModel.scheduleWelcomeToast(emission.city)
                        toastScheduled.value = true
                    }
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
                                        mainViewModel.scheduleWelcomeToast(targetEmission.city)

                                    }
                                },
                                // Add additional parameter to highlight the currently selected item
                                selectedEmissionId = selectedEmissionState.value.id,
                                modifier = Modifier.fillMaxSize()
                            )
                        }

                        // Add a visual separator between panels
                        VerticalDivider()

                        // Detail view (map and city details)
                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .fillMaxSize()
                        ) {
                            // Use remember with the emission to prevent unnecessary recompositions
                            val currentEmission = remember(selectedEmissionState.value) {
                                selectedEmissionState.value
                            }
                            
                            DetailsScreen(
                                emission = currentEmission,
                                isInSplitView = true,
                                modifier = Modifier.fillMaxSize()
                            )
                        }
                    }
                } else {
                    // Use remember with emission.id to prevent unnecessary recompositions on orientation changes
                    val stableEmission = remember(emission.id) { emission }
                    DetailsScreen(emission = stableEmission)
                }
            }
        }
    }
}
