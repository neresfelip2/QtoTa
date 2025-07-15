package br.com.qtota.ui.screen.request_location

import android.Manifest
import android.annotation.SuppressLint
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.LocationOn
import androidx.compose.material3.Button
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import br.com.qtota.R
import br.com.qtota.ui.UIState
import br.com.qtota.ui.components.LoadingComponent
import br.com.qtota.ui.components.MessageContent
import br.com.qtota.ui.components.Toolbar
import br.com.qtota.ui.navigation.AppRoute
import br.com.qtota.ui.theme.defaultPadding

@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@Composable
internal fun RequestLocationScreen(navController: NavHostController) {

    val viewModel: RequestLocationViewModel = hiltViewModel()

    val locationState by viewModel.locationUiState.collectAsState()

    Scaffold(
        topBar = { Toolbar() }
    ) { innerPadding ->
        when (locationState) {
            is UIState.Loading -> LoadingComponent(Modifier.fillMaxSize(), "Buscando localização...")
            is UIState.Error -> RequestLocationComponent(viewModel)
            is UIState.Success -> navController.navigate(AppRoute.MainNav.route) {
                popUpTo(navController.graph.id) {
                    inclusive = true
                }
                launchSingleTop = true
            }
        }
    }

}

@Composable
private fun RequestLocationComponent(viewModel: RequestLocationViewModel) {
    val locationPermissionLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { granted ->
        if (granted) {
            viewModel.requestLocation()
        }
    }

    Column(
        Modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    )
    {
        MessageContent(
            { Icons.Outlined.LocationOn },
            stringResource(R.string.request_location_label)
        )
        Spacer(Modifier.height(defaultPadding))
        Button({
            locationPermissionLauncher.launch(Manifest.permission.ACCESS_FINE_LOCATION)
        }) {
            Text(stringResource(R.string.request_location_button))
        }
    }
}