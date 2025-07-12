package br.com.qtota.ui.screen.home

import android.annotation.SuppressLint
import android.content.Context
import android.location.Location
import android.net.Uri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import br.com.qtota.data.local.entity.Product
import br.com.qtota.data.mapper.ProductMapper.toProduct
import br.com.qtota.data.remote.home_response.CategoryResponse
import br.com.qtota.data.remote.home_response.HomeResponse
import br.com.qtota.data.repository.LocationRepository
import br.com.qtota.data.repository.ProductRepository
import br.com.qtota.data.repository.UserRepository
import br.com.qtota.ui.UIState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject


@HiltViewModel
class HomeViewModel @Inject constructor(
    private val userRepository: UserRepository,
    private val productRepository: ProductRepository,
    private val locationRepository: LocationRepository
) : ViewModel() {

    private val _locationUiState = MutableStateFlow<UIState<Location>>(UIState.Loading)
    val locationUiState = _locationUiState.asStateFlow()

    private val _homeUiState = MutableStateFlow<UIState<HomeResponse>>(UIState.Loading)
    val homeUIState = _homeUiState.asStateFlow()

    private val _productListState = MutableStateFlow<UIState<List<Product>>>(UIState.Loading)
    val productListState = _productListState.asStateFlow()

    private val _sendingFlyerState = MutableStateFlow<UIState<List<Product>>>(UIState.Loading)
    val sendingFlyerState = _sendingFlyerState.asStateFlow()

    private val _localityNameState = MutableStateFlow("Carregando...")
    val localityNameState = _localityNameState.asStateFlow()

    private val savedProductsState = productRepository.getSavedProducts()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(), emptyList())

    init {
        viewModelScope.launch {
            locationRepository.loadStatus.collect { isLoading ->

                if (isLoading) {
                    _locationUiState.value = UIState.Loading
                    return@collect
                }

                if (locationRepository.location == null) {
                    _locationUiState.value = UIState.Error("Localização não encontrada")
                    return@collect
                }

                _locationUiState.value = UIState.Success(locationRepository.location!!)

                val neighborhood = locationRepository.getNeighborhood(locationRepository.location!!)
                _localityNameState.value = neighborhood ?: "Indisponível"

                fetchHome(locationRepository.location!!)

            }
        }
        requestLocation()
    }

    @SuppressLint("MissingPermission")
    internal fun requestLocation() {
        locationRepository.startLocationUpdates()
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

            /*savedProductsState.collect { savedProduct ->
                val products = (result as UIState.Success<HomeResponse>).data.products.map { it.toProduct() }
                val savedIds = savedProduct.map { it.id }.toSet()
                products.forEach { product ->
                    product.isSaved = product.id in savedIds
                }

                _productListState.value = UIState.Success(products)
            }*/

        }
    }

    internal fun selectTab(category: CategoryResponse?) {
        _productListState.value = UIState.Loading

        viewModelScope.launch {
            val products = productRepository.getProducts(category?.id, locationRepository.location!!)

            if(products == null) {
                _productListState.value = UIState.Error("")
                return@launch
            }

            _productListState.value = UIState.Success(products)
            /*savedProductsState.collect { savedProduct ->
                val savedIds = savedProduct.map { it.id }.toSet()
                products.forEach { product ->
                    product.isSaved = product.id in savedIds
                }
                _productListState.value = UIState.Success(products)
            }
*/
        }
    }

    internal fun sendFlyer(imageUri: Uri, context: Context, dismissDialog: () -> Unit) {
        viewModelScope.launch {
            _sendingFlyerState.value = UIState.Loading
            val products = productRepository.sendFlyer(imageUri, context)
            if(products != null) {
                _sendingFlyerState.value = UIState.Success(products)
                dismissDialog()
            } else {
                _sendingFlyerState.value = UIState.Error("")
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

    internal fun checkIfLogged(onResult: (Boolean) -> Unit) {
        viewModelScope.launch {
            val isLogged = userRepository.authTokenFlow
                .map { !it.isNullOrEmpty() }
                .first()
            onResult(isLogged)
        }
    }

}
