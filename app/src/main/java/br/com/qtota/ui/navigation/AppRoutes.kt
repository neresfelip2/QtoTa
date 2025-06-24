package br.com.qtota.ui.navigation

sealed class AppRoutes(val route: String) {
    object Login: AppRoutes("login")
    object Home : AppRoutes("home")
    object Settings: AppRoutes("settings")
    object ProductDetails: AppRoutes("product_details")
    object SavedOffers: AppRoutes("saved_offers")
}