package br.com.qtota.ui.navigation

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Home
import androidx.compose.material.icons.outlined.Menu
import androidx.compose.material.icons.outlined.Search
import androidx.compose.ui.graphics.vector.ImageVector

sealed class AppRoute(val route: String, val icon: ImageVector? = null) {

    object RequestLocation: AppRoute("request_location")
    object Login: AppRoute("login")

    object MainNav: AppRoute("main_nav")
    object Home: AppRoute("home", Icons.Outlined.Home)
    object ListProduct: AppRoute("list_product", Icons.Outlined.Search)
    object Menu: AppRoute("menu", Icons.Outlined.Menu)
    object ProductDetails: AppRoute("product_details/{productId}") {

        internal const val ARG_PRODUCT_ID = "productId"

        internal fun productId(id: Long): String {
            return "product_details/$id"
        }
    }
    object SavedOffers: AppRoute("saved_offers")
}