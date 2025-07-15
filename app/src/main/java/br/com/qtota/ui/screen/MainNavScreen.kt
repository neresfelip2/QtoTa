package br.com.qtota.ui.screen

import android.annotation.SuppressLint
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Notifications
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.FabPosition
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.ModalNavigationDrawer
import androidx.compose.material3.Scaffold
import androidx.compose.material3.rememberDrawerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import br.com.qtota.MainViewModel
import br.com.qtota.R
import br.com.qtota.ui.components.ConfirmDialog
import br.com.qtota.ui.components.DrawerContent
import br.com.qtota.ui.components.Toolbar
import br.com.qtota.ui.navigation.AppRoutes
import br.com.qtota.ui.navigation.BottomNavBar
import br.com.qtota.ui.screen.home.HomeScreen
import br.com.qtota.ui.screen.home.SendFlyerDialog
import br.com.qtota.ui.screen.list_product.ListProductScreen
import br.com.qtota.ui.screen.menu.MenuScreen
import br.com.qtota.ui.theme.DefaultColor
import kotlinx.coroutines.launch

@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@Composable
internal fun MainNavigationScreen(navController: NavHostController) {

    val viewModel: MainViewModel = hiltViewModel()
    val bottomNavController = rememberNavController()

    // Drawer
    val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)
    val scope = rememberCoroutineScope()

    // SendFlyerDialog
    var showLoginDialog by remember { mutableStateOf(false) }
    var showSendFlyerDialog by remember { mutableStateOf(false) }

    BackHandler(drawerState.isOpen) {
        scope.launch { drawerState.close() }
    }

    Scaffold(
        topBar = {
            Toolbar(
                stringResource(R.string.app_name),
                backButtonEnabled = null,
                Icons.Outlined.Notifications to {
                    scope.launch {
                        if (drawerState.isClosed) {
                            drawerState.open()
                        } else {
                            drawerState.close()
                        }
                    }
                }
            )
        },
    ) { topBarPadding ->
        CompositionLocalProvider(LocalLayoutDirection provides LayoutDirection.Rtl) {
            ModalNavigationDrawer(
                drawerState = drawerState,
                drawerContent = { DrawerContent() },
            ) {
                CompositionLocalProvider(LocalLayoutDirection provides LayoutDirection.Ltr) {
                        Scaffold(
                            bottomBar = { BottomNavBar(bottomNavController) },
                            floatingActionButton = {
                                FloatingActionButton(
                                    {
                                        viewModel.checkIfLogged { isLogged ->
                                            if (isLogged) {
                                                showSendFlyerDialog = true
                                            } else {
                                                showLoginDialog = true
                                            }
                                        }
                                    },
                                    modifier = Modifier.offset(y = (64).dp),
                                    contentColor = Color.White,
                                    containerColor = DefaultColor,
                                ) {
                                    Icon(
                                        painterResource(R.drawable.ic_send_flyer),
                                        null
                                    )
                                }
                            },
                            floatingActionButtonPosition = FabPosition.Center,
                        ) { bottomBarPadding ->
                            Box(Modifier.padding(top = topBarPadding.calculateTopPadding(), bottom = bottomBarPadding.calculateBottomPadding())) {
                                NavHost(
                                    bottomNavController,
                                    startDestination = AppRoutes.Home.route
                                ) {
                                    composable(AppRoutes.Home.route) { HomeScreen(navController) }
                                    composable(AppRoutes.ListProduct.route) { ListProductScreen(navController) }
                                    composable(AppRoutes.Menu.route) { MenuScreen(navController) }
                                }
                            }
                        }
                    }

            }

        }

        if(showLoginDialog) {
            ConfirmDialog(
                text = stringResource(R.string.send_flyer_dialog),
                onDismiss = { showLoginDialog = false },
                onConfirm = {
                    showLoginDialog = false
                    navController.navigate(AppRoutes.Login.route)
                },
                confirmText = stringResource(R.string.log_in)
            )
        }

        if(showSendFlyerDialog) {
            SendFlyerDialog { showSendFlyerDialog = false }
        }

    }

}