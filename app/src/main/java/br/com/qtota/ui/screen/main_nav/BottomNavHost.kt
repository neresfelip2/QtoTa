package br.com.qtota.ui.screen.main_nav

import androidx.activity.compose.BackHandler
import androidx.compose.material3.DrawerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.navigation.NamedNavArgument
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import br.com.qtota.ui.navigation.AppRoute
import br.com.qtota.ui.screen.home.HomeScreen
import br.com.qtota.ui.screen.map.MapScreen
import br.com.qtota.ui.screen.menu.MenuScreen
import br.com.qtota.ui.screen.saved_offers.SavedItemsScreen
import br.com.qtota.ui.screen.search_product.SearchProductScreen
import br.com.qtota.ui.screen.store_detail.StoreDetailScreen
import br.com.qtota.ui.screen.store_list.StoreListScreen
import kotlinx.coroutines.launch

@Composable
internal fun BottomNavHost(
    bottomNavController: NavHostController,
    navController: NavHostController,
    drawerState: DrawerState,
) {
    NavHost(
        bottomNavController,
        AppRoute.Home.route
    ) {

        drawerComposable(
            AppRoute.Home.route,
            drawerState,
        ) {
            HomeScreen(navController, bottomNavController)
        }
        drawerComposable(
            AppRoute.SearchProduct.route,
            drawerState,
            listOf(
                navArgument(AppRoute.SearchProduct.ARG_QUERY) {
                    type = NavType.StringType
                    nullable = true
                    defaultValue = null
                }
            ),
        ) {
            SearchProductScreen(navController)
        }
        drawerComposable(AppRoute.SavedOffers.route, drawerState) { SavedItemsScreen(navController) }
        drawerComposable(AppRoute.Menu.route, drawerState) { MenuScreen(navController) }
        drawerComposable(AppRoute.StoreList.route, drawerState) { StoreListScreen(bottomNavController) }

        drawerComposable(AppRoute.StoreDetail.route,
            drawerState,
            arguments = listOf(
                navArgument(AppRoute.StoreDetail.ARG_ID) {
                    type = NavType.LongType
                }
            )
        ) {
            StoreDetailScreen(navController, bottomNavController)
        }

        drawerComposable(
            AppRoute.Map.route,
            drawerState,
            arguments = listOf(
                navArgument(AppRoute.Map.ARG_STORE_ID) {
                    type = NavType.LongType
                }
            )
        ) { MapScreen(bottomNavController) }

    }

}

private fun NavGraphBuilder.drawerComposable(
    route: String,
    drawerState: DrawerState,
    arguments: List<NamedNavArgument> = emptyList(),
    content: @Composable () -> Unit
) {

    composable(route, arguments) {
        val scope = rememberCoroutineScope()

        BackHandler(drawerState.isOpen) {
            scope.launch {
                drawerState.close()
            }
        }
        content()
    }
}