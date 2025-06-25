package br.com.qtota.ui.screen.product_details

import br.com.qtota.data.remote.product.ProductResponse

sealed class ProductState {
    data object Loading: ProductState()
    data class Success(val productDetail: ProductResponse): ProductState()
    data class Error(val message: String) : ProductState()
}