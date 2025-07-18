package br.com.qtota.ui.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import br.com.qtota.data.remote.store.StoreResponse
import br.com.qtota.ui.screen.home.HomeScreen
import br.com.qtota.ui.screen.menu.MenuScreen
import br.com.qtota.ui.screen.search_product.SearchProductScreen
import br.com.qtota.ui.screen.store_list.StoreListScreen
import br.com.qtota.ui.screen.store_products.StoreProductsScreen

@Composable
internal fun BottomNavHost(bottomNavController: NavHostController, navController: NavHostController) {

    NavHost(
        bottomNavController,
        AppRoute.Home.route
    ) {
        composable(AppRoute.Home.route) { HomeScreen(navController, bottomNavController) }
        composable(
            AppRoute.SearchProduct.route,
            arguments = listOf(
                navArgument(AppRoute.SearchProduct.ARG_QUERY) {
                    type = NavType.StringType
                    nullable = true
                    defaultValue = null
                }
            )
        ) { backStack ->
            val query = backStack.arguments?.getString(AppRoute.SearchProduct.ARG_QUERY)
            SearchProductScreen(navController, query)
        }
        composable(AppRoute.Menu.route) { MenuScreen(navController) }
        composable(AppRoute.StoreList.route) { StoreListScreen(bottomNavController) }
        composable(AppRoute.StoreProducts.route) { StoreProductsScreen(StoreResponse(
            id = 0,
            name = "Testando",
            branch = "TODO()",
            distance = 1234,
            logo = null
        ), bottomNavController, navController) }
    }

}