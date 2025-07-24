package br.com.qtota.ui.screen.main_nav

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import br.com.qtota.ui.navigation.AppRoute
import br.com.qtota.ui.screen.home.HomeScreen
import br.com.qtota.ui.screen.menu.MenuScreen
import br.com.qtota.ui.screen.saved_offers.SavedItemsScreen
import br.com.qtota.ui.screen.search_product.SearchProductScreen
import br.com.qtota.ui.screen.store_detail.StoreDetailScreen
import br.com.qtota.ui.screen.store_list.StoreListScreen
import br.com.qtota.ui.screen.store_list.ViewMode

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
        ) {
            SearchProductScreen(navController)
        }
        composable(AppRoute.SavedOffers.route) { SavedItemsScreen(navController) }
        composable(AppRoute.Menu.route) { MenuScreen(navController) }
        composable(AppRoute.StoreList.route,
            arguments = listOf(
                navArgument(AppRoute.StoreList.ARG_VIEW_MODE) {
                    type = NavType.StringType
                }
            )
        ) { navBackStackEntry ->
            val arg = navBackStackEntry.arguments?.getString(AppRoute.StoreList.ARG_VIEW_MODE)!!
            StoreListScreen(bottomNavController, ViewMode.valueOf(arg))
        }

        composable(AppRoute.StoreDetail.route,
            arguments = listOf(
                navArgument(AppRoute.StoreDetail.ARG_ID) {
                    type = NavType.LongType
                }
            )
        ) {
            StoreDetailScreen()
        }

    }

}