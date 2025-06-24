package br.com.qtota.ui.screen.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import br.com.qtota.data.local.entity.Product
import br.com.qtota.data.repository.ProductRepository
import br.com.qtota.data.repository.UserRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.time.LocalDate
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val userRepository: UserRepository,
    private val productRepository: ProductRepository,
) : ViewModel() {

    private val _storeTabsState = MutableStateFlow<List<String>>(listOf())
    val storeTabsState = _storeTabsState.asStateFlow()

    private val _listProductState = MutableStateFlow<ListProductState>(ListProductState.Loading)
    val listProductState = _listProductState.asStateFlow()

    internal val savedProducts = productRepository.getAll().stateIn(
        viewModelScope,
        SharingStarted.Lazily,
        emptyList()
    )

    init {
        _listProductState.value = mockSuccess
    }

    internal fun checkIfLogged(onResult: (Boolean) -> Unit) {
        viewModelScope.launch {
            val isLogged = userRepository.authTokenFlow
                .map { !it.isNullOrEmpty() }
                .first()
            onResult(isLogged)
        }
    }

    internal fun saveProduct(product: Product) {
        viewModelScope.launch {
            if (savedProducts.value.find { it.id == product.id } == null) {
                productRepository.insert(product)
            } else {
                productRepository.delete(product)
            }
        }
    }

}

/* @TODO mocks para fins de desenvolvimento  */
private val mockError = ListProductState.Error("Ocorreu um erro")
private val mockEmptyList = ListProductState.Success(emptyList())
private val mockSuccess = ListProductState.Success(
    listOf(
        Product(
            id = 1,
            name = "Arroz Pai João 5kg",
            description = "Arroz parboilizado tipo 1",
            currentValue = 5.99,
            previousValue = 8.99,
            storeName = "Carrefour",
            distance = 800,
            expirationDate = LocalDate.of(2025, 6, 20)
        ),
        Product(
            id = 2,
            name = "Óleo Liza 900ml",
            description = "Óleo de soja refinado, perfeito para frituras",
            currentValue = 4.49,
            previousValue = 6.99,
            storeName = "Extra",
            distance = 1300,
            expirationDate = LocalDate.of(2025, 6, 18)
        ),
        Product(
            id = 3,
            name = "Macarrão",
            description = "Só um macarrão",
            currentValue = 7.89,
            previousValue = 10.0,
            storeName = "Guará",
            distance = 2100,
            expirationDate = LocalDate.of(2025, 6, 19)
        ),
    )
)