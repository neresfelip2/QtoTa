package br.com.qtota.ui.screen.menu

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import br.com.qtota.R
import br.com.qtota.ui.components.ConfirmDialog
import br.com.qtota.ui.components.ImageComponent
import br.com.qtota.ui.navigation.AppRoute
import br.com.qtota.ui.theme.DefaultColor
import br.com.qtota.ui.theme.ErrorColor
import br.com.qtota.ui.theme.defaultPadding
import com.composables.icons.lucide.Info
import com.composables.icons.lucide.LogIn
import com.composables.icons.lucide.LogOut
import com.composables.icons.lucide.Lucide
import com.composables.icons.lucide.ThumbsUp
import com.composables.icons.lucide.User
import com.composables.icons.lucide.UserCog

@Composable
internal fun MenuScreen(navController: NavHostController) {

    val viewModel: MenuViewModel = hiltViewModel()
    val user by viewModel.user.collectAsState()

    var showDialogLogout by remember { mutableStateOf(false) }

    val context = LocalContext.current

    Column(
        Modifier
            .padding(defaultPadding)
    ) {

        user?.let {
            Row(
                Modifier.padding(defaultPadding),
                verticalAlignment = Alignment.CenterVertically
            ) {
                ImageComponent(
                    null,
                    Lucide.User,
                    32.dp,
                    Color.White,
                    Modifier.background(
                        shape = CircleShape,
                        color = Color.LightGray
                    ).padding(defaultPadding)
                )
                Spacer(Modifier.width(defaultPadding))
                Column {
                    Text(it.name, color = Color.DarkGray, fontSize = 14.sp, lineHeight = 17.sp, fontWeight = FontWeight.SemiBold)
                    Text(it.email, color = Color.Gray, fontSize = 13.sp, lineHeight = 16.sp)
                }
            }
            MenuGroup {
                MenuButton(stringResource(R.string.logout), Lucide.LogOut, color = ErrorColor) {
                    showDialogLogout = true
                }
                HorizontalDivider(Modifier.padding(horizontal = defaultPadding),0.5.dp, Color.LightGray)
                MenuButton("Configurações da conta", Lucide.UserCog) {

                }
            }
        } ?: MenuGroup {
            MenuButton(stringResource(R.string.log_in), Lucide.LogIn) {
                navController.navigate(AppRoute.Login.route) { launchSingleTop = true }
            }
        }

        MenuGroup {
            MenuButton(stringResource(R.string.rate_us), Lucide.ThumbsUp) { viewModel.openPlayStore(context) }
            HorizontalDivider(Modifier.padding(horizontal = defaultPadding),0.5.dp, Color.LightGray)
            MenuButton(stringResource(R.string.about),    Lucide.Info)     { /* … */ }
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
private fun MenuButton(title: String, icon: ImageVector, color: Color = DefaultColor, onClick: () -> Unit) {
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