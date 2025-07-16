package br.com.qtota.ui.screen.search_product

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
class SearchProductViewModel @Inject constructor(
    private val locationRepository: LocationRepository,
    private val productRepository: ProductRepository,
) : ViewModel() {

    val neighborhood = locationRepository.getNeighborhood()

    private val _listProductState = MutableStateFlow<List<Product>>(emptyList())
    val listProductState = _listProductState.asStateFlow()

    private val _loadState = MutableStateFlow(LoadState.LOADING)
    val loadState = _loadState.asStateFlow()

    private var page = 0
    private val limit = 10
    private var query: String = ""

    fun getProducts() {
        _loadState.value = LoadState.LOADING
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
                    _loadState.value = LoadState.ERROR
                }
                result.isEmpty() -> {
                    page--
                    _loadState.value = LoadState.EMPTY
                }
                else -> {
                    _loadState.value = LoadState.SUCCESS
                    // usamos List + resultado
                    _listProductState.value = _listProductState.value + result
                }
            }
        }
    }

    /** Sempre que quiser buscar (nova query ou paginação) chame aqui */
    fun performSearch(newQuery: String?) {
        // Se vier null, tratamos como string vazia
        val cleaned = newQuery.orEmpty()
        // Se for diferente da anterior, resetamos tudo
        if (cleaned != query) {
            query = cleaned
            page = 0
            _listProductState.value = emptyList()
        }
        // busca a próxima página (ou primeira, se resetamos)
        getProducts()
    }

}