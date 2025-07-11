package br.com.qtota.ui.screen.home

import android.annotation.SuppressLint
import android.content.Context
import android.location.Location
import android.net.Uri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import br.com.qtota.data.local.entity.Product
import br.com.qtota.data.remote.CategoryItem
import br.com.qtota.data.repository.LocationRepository
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
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val userRepository: UserRepository,
    private val productRepository: ProductRepository,
    private val locationRepository: LocationRepository
) : ViewModel() {

    private var currentTab: CategoryItem? = null
    private var currentPage: Int = 0

    private val _storeTabsState = MutableStateFlow<List<CategoryItem>>(listOf())
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
        viewModelScope.launch {
            locationRepository.loadStatus.collect { isLoading ->

                if (isLoading) {
                    _loadState.value = LoadState.LoadingScreen
                    return@collect
                }

                if (locationRepository.location == null) {
                    _loadState.value = LoadState.LocationError
                    return@collect
                }

                getCategoryTabs()
                fetchProducts(
                    location = locationRepository.location!!,
                    categoryId = null,
                    loadState = LoadState.LoadingScreen
                ) { firstPage ->
                    _productListState.value = firstPage.toMutableList()
                }

            }
        }
        locationRepository.startLocationUpdates()
    }

    internal fun loadMoreProducts() {
        fetchProducts(
            location      = locationRepository.location!!,
            categoryId       = currentTab?.id,
            loadState = LoadState.LoadingMore)
        { newPage ->
            _productListState.value = (_productListState.value + newPage).toMutableList()
        }
    }

    internal fun selectTab(category: CategoryItem?) {
        fetchProducts(
            location     = locationRepository.location!!,
            categoryId      = category?.id,
            loadState = LoadState.LoadingAllList)
        { firstPage ->
            this@HomeViewModel.currentTab = category
            _productListState.value = firstPage.toMutableList()
        }
    }

    private fun fetchProducts(
        location: Location,
        categoryId: Long? = null,
        loadState: LoadState,
        onSuccess: (List<Product>) -> Unit,
    ) {
        _loadState.value = loadState
        if (loadState == LoadState.LoadingAllList) currentPage = 1 else currentPage++

        viewModelScope.launch {
            val products = productRepository.getProducts(categoryId, location, currentPage)

            if(products == null) {
                currentPage--
                _loadState.value = LoadState.GetProductError
                return@launch
            }

            if (products.isEmpty()) {
                currentPage--
                _loadState.value = LoadState.FinalList
            } else {
                _loadState.value = LoadState.ReadyToLoad
                savedProductsState.collect { savedProduct ->
                    val savedIds = savedProduct.map { it.id }.toSet()
                    products.forEach { product ->
                        product.isSaved = product.id in savedIds
                    }
                    onSuccess(products)
                }
            }

        }
    }

    internal fun sendFlyer(imageUri: Uri, context: Context, dismissDialog: () -> Unit) {
        viewModelScope.launch {
            _sendingFlyerState.value = FlyerState.Sending
            val products = productRepository.sendFlyer(imageUri, context)
            if(products != null) {
                _productListState.value = products.toMutableList()
                _sendingFlyerState.value = null
                dismissDialog()
            } else {
                _sendingFlyerState.value = FlyerState.Error
            }
            currentPage = 1
        }
    }

    private fun getCategoryTabs() {
        viewModelScope.launch {
            val tabs = productRepository.getCategories()
            _storeTabsState.value = tabs ?: listOf()
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
