package br.com.qtota.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.ArrowBack
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.navigation.NavHostController
import br.com.qtota.ui.theme.GradientBackground

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun Toolbar(title: String? = null, backButtonEnabled: NavHostController? = null, vararg actions: Pair<ImageVector, () -> Unit>) {

    var backButtonClicked = false

    Box(Modifier.background(brush = GradientBackground)) {
        TopAppBar(
            colors = TopAppBarDefaults.topAppBarColors(
                containerColor = Color.Transparent,
                titleContentColor = Color.White,
                actionIconContentColor = Color.White
            ),
            title = { title?.let { Text(it, fontWeight = FontWeight.Bold) } },
            actions = {
                actions.forEach {
                    IconButton(it.second) {
                        Icon(it.first, null)
                    }
                }
            },
            navigationIcon = {
                backButtonEnabled?.let {
                    IconButton({
                        if(!backButtonClicked) {
                            backButtonEnabled.popBackStack()
                            backButtonClicked = true
                        }
                    }) {
                        Icon(Icons.AutoMirrored.Outlined.ArrowBack, null, tint = Color.White)
                    }
                }
            }
        )
    }
}