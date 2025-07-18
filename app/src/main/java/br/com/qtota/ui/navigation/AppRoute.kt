package br.com.qtota.ui.navigation

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Home
import androidx.compose.material.icons.outlined.Menu
import androidx.compose.material.icons.outlined.Search
import androidx.compose.ui.graphics.vector.ImageVector

sealed class AppRoute(val route: String, val icon: ImageVector? = null) {

    /* Bottom Navigation Routes */
    object MainNav: AppRoute("main_nav")
    object Home: AppRoute("home", Icons.Outlined.Home)
    object SearchProduct : AppRoute(
        route = "search_product?query={query}",
        icon  = Icons.Outlined.Search
    ) {
        const val BASE_ROUTE = "search_product"
        const val ARG_QUERY  = "query"

        fun createRoute(query: String?): String {
            return if (!query.isNullOrBlank()) {
                "$BASE_ROUTE?${ARG_QUERY}=${query}"
            } else {
                BASE_ROUTE
            }
        }
    }
    object Menu: AppRoute("menu", icon = Icons.Outlined.Menu)
    object StoreList: AppRoute("store_list")
    object StoreProducts: AppRoute("store_products")


    object RequestLocation: AppRoute("request_location")
    object Login: AppRoute("login")
    object ProductDetails: AppRoute("product_details/{productId}") {

        internal const val ARG_PRODUCT_ID = "productId"

        internal fun productId(id: Long): String {
            return "product_details/$id"
        }
    }
    object SavedOffers: AppRoute("saved_offers")
}