package br.com.qtota.ui.screen.store_detail

data class StoreDetailProduct(
    val id: Long,
    val name: String,
    val urlImage: String?,
    val price: Double,
    val percentageOfAverage: Int,
)
