package br.com.qtota.ui.screen.search_product

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import br.com.qtota.data.remote.home_response.CategoryResponse
import br.com.qtota.data.remote.product.ProductResponse
import br.com.qtota.data.repository.LocationRepository
import br.com.qtota.data.repository.ProductRepository
import br.com.qtota.ui.state_handler.LoadMoreListState
import br.com.qtota.ui.state_handler.UIState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SearchProductViewModel @Inject constructor(
    private val locationRepository: LocationRepository,
    private val productRepository: ProductRepository,
) : ViewModel() {

    val neighborhood = locationRepository.getNeighborhood()

    private val _productListState = MutableStateFlow<List<ProductResponse>>(emptyList())
    val productListState = _productListState.asStateFlow()

    private val _categoryListState = MutableStateFlow<UIState<List<CategoryResponse>>>(UIState.Loading)
    val categoryListState = _categoryListState.asStateFlow()

    private val _loadState = MutableStateFlow(LoadMoreListState.LOADING)
    val loadState = _loadState.asStateFlow()

    private var currentPage = 0
    private val limit = 10
    private var query: String = ""
    private var category: CategoryResponse? = null

    private var request: Job? = null

    init {
        getCategoryList()
    }

    internal fun getProducts(page: Int) {

        if(request != null) {
            request?.cancel()
        }

        request = viewModelScope.launch {
            _loadState.value = LoadMoreListState.LOADING

            val result = productRepository.getProducts(
                location = locationRepository.location!!,
                query = query,
                categoryId = category?.id,
                page = page,
                limit = limit
            )

            if (result == null) {
                _loadState.value = LoadMoreListState.ERROR
                return@launch
            }

            if (result.isEmpty()) {
                _loadState.value = LoadMoreListState.EMPTY
                return@launch
            }

            _loadState.value = LoadMoreListState.SUCCESS
            _productListState.value = _productListState.value + result

            currentPage = page
            request = null
        }
    }

    internal fun getCategoryList() {
        viewModelScope.launch {
            val result = productRepository.getCategories()
            if (result == null) {
                _categoryListState.value = UIState.Error("")
                return@launch
            }

            _categoryListState.value = UIState.Success(result)
        }
    }

    internal fun performSearch(newQuery: String?) {
        if (newQuery != query) {
            query = newQuery.orEmpty()
            resetPaging()
            getProducts(1)
        }
    }

    internal fun selectTab(category: CategoryResponse?) {
        if(category != this.category) {
            this.category = category
            resetPaging()
            getProducts(1)
        }
    }

    internal fun loadMore() {
        getProducts(currentPage + 1)
    }

    private fun resetPaging() {
        currentPage = 0
        _productListState.value = emptyList()
    }

}