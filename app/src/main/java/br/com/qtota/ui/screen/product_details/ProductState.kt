package br.com.qtota.ui.screen.product_details

sealed class ProductState {
    data object Loading: ProductState()
    data class Success(val productDetail: ProductDetail): ProductState()
    data class Error(val message: String) : ProductState()
}