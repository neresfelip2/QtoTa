package br.com.qtota.ui.navigation

import android.net.Uri
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.FavoriteBorder
import androidx.compose.material.icons.outlined.Home
import androidx.compose.material.icons.outlined.Menu
import androidx.compose.material.icons.outlined.Search
import androidx.compose.ui.graphics.vector.ImageVector
import br.com.qtota.data.remote.store.StoreResponse
import br.com.qtota.ui.screen.store_list.ViewMode
import com.google.gson.Gson

sealed class AppRoute(val route: String, val icon: ImageVector? = null) {

    /* Bottom Navigation Routes */
    object MainNav: AppRoute("main_nav")
    object Home: AppRoute("home", Icons.Outlined.Home)
    object SearchProduct : AppRoute(
        "search_product?query={query}&store={store}",
        Icons.Outlined.Search
    ) {
        const val BASE_ROUTE = "search_product"
        const val ARG_QUERY  = "query"
        const val ARG_STORE = "store"

        fun createRoute(query: String? = null, store: StoreResponse? = null): String {
            val params = mutableListOf<String>()

            query
                .takeIf { !it.isNullOrBlank() }
                ?.let { params += "$ARG_QUERY=${Uri.encode(it)}" }

            store
                ?.let {
                    val json = Gson().toJson(store)
                    val encoded = Uri.encode(json)
                    params += "$ARG_STORE=${Uri.encode(encoded)}"
                }

            return if (params.isEmpty()) {
                BASE_ROUTE
            } else {
                "$BASE_ROUTE?${params.joinToString("&")}"
            }
        }
    }

    object SavedOffers: AppRoute("saved_offers", Icons.Outlined.FavoriteBorder)
    object Menu: AppRoute("menu", Icons.Outlined.Menu)
    object StoreList: AppRoute("home/store_list/{view_mode}") {

        internal const val BASE_ROUTE = "home/store_list"

        internal const val ARG_VIEW_MODE = "view_mode"

        internal fun createRoute(viewMode: ViewMode) : String {
            return "${BASE_ROUTE}/${viewMode.name}"
        }


    }


    object RequestLocation: AppRoute("request_location")
    object Login: AppRoute("login")
    object ProductDetails: AppRoute("product_details/{productId}") {

        internal const val ARG_PRODUCT_ID = "productId"

        internal fun productId(id: Long): String {
            return "product_details/$id"
        }
    }

}