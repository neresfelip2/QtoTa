package br.com.qtota.ui.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.navArgument
import br.com.qtota.ui.screen.MainNavigationScreen
import br.com.qtota.ui.screen.login.LoginScreen
import br.com.qtota.ui.screen.product_details.ProductDetailsScreen
import br.com.qtota.ui.screen.request_location.RequestLocationScreen
import br.com.qtota.ui.screen.saved_offers.SavedOffersScreen

@Composable
internal fun AppNavHost(navController: NavHostController, startDestination: String) {

    NavHost(navController, startDestination = startDestination) {

        animatedComposable(AppRoutes.Login.route) { LoginScreen(navController) }

        animatedComposable(AppRoutes.RequestLocation.route) { RequestLocationScreen(navController) }

        animatedComposable(AppRoutes.MainNav.route) { MainNavigationScreen(navController)}

        animatedComposable(
            AppRoutes.ProductDetails.route,
            arguments = listOf(
                navArgument(AppRoutes.ProductDetails.ARG_PRODUCT_ID) {
                    type = NavType.LongType
                }
            )
        ) { ProductDetailsScreen(navController) }

        animatedComposable(AppRoutes.SavedOffers.route) { SavedOffersScreen(navController) }
    }
}