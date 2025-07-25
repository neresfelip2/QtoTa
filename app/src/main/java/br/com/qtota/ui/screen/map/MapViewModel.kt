package br.com.qtota.ui.screen.map

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import br.com.qtota.data.repository.LocationRepository
import br.com.qtota.data.repository.StoreRespository
import br.com.qtota.ui.navigation.AppRoute
import br.com.qtota.ui.state_handler.UIState
import com.google.android.gms.maps.model.LatLng
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MapViewModel @Inject constructor(
    private val storeRepository: StoreRespository,
    private val locationRepository: LocationRepository,
    savedStateHandle: SavedStateHandle
): ViewModel() {

    val storeId: Long? = savedStateHandle.get<Long>(AppRoute.Map.ARG_STORE_ID).takeIf { it != 0L }

    private val _markersState = MutableStateFlow<UIState<List<StoreMarker>>>(UIState.Loading)
    val markerState = _markersState.asStateFlow()

    init {
        viewModelScope.launch {
            val result = storeRepository.getNearbyStoreBranches(
                storeId, locationRepository.location!!.latitude, locationRepository.location!!.longitude
            )

            if(result == null) {
                _markersState.value = UIState.Error("")
                return@launch
            }

            _markersState.value = UIState.Success(result.map { store ->
                StoreMarker(
                    id = store.id,
                    name = store.name,
                    logo = store.logo,
                    branch = store.branch,
                    position = LatLng(store.latitude, store.longitude),
                )
            })
        }
    }

    fun getCurrentPosition() : LatLng {
        return LatLng(locationRepository.location!!.latitude, locationRepository.location!!.longitude)
    }

}