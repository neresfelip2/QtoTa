package br.com.qtota.ui.screen.menu

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
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
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import br.com.qtota.R
import br.com.qtota.ui.components.ConfirmDialog
import br.com.qtota.ui.navigation.AppRoutes
import br.com.qtota.ui.theme.ErrorColor
import br.com.qtota.ui.theme.defaultPadding

@Composable
internal fun MenuScreen(navController: NavHostController) {

    val viewModel: MenuViewModel = hiltViewModel()
    val isLogged by viewModel.isLogged.collectAsState()

    var showDialogLogout by remember { mutableStateOf(false) }

    val context = LocalContext.current

    Column(
        Modifier
            .padding(defaultPadding)
    ) {

        MenuGroup {
            if (isLogged) {
                MenuButton(stringResource(R.string.logout), Icons.AutoMirrored.Outlined.ExitToApp, color = ErrorColor) {
                    showDialogLogout = true
                }
            } else {
                MenuButton(stringResource(R.string.log_in), Icons.Outlined.Person) {
                    navController.navigate(AppRoutes.Login.route) { launchSingleTop = true }
                }
            }
        }

        MenuGroup {
            MenuButton(stringResource(R.string.saved_offers), Icons.Outlined.FavoriteBorder) {
                navController.navigate(AppRoutes.SavedOffers.route)
            }
        }

        MenuGroup {
            MenuButton(stringResource(R.string.rate_us), Icons.Outlined.ThumbUp) { viewModel.openPlayStore(context) }
            MenuButton(stringResource(R.string.about),    Icons.Outlined.Info)     { /* … */ }
        }
    }

    if (showDialogLogout) {
        ConfirmDialog (
            text = stringResource(R.string.logout_dialog_message),
            onDismiss = { showDialogLogout = false },
            onConfirm = {
                viewModel.logout()
                showDialogLogout = false
            }
        )
    }

}

@Composable
private fun MenuGroup(
    modifier: Modifier = Modifier,
    content: @Composable ColumnScope.() -> Unit
) {
    Column(
        modifier
            .padding(8.dp)
            .background(Color.White, RoundedCornerShape(24.dp)),
        content = content
    )
}

@Composable
private fun MenuButton(title: String, icon: ImageVector, color: Color = MaterialTheme.colorScheme.primary, onClick: () -> Unit) {
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
private fun MenuScreenPreview() {
    MenuScreen(rememberNavController())
}

@Composable @Preview(showBackground = true)
private fun DialogLogoutPreview() {

}