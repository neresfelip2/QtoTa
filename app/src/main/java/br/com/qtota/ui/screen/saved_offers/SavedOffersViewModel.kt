package br.com.qtota.ui.screen.saved_offers

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import br.com.qtota.data.local.entity.Product
import br.com.qtota.data.repository.ProductRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SavedOffersViewModel @Inject constructor(
    private val productRepository: ProductRepository
) : ViewModel() {

    internal val savedProducts = productRepository.getAll().stateIn(
        viewModelScope,
        SharingStarted.Lazily,
        null
    )

    internal fun deleteProduct(product: Product) {
        viewModelScope.launch {
            productRepository.delete(product)
        }
    }

}