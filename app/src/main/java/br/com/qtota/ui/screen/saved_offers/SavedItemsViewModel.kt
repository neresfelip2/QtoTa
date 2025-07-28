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

    internal fun loadSavedOffers(sortType: SortType) {

        if (_savedProducts.value != null) {
            val list = _savedProducts.value!!
            _savedProducts.value = when(sortType) {
                SortType.ALFABETIC -> list.sortedBy { it.product.name }
                SortType.MOST_RECENT -> list.sortedByDescending { it.product.createdAt }
            }
            return
        }

        viewModelScope.launch {
            productRepository.getSavedProducts().collectLatest {

                _savedProducts.value = it.map { product ->
                    SavedProductUI(
                        product = product,
                        offersState = UIState.Loading
                    )
                }

                _savedProducts.value?.let { listProducts ->
                    productRepository.getSavedProductsWithOffers(listProducts,
                        {
                            _savedProducts.value = listProducts.map { product ->
                                product.copy(offersState = UIState.Error(""))
                            }
                        }
                    ) { savedProducts ->
                        _savedProducts.value = savedProducts
                    }
                }
            }
        }

    }

}