package br.com.qtota.ui.screen.home

import android.annotation.SuppressLint
import android.content.Context
import android.location.Location
import android.net.Uri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import br.com.qtota.data.local.entity.Product
import br.com.qtota.data.repository.ProductRepository
import br.com.qtota.data.repository.UserRepository
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.Priority
import com.google.android.gms.tasks.CancellationTokenSource
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
    private val productRepository: ProductRepository,
    private val fusedLocationClient: FusedLocationProviderClient
) : ViewModel() {

    private var storeName: String? = null
    private var page: Int = 0

    private val _storeTabsState = MutableStateFlow<List<String>>(listOf())
    val storeTabsState = _storeTabsState.asStateFlow()

    private val _productListState = MutableStateFlow<MutableList<Product>>(mutableListOf())
    val productListState = _productListState.asStateFlow()

    private val _loadScreenState = MutableStateFlow(true)
    val loadScreenState = _loadScreenState.asStateFlow()

    private val _loadListState = MutableStateFlow(false)
    val loadListState = _loadListState.asStateFlow()

    private val _loadPageState = MutableStateFlow(false)
    val loadPageState = _loadPageState.asStateFlow()

    private val _sendingFlyerState = MutableStateFlow<FlyerState?>(null)
    val sendingFlyerState = _sendingFlyerState.asStateFlow()

    private val _location = MutableStateFlow<Location?>(null)
    val location = _location.asStateFlow()

    init {
        requestLocation()
    }

    private fun fetchProducts(
        location: Location,
        store: String? = null,
        resetPage: Boolean = true,
        setLoading: (Boolean) -> Unit,
        onSuccess: (List<Product>) -> Unit,
        onError: () -> Unit = {}
    ) {
        setLoading(true)
        if (resetPage) page = 1 else page++

        viewModelScope.launch {
            delay(2_000L) // simula latência
            val result = productRepository.getProducts(location, store, page).getOrNull()
            if (result != null) {
                onSuccess(result)
            } else {
                page--
                onError()
            }
            setLoading(false)
        }
    }

    internal fun changeTab(store: String?) {
        fetchProducts(
            location     = location.value!!,
            store        = store,
            resetPage    = true,
            setLoading   = { _loadListState.value = it },
            onSuccess    = { firstPage ->
                _productListState.value = firstPage.toMutableList()
                this@HomeViewModel.storeName = store
            }
        )
    }

    internal fun loadMoreProducts() {
        fetchProducts(
            location     = location.value!!,
            store        = storeName,
            resetPage    = false,
            setLoading   = { _loadPageState.value = it },
            onSuccess    = { newPage ->
                _productListState.value = (_productListState.value + newPage).toMutableList()
            }
        )
    }

    internal fun sendFlyer(imageUri: Uri, context: Context, dismissDialog: () -> Unit) {
        viewModelScope.launch {
            _sendingFlyerState.value = FlyerState.Sending
            val result = productRepository.sendFlyer(imageUri, context).getOrNull()
            delay(5_000)
            if(result != null) {
                updateProductListAndStoreTabs(result)
                _sendingFlyerState.value = null
                dismissDialog()
            } else {
                _sendingFlyerState.value = FlyerState.Error
            }
            page = 1
        }
    }

    private fun updateProductListAndStoreTabs(list: List<Product>) {
        _storeTabsState.value = getStoreTabs(list)
        _productListState.value = list.toMutableList()
    }

    private fun getStoreTabs(products: List<Product>) : List<String> {
        return products.map { it.storeName }.distinct()
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

    @SuppressLint("MissingPermission")
    internal fun requestLocation() {
        fusedLocationClient.getCurrentLocation(
            Priority.PRIORITY_HIGH_ACCURACY,
            CancellationTokenSource().token
        ).addOnSuccessListener { location ->
            if(location != null) {
                fetchProducts(
                    location = location,
                    setLoading = { _loadScreenState.value = it },
                    onSuccess = { products ->
                        updateProductListAndStoreTabs(_productListState.value + products)
                    }
                )
                _location.value = location
            } else {
                _loadScreenState.value = false
            }
        }.addOnFailureListener {
            _loadScreenState.value = false
        }
    }

}
