package br.com.qtota.ui.navigation

import android.location.Location

sealed class AppRoutes(val route: String) {
    object Login: AppRoutes("login")
    object Home : AppRoutes("home")
    object Settings: AppRoutes("settings")
    object ProductDetails : AppRoutes("product_details/{productId}/{latitude}/{longitude}") {

        internal const val ARG_PRODUCT_ID = "productId"
        internal const val ARG_LATITUDE = "latitude"
        internal const val ARG_LONGITUDE = "longitude"

        internal fun productId(id: Long, location: Location): String {
            return "product_details/$id/${location.latitude}/${location.longitude}"
        }
    }
    object SavedOffers: AppRoutes("saved_offers")
}