package br.com.qtota.ui.screen.list_product

import androidx.lifecycle.ViewModel
import br.com.qtota.data.repository.LocationRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class ListProductViewModel @Inject constructor(
    locationRepository: LocationRepository
) : ViewModel() {

    val location = locationRepository.getNeighborhood()

}