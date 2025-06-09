package com.testcityapp.presentation.navigation

/**
 * Sealed class representing all possible navigation routes in the app
 */
sealed class AppRoute(val route: String) {
    object Splash : AppRoute("splash")
    object Main : AppRoute("main")
    object Details : AppRoute("details/{emissionId}") {
        fun createRoute(emissionId: Long): String = "details/$emissionId"
    }
    
    companion object {
        fun fromRoute(route: String?): AppRoute {
            return when {
                route == Splash.route -> Splash
                route == Main.route -> Main
                route?.startsWith("details/") == true -> Details
                else -> Splash // Default route
            }
        }
    }
}
