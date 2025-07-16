package br.com.qtota.ui.screen.home

import android.location.Location
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import br.com.qtota.data.local.entity.Product
import br.com.qtota.data.mapper.ProductMapper.toProduct
import br.com.qtota.data.remote.home_response.CategoryResponse
import br.com.qtota.data.remote.home_response.HomeResponse
import br.com.qtota.data.repository.LocationRepository
import br.com.qtota.data.repository.ProductRepository
import br.com.qtota.ui.UIState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val productRepository: ProductRepository,
    private val locationRepository: LocationRepository
) : ViewModel() {

    private val _homeUiState = MutableStateFlow<UIState<HomeResponse>>(UIState.Loading)
    val homeUIState = _homeUiState.asStateFlow()

    private val _productListState = MutableStateFlow<UIState<List<Product>>>(UIState.Loading)
    val productListState = _productListState.asStateFlow()

    private val _localityNameState = MutableStateFlow("Carregando...")
    val localityNameState = _localityNameState.asStateFlow()

    private val savedProductsState = productRepository.getSavedProducts()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(), emptyList())

    init {
        _localityNameState.value = locationRepository.getNeighborhood()
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
            _productListState.value = UIState.Success(result.products.map { it.toProduct() })

            savedProductsState.collectLatest { savedProduct ->
                val savedIds = savedProduct.map { it.id }.toSet()
                val products = (_productListState.value as UIState.Success).data
                products.forEach { product ->
                    product.isSaved = product.id in savedIds
                }
                _productListState.value = UIState.Success(products)
            }

        }
    }

    internal fun selectTab(category: CategoryResponse?) {
        _productListState.value = UIState.Loading

        viewModelScope.launch {
            val products = productRepository.getProducts(categoryId = category?.id, location = locationRepository.location!!)

            if(products == null) {
                _productListState.value = UIState.Error("")
                return@launch
            }

            _productListState.value = UIState.Success(products)
            savedProductsState.collectLatest { savedProduct ->
                val savedIds = savedProduct.map { it.id }.toSet()
                val products = (_productListState.value as UIState.Success).data
                products.forEach { product ->
                    product.isSaved = product.id in savedIds
                }
                _productListState.value = UIState.Success(products)
            }

        }
    }

    internal fun saveProduct(product: Product) {
        viewModelScope.launch {
            if (product.isSaved) {
                productRepository.delete(product)
            } else {
                productRepository.insert(product)
            }
        }
    }

}
