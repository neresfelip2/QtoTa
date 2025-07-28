package br.com.qtota.ui.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.navArgument
import br.com.qtota.ui.screen.auth.AuthScreen
import br.com.qtota.ui.screen.account_settings.AccountSettingsScreen
import br.com.qtota.ui.screen.product_detail.ProductDetailsScreen
import br.com.qtota.ui.screen.main_navigation.MainNavigationScreen

@Composable
internal fun AppNavHost(navController: NavHostController, startDestination: String) {

    NavHost(navController, startDestination = startDestination) {

        animatedComposable(AppRoute.Login.route) { AuthScreen(navController) }

        animatedComposable(AppRoute.Main.route) { MainNavigationScreen(navController) }

        animatedComposable(
            AppRoute.ProductDetails.route,
            arguments = listOf(
                navArgument(AppRoute.ProductDetails.ARG_PRODUCT_ID) {
                    type = NavType.LongType
                }
            )
        ) { ProductDetailsScreen(navController) }

        animatedComposable(AppRoute.AccountSettings.route) {
            AccountSettingsScreen(navController)
        }

    }
}