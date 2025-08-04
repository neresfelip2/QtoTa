package br.com.qtota.ui.screen.request_location

import android.Manifest
import android.os.Build.VERSION.SDK_INT
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import br.com.qtota.R
import br.com.qtota.ui.components.MessageContent
import br.com.qtota.ui.components.Toolbar
import br.com.qtota.ui.theme.DefaultColor
import br.com.qtota.ui.theme.defaultPadding
import coil.ImageLoader
import coil.compose.rememberAsyncImagePainter
import coil.decode.GifDecoder
import coil.decode.ImageDecoderDecoder
import coil.request.ImageRequest
import coil.size.Size
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

    val context = LocalContext.current
    val imageLoader = ImageLoader.Builder(context)
        .components {
            if (SDK_INT >= 28) {
                add(ImageDecoderDecoder.Factory())
            } else {
                add(GifDecoder.Factory())
            }
        }
        .build()

    Column(
        Modifier.fillMaxSize().background(Color.White),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Image(
            rememberAsyncImagePainter(
                ImageRequest.Builder(context).data(data = R.drawable.request_location).apply(block = {
                    size(Size.ORIGINAL)
                }).build(), imageLoader = imageLoader
            ),
            contentDescription = null,
            modifier = Modifier.fillMaxWidth(),
        )
        Text(stringResource(R.string.loading_location))
    }
}