package br.com.qtota.ui.screen.home

import android.location.Location
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import br.com.qtota.data.remote.home_response.HomeResponse
import br.com.qtota.data.repository.LocationRepository
import br.com.qtota.data.repository.ProductRepository
import br.com.qtota.ui.state_handler.UIState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val productRepository: ProductRepository,
    locationRepository: LocationRepository
) : ViewModel() {

    private val _homeUiState = MutableStateFlow<UIState<HomeResponse>>(UIState.Loading)
    val homeUIState = _homeUiState.asStateFlow()

    private val _localityNameState = MutableStateFlow("Carregando...")
    val localityNameState = _localityNameState.asStateFlow()

    init {
        viewModelScope.launch {
            _localityNameState.value = locationRepository.getNeighborhood()
        }
        fetchHome(locationRepository.location!!)
    }

    private fun fetchHome(location: Location) {

        viewModelScope.launch {
            val result = productRepository.getHome(location.latitude, location.longitude)

            if(result == null) {
                _homeUiState.value = UIState.Error("")
                return@launch
            }

            _homeUiState.value = UIState.Success(result)

        }
    }

}
