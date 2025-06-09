package com.testcityapp.presentation.navigation

import android.util.Log
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
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
import com.testcityapp.presentation.details.DetailsScreen
import com.testcityapp.presentation.main.MainScreen
import com.testcityapp.presentation.main.MainViewModel
import com.testcityapp.presentation.splash.SplashScreen
import com.testcityapp.core.utils.isSideBySideMode

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
                onNavigateToMain = { navController.navigate(AppRoute.Main.route) }
            )

            // Navigate to main screen when first emission happens
            if (emissions.isNotEmpty()) {
                navController.navigate(AppRoute.Main.route) {
                    popUpTo(AppRoute.Splash.route) { inclusive = true }
                }
            }
        }

        composable(AppRoute.Main.route) {
            MainScreen(
                emissions = emissions,
                onEmissionClick = { emission ->
                    // Debug log to check emission ID
                    Log.d("Navigating"," to details for emission: ${emission.id}, city: ${emission.city}")
                    // Make sure we're using a valid ID for navigation
                    if (emission.id > 0) {
                        navController.navigate(AppRoute.Details.createRoute(emission.id))
                    } else {
                        // Find the emission with the same city name in the emissions list
                        val matchingEmission = emissions.find { it.city == emission.city }
                        if (matchingEmission != null) {
                            navController.navigate(AppRoute.Details.createRoute(matchingEmission.id))
                        }
                    }
                }
            )
        }

        composable(
            route = AppRoute.Details.route,
            arguments = listOf(navArgument("emissionId") { type = NavType.LongType })
        ) { backStackEntry ->
            val emissionId = backStackEntry.arguments?.getLong("emissionId") ?: 0
            val emission = emissions.find { it.id == emissionId }
            Log.d("Navigating"," to details for emission: ${emission}, city: -----")

            emission?.let {
                // Schedule WorkManager toast
                mainViewModel.scheduleWelcomeToast(emission.city)

                // Handle tablet split view in landscape
                if (isSideBySideMode()) {
                    Row(modifier = Modifier.fillMaxSize()) {
                        MainScreen(
                            emissions = emissions,
                            onEmissionClick = { clickedEmission ->
                                // Make sure we navigate only if the emission is still in the list
                                val targetEmission = emissions.find { it.id == clickedEmission.id }
                                if (targetEmission != null) {
                                    navController.navigate(AppRoute.Details.createRoute(targetEmission.id))
                                }
                            },
                            modifier = Modifier.weight(1f)
                        )
                        DetailsScreen(
                            emission = emission,
                            modifier = Modifier.weight(1f)
                        )
                    }
                } else {
                    DetailsScreen(emission = emission)
                }
            }
        }
    }
}
