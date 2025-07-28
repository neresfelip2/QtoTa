package br.com.qtota.ui.screen.request_location

import android.Manifest
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import br.com.qtota.R
import br.com.qtota.ui.components.MessageContent
import br.com.qtota.ui.components.Toolbar
import br.com.qtota.ui.theme.DefaultColor
import br.com.qtota.ui.theme.defaultPadding
import coil.compose.AsyncImage
import com.composables.icons.lucide.Lucide
import com.composables.icons.lucide.MapPin

@Composable
internal fun RequestLocationScreen(viewModel: RequestLocationViewModel) {

    val locationPermissionLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { granted ->
        if (granted) {
            viewModel.requestLocation()
        }
    }

    Scaffold(topBar = { Toolbar(stringResource(R.string.app_name)) }) { innerPadding ->
        Column(
            Modifier.fillMaxSize().padding(innerPadding),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        )
        {
            MessageContent(
                { Lucide.MapPin },
                stringResource(R.string.request_location_label)
            )
            Spacer(Modifier.height(defaultPadding))
            Button({
                locationPermissionLauncher.launch(Manifest.permission.ACCESS_FINE_LOCATION)
            },
                colors = ButtonDefaults.buttonColors(containerColor = DefaultColor)
            ) {
                Text(stringResource(R.string.request_location_button))
            }
        }
    }

}

@Composable
internal fun LoadingLocation() {
    Column(
        Modifier.fillMaxSize().background(Color.White),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        AsyncImage(
            model = R.drawable.request_location, null,
        )
        Text(stringResource(R.string.loading_location))
    }
}