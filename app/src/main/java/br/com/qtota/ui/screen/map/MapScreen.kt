package br.com.qtota.ui.screen.map

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.core.graphics.scale
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import br.com.qtota.R
import br.com.qtota.ui.components.ErrorComponent
import br.com.qtota.ui.components.LoadingComponent
import br.com.qtota.ui.navigation.AppRoute
import br.com.qtota.ui.state_handler.UIState
import br.com.qtota.utils.BitmapUtils
import br.com.qtota.utils.BitmapUtils.cropToCircle
import br.com.qtota.utils.Utils.mapStyle
import com.google.android.gms.maps.model.BitmapDescriptor
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.MapStyleOptions
import com.google.maps.android.compose.GoogleMap
import com.google.maps.android.compose.MapProperties
import com.google.maps.android.compose.Marker
import com.google.maps.android.compose.MarkerState
import com.google.maps.android.compose.rememberCameraPositionState

@Composable
fun MapScreen(bottomNavController: NavHostController) {

    val viewModel: MapViewModel = hiltViewModel()
    val markerState by viewModel.markerState.collectAsState()

    when(markerState) {
        is UIState.Loading -> LoadingComponent(Modifier.fillMaxSize())
        is UIState.Error -> ErrorComponent(stringResource(R.string.error_loading_message), Modifier.fillMaxSize())
        is UIState.Success -> {

            val markers = (markerState as UIState.Success).data

            val context = LocalContext.current
            val density = LocalDensity.current

            val cameraPositionState = rememberCameraPositionState {
                position = CameraPosition.fromLatLngZoom(viewModel.getCurrentPosition(), 15f)
            }

            val targetSize = 40.dp
            val iconSizePx = with(density) { targetSize.toPx().toInt() }

            GoogleMap(
                modifier = Modifier
                    .fillMaxSize(),
                cameraPositionState = cameraPositionState,
                properties = MapProperties(
                    isMyLocationEnabled = true,
                    mapStyleOptions = MapStyleOptions(mapStyle)
                ),
            ) {

                markers.forEach { marker ->

                    var markerIcon by remember { mutableStateOf<BitmapDescriptor?>(null) }

                    LaunchedEffect(marker.logo) {
                        BitmapUtils.downloadImageFromUrl(marker.logo, context)?.let { bitmap ->
                            val circleBmp = bitmap.cropToCircle()
                            val scaledBmp = circleBmp.scale(iconSizePx, iconSizePx, false)
                            markerIcon = BitmapDescriptorFactory.fromBitmap(scaledBmp)
                        }
                    }

                    Marker(
                        state = MarkerState(
                            position = marker.position
                        ),
                        title = marker.name,
                        snippet = marker.branch,
                        icon = markerIcon,
                        onInfoWindowClick = {
                            if(viewModel.storeId == null)
                                bottomNavController.navigate(AppRoute.StoreDetail.createRoute(marker.id))
                        },
                    )
                }

            }
        }
    }

}