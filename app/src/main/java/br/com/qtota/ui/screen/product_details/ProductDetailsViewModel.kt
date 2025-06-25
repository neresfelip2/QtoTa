package br.com.qtota.ui.screen.product_details

import android.util.Log
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import br.com.qtota.data.repository.ProductRepository
import br.com.qtota.ui.navigation.AppRoutes
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ProductDetailsViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    productRepository: ProductRepository
) : ViewModel() {

    private val productId: Long = savedStateHandle[AppRoutes.ProductDetails.ARG_PRODUCT_ID]!!

    private val _productDetails = MutableStateFlow<ProductState>(ProductState.Loading)
    val productDetails = _productDetails.asStateFlow()

    init {
        viewModelScope.launch {
            val response = productRepository.getProductById(productId)
            val result = response.getOrNull()
            Log.i("teste", "response: ${response.isSuccess} | result: $result")
            if(response.isSuccess && result != null) {
                _productDetails.value = ProductState.Success(result)
            } else {
                _productDetails.value = ProductState.Error("Algo deu errado")
            }
        }
    }

}