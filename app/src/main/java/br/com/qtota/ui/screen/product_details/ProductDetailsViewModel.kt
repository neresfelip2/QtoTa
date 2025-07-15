package br.com.qtota.ui.screen.product_details

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import br.com.qtota.data.mapper.ProductMapper.toProduct
import br.com.qtota.data.repository.LocationRepository
import br.com.qtota.data.repository.ProductRepository
import br.com.qtota.ui.UIState
import br.com.qtota.ui.navigation.AppRoute
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ProductDetailsViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    val productRepository: ProductRepository,
    locationRepository: LocationRepository,
) : ViewModel() {

    private val productId: Long = savedStateHandle[AppRoute.ProductDetails.ARG_PRODUCT_ID]!!

    private val _productDetails = MutableStateFlow<UIState<ProductDetail>>(UIState.Loading)
    val productDetails = _productDetails.asStateFlow()

    private val _savedProductState = MutableStateFlow<UIState<Boolean>>(UIState.Loading)
    val savedProductState = _savedProductState.asStateFlow()

    private val savedProductsState = productRepository.getSavedProducts()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(), emptyList())

    init {
        viewModelScope.launch {
            val productDetail = productRepository.getProductById(productId, locationRepository.location!!)
            if(productDetail != null) {
                _productDetails.value = UIState.Success(productDetail)
                savedProductsState.collectLatest { savedProduct ->
                    val savedIds = savedProduct.map { it.id }.toSet()
                    _savedProductState.value = UIState.Success(savedIds.contains(productId))
                }
            } else {
                _productDetails.value = UIState.Error("Algo deu errado")
            }
        }
    }

    internal fun saveProduct() {
        viewModelScope.launch {
            val productDetails = (_productDetails.value as UIState.Success).data
            productRepository.insert(productDetails.toProduct())
        }
    }

    internal fun deleteProduct() {
        viewModelScope.launch {
            val productDetails = (_productDetails.value as UIState.Success).data
            productRepository.delete(productDetails.toProduct())
        }
    }

}