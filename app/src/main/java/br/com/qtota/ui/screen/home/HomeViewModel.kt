package br.com.qtota.ui.screen.home

import android.annotation.SuppressLint
import android.content.Context
import android.location.Location
import android.net.Uri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import br.com.qtota.data.local.entity.Product
import br.com.qtota.data.remote.store_tabs.TabItem
import br.com.qtota.data.repository.ProductRepository
import br.com.qtota.data.repository.UserRepository
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.Priority
import com.google.android.gms.tasks.CancellationTokenSource
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
    private val fusedLocationClient: FusedLocationProviderClient
) : ViewModel() {

    private var currentTab: TabItem? = null
    private var currentPage: Int = 0

    lateinit var location: Location

    private val _storeTabsState = MutableStateFlow<List<TabItem>>(listOf())
    val storeTabsState = _storeTabsState.asStateFlow()

    private val _productListState = MutableStateFlow<MutableList<Product>>(mutableListOf())
    val productListState = _productListState.asStateFlow()

    private val _loadState = MutableStateFlow(LoadState.LoadingScreen)
    val loadListState = _loadState.asStateFlow()

    private val _sendingFlyerState = MutableStateFlow<FlyerState?>(null)
    val sendingFlyerState = _sendingFlyerState.asStateFlow()

    private val savedProductsState = productRepository.getSavedProducts()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(), emptyList())

    init {
        requestLocation()
    }

    @SuppressLint("MissingPermission")
    internal fun requestLocation() {
        _loadState.value = LoadState.LoadingScreen
        fusedLocationClient.getCurrentLocation(Priority.PRIORITY_HIGH_ACCURACY, CancellationTokenSource().token)
            .addOnSuccessListener { location ->
                if(location != null) {
                    getStoreTabs(location)
                    this.location = location
                    selectTab(null)
                }
            }.addOnFailureListener {
                _loadState.value = LoadState.LocationError
            }
    }

    internal fun loadMoreProducts() {
        fetchProducts(
            location      = location,
            storeId       = currentTab?.storeId,
            loadState = LoadState.LoadingMore)
        { newPage ->
            _productListState.value = (_productListState.value + newPage).toMutableList()
        }
    }

    internal fun selectTab(tabItem: TabItem?) {
        fetchProducts(
            location     = location,
            storeId      = tabItem?.storeId,
            loadState = LoadState.LoadingAllList)
        { firstPage ->
            this@HomeViewModel.currentTab = tabItem
            _productListState.value = firstPage.toMutableList()
        }
    }

    private fun fetchProducts(
        location: Location,
        storeId: Long? = null,
        loadState: LoadState,
        onSuccess: (List<Product>) -> Unit,
    ) {
        _loadState.value = loadState
        if (loadState == LoadState.LoadingAllList) currentPage = 1 else currentPage++

        viewModelScope.launch {
            val result =
                productRepository.getProducts(location, storeId, currentPage).getOrNull()
            if (result.isNullOrEmpty()) {
                currentPage--
                _loadState.value = LoadState.FinalList
            } else {
                _loadState.value = LoadState.ReadyToLoad
                savedProductsState.collect { savedProduct ->
                    val savedIds = savedProduct.map { it.id }.toSet()
                    result.forEach { product ->
                        product.isSaved = product.id in savedIds
                    }
                    onSuccess(result)
                }
            }
        }
    }

    internal fun sendFlyer(imageUri: Uri, context: Context, dismissDialog: () -> Unit) {
        viewModelScope.launch {
            _sendingFlyerState.value = FlyerState.Sending
            val result = productRepository.sendFlyer(imageUri, context).getOrNull()
            if(result != null) {
                _productListState.value = result.toMutableList()
                _sendingFlyerState.value = null
                dismissDialog()
            } else {
                _sendingFlyerState.value = FlyerState.Error
            }
            currentPage = 1
        }
    }

    private fun getStoreTabs(location: Location) {
        viewModelScope.launch {
            val tabs = productRepository.getNearbyStores(location)
            _storeTabsState.value = tabs
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
