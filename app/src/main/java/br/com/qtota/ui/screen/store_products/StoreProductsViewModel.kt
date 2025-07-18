package br.com.qtota.ui.screen.store_products

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import br.com.qtota.data.local.entity.Product
import br.com.qtota.data.remote.product.ProductResponse
import br.com.qtota.data.repository.LocationRepository
import br.com.qtota.data.repository.ProductRepository
import br.com.qtota.ui.state_handler.LoadMoreListState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class StoreProductsViewModel @Inject constructor(
    private val productRepository: ProductRepository,
    private val locationRepository: LocationRepository
) : ViewModel() {

    private val _listProductState = MutableStateFlow<List<ProductResponse>>(emptyList())
    val listProductState = _listProductState.asStateFlow()

    private val _loadState = MutableStateFlow(LoadMoreListState.LOADING)
    val loadState = _loadState.asStateFlow()

    private var page = 0
    private val limit = 10
    private var query: String = ""

    fun getProducts() {
        _loadState.value = LoadMoreListState.LOADING
        page++
        viewModelScope.launch {
            val result = productRepository.getProducts(
                location = locationRepository.location!!,
                query = query,
                page = page,
                limit = limit
            )
            when {
                result == null -> {
                    page--
                    _loadState.value = LoadMoreListState.ERROR
                }
                result.isEmpty() -> {
                    page--
                    _loadState.value = LoadMoreListState.EMPTY
                }
                else -> {
                    _loadState.value = LoadMoreListState.SUCCESS
                    _listProductState.value = _listProductState.value + result
                }
            }
        }
    }

    fun performSearch(newQuery: String?) {
        val cleaned = newQuery.orEmpty()
        if (cleaned != query) {
            query = cleaned
            page = 0
            _listProductState.value = emptyList()
        }
        getProducts()
    }

}