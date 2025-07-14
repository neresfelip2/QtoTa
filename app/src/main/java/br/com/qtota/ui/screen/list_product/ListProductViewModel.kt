package br.com.qtota.ui.screen.list_product

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import br.com.qtota.data.local.entity.Product
import br.com.qtota.data.repository.LocationRepository
import br.com.qtota.data.repository.ProductRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ListProductViewModel @Inject constructor(
    private val locationRepository: LocationRepository,
    private val productRepository: ProductRepository,
) : ViewModel() {

    val neighborhood = locationRepository.getNeighborhood()

    private val _listProductState = MutableStateFlow<List<Product>>(emptyList())
    val listProductState = _listProductState.asStateFlow()

    private val _loadState = MutableStateFlow<LoadState>(LoadState.LOADING)
    val loadState = _loadState.asStateFlow()

    var page = 0
    val limit = 10

    init {
        getProducts()
    }

    fun getProducts() {
        _loadState.value = LoadState.LOADING
        page++
        viewModelScope.launch {
            val result = productRepository.getProducts(
                location = locationRepository.location!!,
                page = page,
                limit = limit
            )

            if(result == null) {
                page--
                _loadState.value = LoadState.ERROR
                return@launch
            }

            if(result.isEmpty()) {
                page--
                _loadState.value = LoadState.EMPTY
                return@launch
            }

            _loadState.value = LoadState.SUCCESS
            _listProductState.value = _listProductState.value + result

        }
    }

}