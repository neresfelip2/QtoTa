package br.com.qtota.ui.screen.saved_offers

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import br.com.qtota.data.repository.ProductRepository
import br.com.qtota.ui.state_handler.UIState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SavedItemsViewModel @Inject constructor(
    private val productRepository: ProductRepository
) : ViewModel() {

    private val _savedProducts = MutableStateFlow<List<SavedProductUI>?>(null)
    val savedProducts = _savedProducts.asStateFlow()

    init {
        viewModelScope.launch {
            productRepository.getSavedProducts().collectLatest {
                _savedProducts.value = it.map { product ->
                    SavedProductUI(
                        product = product,
                        offersState = UIState.Loading
                    )
                }
                _savedProducts.value?.let { listProducts ->
                    val response = productRepository.getSavedProductsWithOffers(listProducts)
                    if(response != null) {
                        _savedProducts.value = response
                    } else {
                        _savedProducts.value = listProducts.map { product ->
                            product.copy(offersState = UIState.Error(""))
                        }
                    }
                }
            }
        }
    }

}