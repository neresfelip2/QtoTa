package br.com.qtota.ui.navigation

import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import androidx.navigation.compose.currentBackStackEntryAsState
import br.com.qtota.ui.theme.DefaultColor

@Composable
internal fun BottomNavBar(navController: NavHostController) {

    val currentRoute = navController
        .currentBackStackEntryAsState().value?.destination?.route

    NavigationBar(Modifier.heightIn(56.dp, 112.dp).windowInsetsPadding(WindowInsets.navigationBars)) {
        NavItem(AppRoute.Home.route, AppRoute.Home.icon, currentRoute, navController)
        NavItem(AppRoute.SearchProduct.createRoute(), AppRoute.SearchProduct.icon, currentRoute, navController)
        Spacer(Modifier.weight(1f, fill = true))
        NavItem(AppRoute.SavedOffers.route, AppRoute.SavedOffers.icon, currentRoute, navController)
        NavItem(AppRoute.Menu.route, AppRoute.Menu.icon, currentRoute, navController)
    }

}

@Composable
internal fun RowScope.NavItem(route: String, icon: ImageVector?, currentRoute: String?, navController: NavHostController) {

    val selected = currentRoute?.startsWith(route) == true

    NavigationBarItem(
        selected = selected,
        colors = NavigationBarItemDefaults.colors(
            selectedIconColor = DefaultColor,
            unselectedIconColor = Color.Gray,
            indicatorColor = Color.Transparent
        ),
        onClick = {
            if(!selected) navController.navigate(route) {
                popUpTo(navController.graph.startDestinationId) {
                    saveState = true
                }
                launchSingleTop = true
                restoreState = true
            }
        },
        icon = { icon?.let { Icon(it, null) } },
    )
}