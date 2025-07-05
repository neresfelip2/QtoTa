package br.com.qtota.ui.navigation

import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import br.com.qtota.ui.screen.home.HomeScreen
import br.com.qtota.ui.screen.login.LoginScreen
import br.com.qtota.ui.screen.product_details.ProductDetailsScreen
import br.com.qtota.ui.screen.saved_offers.SavedOffersScreen
import br.com.qtota.ui.screen.settings.SettingsScreen

@Composable
internal fun AppNavHost(navController: NavHostController, startDestination: String) {
    NavHost(navController, startDestination = startDestination) {
        composable(
            AppRoutes.Login.route,
            enterTransition = { slideInHorizontally() },
            exitTransition = { slideOutHorizontally(targetOffsetX = { -it }) },
        ) { LoginScreen(navController) }

        composable(AppRoutes.Home.route) { HomeScreen(navController) }

        composable(
            AppRoutes.ProductDetails.route,
            arguments = listOf(
                navArgument(AppRoutes.ProductDetails.ARG_PRODUCT_ID) { type = NavType.LongType }
            ),
            enterTransition = { slideInHorizontally() },
            popEnterTransition = { null },
            popExitTransition = { slideOutHorizontally() }
        ) { ProductDetailsScreen(navController) }

        composable(
            AppRoutes.SavedOffers.route,
            enterTransition = { slideInHorizontally() },
            popExitTransition = { slideOutHorizontally() }
        ) { SavedOffersScreen(navController) }

        composable(
            AppRoutes.Settings.route,
            enterTransition = { slideInHorizontally() },
            popExitTransition = { slideOutHorizontally() },
        ) { SettingsScreen(navController) }

    }
}