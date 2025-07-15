package br.com.qtota.ui.navigation

import android.annotation.SuppressLint
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Home
import androidx.compose.material.icons.outlined.Menu
import androidx.compose.material.icons.outlined.Search
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import androidx.navigation.compose.currentBackStackEntryAsState
import br.com.qtota.ui.theme.DefaultColor

@SuppressLint("UnusedBoxWithConstraintsScope")
@Composable
internal fun BottomNavBar(navController: NavHostController) {

    val currentRoute = navController
        .currentBackStackEntryAsState().value?.destination?.route

    NavigationBar(modifier = Modifier.height(112.dp)) {
        NavigationBarItem(
            selected = currentRoute == AppRoutes.Home.route,
            colors = NavigationBarItemDefaults.colors(
                selectedIconColor = DefaultColor,
                unselectedIconColor = Color.Gray,
                indicatorColor = Color.Transparent
            ),
            onClick = {
                if(currentRoute != AppRoutes.Home.route) navController.navigate(AppRoutes.Home.route) {
                    popUpTo(navController.graph.startDestinationId) {
                        saveState = true
                    }
                    launchSingleTop = true
                    restoreState = true
                }
            },
            icon = { Icon(Icons.Outlined.Home, null) },
        )
        NavigationBarItem(
            selected = currentRoute == AppRoutes.ListProduct.route,
            colors = NavigationBarItemDefaults.colors(
                selectedIconColor = DefaultColor,
                unselectedIconColor = Color.Gray,
                indicatorColor = Color.Transparent
            ),
            onClick = {
                if(currentRoute != AppRoutes.ListProduct.route) navController.navigate(AppRoutes.ListProduct.route) {
                    popUpTo(navController.graph.startDestinationId) {
                        saveState = true
                    }
                    launchSingleTop = true
                    restoreState = true
                }
            },
            icon = { Icon(Icons.Outlined.Search, null) }
        )
        Spacer(Modifier.weight(1f, fill = true))
        NavigationBarItem(
            selected = false,
            colors = NavigationBarItemDefaults.colors(
                selectedIconColor = DefaultColor,
                unselectedIconColor = Color.Gray,
                indicatorColor = Color.Transparent
            ),
            onClick = {},
            icon = {}
        )
        NavigationBarItem(
            selected = currentRoute == AppRoutes.Menu.route,
            colors = NavigationBarItemDefaults.colors(
                selectedIconColor = DefaultColor,
                unselectedIconColor = Color.Gray,
                indicatorColor = Color.Transparent
            ),
            onClick = {
                if(currentRoute != AppRoutes.Menu.route) navController.navigate(AppRoutes.Menu.route) {
                    popUpTo(navController.graph.startDestinationId) {
                        saveState = true
                    }
                    launchSingleTop = true
                    restoreState = true
                }
            },
            icon = { Icon(Icons.Outlined.Menu, null) }
        )
    }

}