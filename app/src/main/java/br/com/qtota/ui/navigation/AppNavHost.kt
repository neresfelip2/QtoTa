package br.com.qtota.ui.navigation

import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.runtime.Composable
import androidx.navigation.NamedNavArgument
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import br.com.qtota.ui.screen.home.HomeScreen
import br.com.qtota.ui.screen.list_product.ListProductScreen
import br.com.qtota.ui.screen.login.LoginScreen
import br.com.qtota.ui.screen.product_details.ProductDetailsScreen
import br.com.qtota.ui.screen.saved_offers.SavedOffersScreen
import br.com.qtota.ui.screen.settings.SettingsScreen

@Composable
internal fun AppNavHost(navController: NavHostController, startDestination: String) {
    NavHost(navController, startDestination = startDestination) {

        animatedComposable(AppRoutes.Home.route) { HomeScreen(navController) }

        animatedComposable(AppRoutes.Login.route) { LoginScreen(navController) }

        animatedComposable(AppRoutes.ListProduct.route) { ListProductScreen(navController) }

        animatedComposable(
            AppRoutes.ProductDetails.route,
            listOf(
                navArgument(AppRoutes.ProductDetails.ARG_PRODUCT_ID) {
                    type = NavType.LongType
                }
            )
        ) { ProductDetailsScreen(navController) }

        animatedComposable(AppRoutes.SavedOffers.route) { SavedOffersScreen(navController) }

        animatedComposable(AppRoutes.Settings.route) { SettingsScreen(navController) }
    }
}

private fun NavGraphBuilder.animatedComposable(
    route: String,
    arguments: List<NamedNavArgument> = emptyList(),
    content: @Composable () -> Unit
) {
    composable(
        route = route,
        arguments = arguments,
        enterTransition = { slideInHorizontally(initialOffsetX = { it }) },
        popEnterTransition = { null },
        popExitTransition = { slideOutHorizontally(targetOffsetX = { it }) },
        content = { content() }
    )
}