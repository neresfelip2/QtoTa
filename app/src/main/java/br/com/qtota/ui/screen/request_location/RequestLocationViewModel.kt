package br.com.qtota.ui.screen.request_location

import android.annotation.SuppressLint
import android.location.Location
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import br.com.qtota.data.repository.LocationRepository
import br.com.qtota.ui.UIState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class RequestLocationViewModel @Inject constructor(
    private val locationRepository: LocationRepository
) : ViewModel() {

    private val _locationUiState = MutableStateFlow<UIState<Location>>(UIState.Loading)
    val locationUiState = _locationUiState.asStateFlow()

    init {
        viewModelScope.launch {
            locationRepository.loadStatus.collectLatest { isLoading ->

                if (isLoading) {
                    _locationUiState.value = UIState.Loading
                    return@collectLatest
                }

                if (locationRepository.location == null) {
                    _locationUiState.value = UIState.Error("Localização não encontrada")
                    return@collectLatest
                }

                _locationUiState.value = UIState.Success(locationRepository.location!!)

            }
        }
        requestLocation()
    }

    @SuppressLint("MissingPermission")
    internal fun requestLocation() {
        locationRepository.startLocationUpdates()
    }

}