package br.com.qtota.ui.screen.store_list

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import br.com.qtota.data.remote.store.StoreResponse
import br.com.qtota.data.repository.LocationRepository
import br.com.qtota.data.repository.StoreRespository
import br.com.qtota.ui.state_handler.UIState
import com.google.android.gms.maps.model.LatLng
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class StoreListViewModel @Inject constructor(
    private val storeRepository: StoreRespository,
    private val locationRepository: LocationRepository
) : ViewModel() {

    private val _storeListState = MutableStateFlow<UIState<List<StoreResponse>>>(UIState.Loading)
    val storeListState = _storeListState.asStateFlow()

    init {
        viewModelScope.launch {
            val result = storeRepository.getNearbyStores(
                locationRepository.location!!.latitude, locationRepository.location!!.longitude
            )

            if(result == null) {
                _storeListState.value = UIState.Error("")
                return@launch
            }

            _storeListState.value = UIState.Success(result)
        }
    }

    fun getLatLng() : LatLng {
        return LatLng(locationRepository.location!!.latitude, locationRepository.location!!.longitude)
    }

}