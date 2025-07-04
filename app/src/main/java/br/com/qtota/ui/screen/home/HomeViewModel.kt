package br.com.qtota.ui.screen.home

import android.content.Context
import android.net.Uri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import br.com.qtota.data.local.entity.Product
import br.com.qtota.data.repository.ProductRepository
import br.com.qtota.data.repository.UserRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val userRepository: UserRepository,
    private val productRepository: ProductRepository
) : ViewModel() {

    private var page: Int = 0

    private val _storeTabsState = MutableStateFlow<List<String>>(listOf())
    val storeTabsState = _storeTabsState.asStateFlow()

    private val _productListState = MutableStateFlow<MutableList<Product>>(mutableListOf())
    val productListState = _productListState.asStateFlow()

    private val _loadingState = MutableStateFlow(true)
    val loadingState = _loadingState.asStateFlow()

    private val _sendingFlyerState = MutableStateFlow<FlyerState?>(null)
    val sendingFlyerState = _sendingFlyerState.asStateFlow()

    init {
        loadProducts()
    }

    internal fun loadProducts() {
        _loadingState.value = true
        page++
        viewModelScope.launch {
            val result = productRepository.getProducts(page).getOrNull()
            if(result != null) {
                updateProductList(_productListState.value + result)
            } else {
                page--
            }
            _loadingState.value = false
        }
    }

    private fun updateProductList(list: List<Product>) {
        _productListState.value = list.toMutableList()
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

    internal fun checkIfLogged(onResult: (Boolean) -> Unit) {
        viewModelScope.launch {
            val isLogged = userRepository.authTokenFlow
                .map { !it.isNullOrEmpty() }
                .first()
            onResult(isLogged)
        }
    }

    internal fun sendFlyer(imageUri: Uri, context: Context, dismissDialog: () -> Unit) {
        viewModelScope.launch {
            _sendingFlyerState.value = FlyerState.Sending
            val result = productRepository.sendFlyer(imageUri, context).getOrNull()
            delay(5_000)
            if(result != null) {
                updateProductList(result)
                _sendingFlyerState.value = null
                dismissDialog()
            } else {
                _sendingFlyerState.value = FlyerState.Error
            }
        }
    }

}
