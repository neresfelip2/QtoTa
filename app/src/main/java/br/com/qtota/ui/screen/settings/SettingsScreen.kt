package br.com.qtota.ui.screen.settings

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.ExitToApp
import androidx.compose.material.icons.outlined.FavoriteBorder
import androidx.compose.material.icons.outlined.Info
import androidx.compose.material.icons.outlined.Person
import androidx.compose.material.icons.outlined.ThumbUp
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import br.com.qtota.ui.components.ConfirmDialog
import br.com.qtota.ui.components.Toolbar
import br.com.qtota.ui.navigation.AppRoutes
import br.com.qtota.ui.theme.ErrorColor

@Composable
internal fun SettingsScreen(navController: NavHostController) {

    val viewModel: SettingsViewModel = hiltViewModel()
    val isLogged by viewModel.isLogged.collectAsState()

    var showDialogLogout by remember { mutableStateOf(false) }

    val context = LocalContext.current

    Scaffold(
        topBar = { Toolbar(
            backButtonEnabled = navController
        ) }
    ) { innerPadding ->
        Column(
            Modifier
                .padding(innerPadding)
                .padding(8.dp)) {

            SettingsGroup(
                {
                    if(isLogged) {
                        SettingsButton("Logout", Icons.AutoMirrored.Outlined.ExitToApp, color = ErrorColor) {
                            showDialogLogout = true
                        }
                    } else {
                        SettingsButton("Fazer login", Icons.Outlined.Person) {
                            navController.navigate(AppRoutes.Login.route) { launchSingleTop = true }
                        }
                    }
                }
            )
            SettingsGroup(
                {
                    SettingsButton("Ofertas salvas", Icons.Outlined.FavoriteBorder) {
                        navController.navigate(AppRoutes.SavedOffers.route)
                    }
                }
            )
            SettingsGroup(
                { SettingsButton("Avalie-nos", Icons.Outlined.ThumbUp) { viewModel.openPlayStore(context) } },
                { SettingsButton("Sobre", Icons.Outlined.Info) {} },
            )
        }
    }

    if (showDialogLogout) {
        ConfirmDialog (
            text = "Deseja fazer logout?",
            onDismiss = { showDialogLogout = false },
            onConfirm = {
                viewModel.logout()
                showDialogLogout = false
            }
        )
    }

}

@Composable
private fun SettingsGroup(vararg settingsButton: @Composable () -> Unit) {
    Column(
        Modifier
            .padding(8.dp)
            .background(Color.White, RoundedCornerShape(24.dp))
    ) {
        settingsButton.forEach { it() }
    }
}

@Composable
private fun SettingsButton(title: String, icon: ImageVector, color: Color = MaterialTheme.colorScheme.primary, onClick: () -> Unit) {
    TextButton(onClick) {
        Icon(icon, null, Modifier.padding(8.dp), tint = color)
        Text(title,
            Modifier
                .fillMaxWidth()
                .padding(8.dp),
            color = color
        )
    }
}

@Composable @Preview(showSystemUi = true)
private fun SettingsScreenPreview() {
    SettingsScreen(rememberNavController())
}

@Composable @Preview(showBackground = true)
private fun DialogLogoutPreview() {

}