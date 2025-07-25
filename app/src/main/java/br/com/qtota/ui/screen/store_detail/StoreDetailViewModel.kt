package br.com.qtota.ui.screen.store_detail

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import br.com.qtota.data.repository.LocationRepository
import br.com.qtota.data.repository.StoreRespository
import br.com.qtota.ui.navigation.AppRoute
import br.com.qtota.ui.state_handler.UIState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class StoreDetailViewModel @Inject constructor(
    private val storeRepository: StoreRespository,
    private val locationRepository: LocationRepository,
    savedStateHandle: SavedStateHandle
) : ViewModel() {

    private val id: Long = savedStateHandle[AppRoute.StoreDetail.ARG_ID]!!

    private val _uiState = MutableStateFlow<UIState<StoreDetail>>(UIState.Loading)
    val storeDetailState = _uiState.asStateFlow()

    init {
        viewModelScope.launch {
            _uiState.value = UIState.Loading

            val result = storeRepository.getStoreDetail(id, locationRepository.location!!)

            if(result == null) {
                _uiState.value = UIState.Error("")
                return@launch
            }

            _uiState.value = UIState.Success(result)
        }
    }

}