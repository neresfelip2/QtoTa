package br.com.qtota.ui.screen.store_list

import androidx.annotation.DrawableRes
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
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
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import br.com.qtota.R
import br.com.qtota.ui.components.ErrorComponent
import br.com.qtota.ui.components.LoadingComponent
import br.com.qtota.ui.components.MessageContent
import br.com.qtota.ui.components.StoreListItem
import br.com.qtota.ui.state_handler.UIState
import br.com.qtota.ui.theme.defaultPadding
import com.google.android.gms.maps.model.CameraPosition
import com.google.maps.android.compose.GoogleMap
import com.google.maps.android.compose.MapProperties
import com.google.maps.android.compose.rememberCameraPositionState

@Composable
internal fun StoreListScreen(bottomNavController: NavHostController) {

    val viewModel: StoreListViewModel = hiltViewModel()

    var viewMode by remember { mutableStateOf(ViewMode.LIST) }

    Column {
        when(viewMode) {
            ViewMode.LIST -> {
                ChangeViewButton("Ver mapa", R.drawable.outline_map_24) {
                    viewMode = ViewMode.MAP
                }
                ListView(viewModel, bottomNavController)
            }
            ViewMode.MAP -> {
                ChangeViewButton("Ver lista", R.drawable.outline_map_24) {
                    viewMode = ViewMode.LIST
                }
                MapView(viewModel)
            }
        }
    }

}

@Composable
internal fun ColumnScope.ChangeViewButton(text: String, @DrawableRes icon: Int, onClick: () -> Unit) {
    TextButton(onClick,
        Modifier
            .padding(end = defaultPadding)
            .align(Alignment.End)
    ) {
        Icon(painterResource(icon), null)
        Spacer(Modifier.width(defaultPadding))
        Text(text)
    }
}

@Composable
internal fun ListView(viewModel: StoreListViewModel, bottomNavController: NavHostController) {

    val storeListState by viewModel.storeListState.collectAsState()

    when(storeListState) {
        is UIState.Loading -> LoadingComponent(Modifier.fillMaxSize())
        is UIState.Error -> ErrorComponent(stringResource(R.string.error_loading_message), Modifier.fillMaxSize())
        is UIState.Success -> {
            val stores = (storeListState as UIState.Success).data
            if(stores.isEmpty()) {
                MessageContent(
                    {
                        Icon(
                            painterResource(R.drawable.outline_store_24),
                            null,
                            Modifier.size(128.dp),
                            tint = Color(0x59187270)
                        )
                    },
                    stringResource(R.string.any_store_found),
                    modifier = Modifier
                        .fillMaxSize(),
                )
                return
            }
            LazyVerticalGrid(GridCells.Fixed(2), contentPadding = PaddingValues(8.dp)) {
                items(stores) { store ->
                    StoreListItem(store, bottomNavController)
                }
            }
        }
    }
}

@Composable
internal fun MapView(viewModel: StoreListViewModel) {

    val cameraPositionState = rememberCameraPositionState {
        position = CameraPosition.fromLatLngZoom(viewModel.getLatLng(), 16f)
    }

    GoogleMap(
        modifier = Modifier
            .fillMaxSize(),
        cameraPositionState = cameraPositionState,
        properties = MapProperties(
            isMyLocationEnabled = true
        ),
    ) {
        viewModel.storeListState.collectAsState().value
            .let { state ->
                if (state is UIState.Success) {
                    state.data.forEach { store ->
                        /*Marker(
                            state = MarkerState(
                                position = LatLng(store.latitude, store.longitude)
                            ),
                            title = store.name,
                            snippet = store.address
                        )*/
                    }
                }
            }
    }

}