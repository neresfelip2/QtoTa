package br.com.qtota.ui.screen.store_detail

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import br.com.qtota.ui.navigation.AppRoute
import br.com.qtota.ui.state_handler.UIState
import com.google.android.gms.maps.model.LatLng
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class StoreDetailViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle
) : ViewModel() {

    private val id: Long = savedStateHandle[AppRoute.StoreDetail.ARG_ID]!!

    private val _uiState = MutableStateFlow<UIState<StoreDetail>>(UIState.Loading)
    val storeDetailState = _uiState.asStateFlow()

    init {
        viewModelScope.launch {
            _uiState.value = UIState.Success(
                StoreDetail(
                    "Êxito",
                    "https://encrypted-tbn0.gstatic.com/images?q=tbn:ANd9GcQQk0s0OJsPoyDzwWLlrXT5f9__4-Hn2g1SpA&s",
                    LatLng(-3.7674667625817437, -38.62487588392078),
                    690,
                    "Av. Torreon, 245 - Parque Potira, Caucaia - CE, 61650-350",
                    listOf(
                        StoreDetailProduct(
                            id = 0,
                            name = "Arroz",
                            price = 0.0,
                            percentageOfAverage = 0,
                            urlImage = null,
                        ),
                        StoreDetailProduct(
                            id = 0,
                            name = "Arroz",
                            price = 0.0,
                            percentageOfAverage = 0,
                            urlImage = null,
                        ),
                        StoreDetailProduct(
                            id = 0,
                            name = "Arroz",
                            price = 0.0,
                            percentageOfAverage = 0,
                            urlImage = null,
                        ),
                        StoreDetailProduct(
                            id = 0,
                            name = "Arroz",
                            price = 0.0,
                            percentageOfAverage = 0,
                            urlImage = null,
                        ),
                        StoreDetailProduct(
                            id = 0,
                            name = "Arroz",
                            price = 0.0,
                            percentageOfAverage = 0,
                            urlImage = null,
                        ),
                        StoreDetailProduct(
                            id = 0,
                            name = "Arroz",
                            price = 0.0,
                            percentageOfAverage = 0,
                            urlImage = null,
                        ),
                    ),
                    listOf(
                        StoreDetailBranch(
                            description = "Parque Guadalajara",
                            address = "R. Acapulco, 1360 - Parque Guadalajara, Caucaia - CE, 61650-160",
                            distance = 430
                        ),
                        StoreDetailBranch(
                            description = "Parque Potira II",
                            address = "Av. Torreon, 1983 - Parque Potira, Caucaia - CE, 61650-350",
                            distance = 900
                        )
                    )
                )
            )
        }
    }

}