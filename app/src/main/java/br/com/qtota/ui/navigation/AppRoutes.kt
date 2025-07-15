package br.com.qtota.ui.navigation

sealed class AppRoutes(val route: String) {

    object RequestLocation: AppRoutes("request_location")
    object Login: AppRoutes("login")

    object MainNav: AppRoutes("main_nav")
    object Home: AppRoutes("home")
    object ListProduct: AppRoutes("list_product")
    object Menu: AppRoutes("menu")
    object ProductDetails: AppRoutes("product_details/{productId}") {

        internal const val ARG_PRODUCT_ID = "productId"

        internal fun productId(id: Long): String {
            return "product_details/$id"
        }
    }
    object SavedOffers: AppRoutes("saved_offers")
}