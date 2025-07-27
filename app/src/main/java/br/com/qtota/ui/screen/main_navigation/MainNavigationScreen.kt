package br.com.qtota.ui.screen.main_navigation

import android.annotation.SuppressLint
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Notifications
import androidx.compose.material3.DrawerState
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.FabPosition
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.ModalNavigationDrawer
import androidx.compose.material3.Scaffold
import androidx.compose.material3.rememberDrawerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.collectAsState
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
import androidx.navigation.compose.rememberNavController
import br.com.qtota.R
import br.com.qtota.ui.components.ConfirmDialog
import br.com.qtota.ui.components.Toolbar
import br.com.qtota.ui.navigation.AppRoute
import br.com.qtota.ui.navigation.BottomNavBar
import br.com.qtota.ui.screen.request_location.LoadingLocation
import br.com.qtota.ui.screen.request_location.RequestLocationScreen
import br.com.qtota.ui.screen.request_location.RequestLocationViewModel
import br.com.qtota.ui.state_handler.UIState
import br.com.qtota.ui.theme.DefaultColor
import kotlinx.coroutines.launch

@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@Composable
internal fun MainNavigationScreen(navController: NavHostController) {

    val locationViewModel: RequestLocationViewModel = hiltViewModel()
    val locationState by locationViewModel.locationUiState.collectAsState()

    when (locationState) {
        is UIState.Loading -> LoadingLocation()
        is UIState.Error -> RequestLocationScreen(locationViewModel)
        is UIState.Success -> Content(navController)
    }

}

@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@Composable
private fun Content(navController: NavHostController) {

    var showLoginDialog by remember { mutableStateOf(false) }
    var showSendFlyerDialog by remember { mutableStateOf(false) }

    DrawerScaffold { topBarPadding, drawerState ->
        BottomBarScaffold(
            topBarPadding,
            navController,
            drawerState,
            { showLoginDialog = true },
            { showSendFlyerDialog = true }
        )
    }

    if (showLoginDialog)
        LoginDialog(
            onDismiss = { showLoginDialog = false },
            onConfirm = {
                showLoginDialog = false
                navController.navigate(AppRoute.Login.route)
            }
        )

    if (showSendFlyerDialog)
        SendFlyerDialog(onDismiss = { showSendFlyerDialog = false })

}

@Composable
private fun DrawerScaffold(content: @Composable (PaddingValues, DrawerState) -> Unit) {

    // Drawer
    val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)
    val scope = rememberCoroutineScope()

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
                gesturesEnabled = false
            ) {
                CompositionLocalProvider(value =LocalLayoutDirection provides LayoutDirection.Ltr) {
                    content(topBarPadding, drawerState)
                }
            }
        }
    }

}

@Composable
private fun BottomBarScaffold(topBarPadding: PaddingValues, navController: NavHostController, drawerState: DrawerState, showLoginDialog: () -> Unit, showSendFlyerDialog: () -> Unit) {

    val viewModel: MainNavigationViewModel = hiltViewModel()
    val bottomNavController = rememberNavController()

    Scaffold(
        bottomBar = { BottomNavBar(bottomNavController) },
        floatingActionButton = {
            FloatingActionButton(
                {
                    viewModel.checkIfLogged { isLogged ->
                        if (isLogged) {
                            showSendFlyerDialog()
                        } else {
                            showLoginDialog()
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
            BottomNavHost(bottomNavController, navController, drawerState)
        }
    }

}

@Composable
private fun LoginDialog(
    onDismiss: () -> Unit,
    onConfirm: () -> Unit,
) {
    ConfirmDialog(
        text = stringResource(R.string.send_flyer_dialog),
        onDismiss = onDismiss,
        onConfirm = onConfirm,
        confirmText = stringResource(R.string.log_in)
    )
}

@Composable
private fun SendFlyerDialog(
    onDismiss: () -> Unit
) {
    SendFlyerDialog(onDismiss = onDismiss)
}