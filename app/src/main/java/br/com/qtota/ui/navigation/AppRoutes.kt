package br.com.qtota.ui.navigation

sealed class AppRoutes(val route: String) {
    object Login: AppRoutes("login")
    object Home : AppRoutes("home")
    object Settings: AppRoutes("settings")
    object ProductDetails : AppRoutes("product_details/{productId}") {

        internal const val ARG_PRODUCT_ID = "productId"

        internal fun productId(id: Long): String {
            return "product_details/$id"
        }
    }
    object SavedOffers: AppRoutes("saved_offers")
}