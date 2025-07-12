package br.com.qtota.ui.screen.home

import br.com.qtota.data.local.entity.Product

sealed class ListProductState {
    object Loading : ListProductState()
    data class Success(val products: List<Product>) : ListProductState()
    data class Error(val message: String) : ListProductState()
}