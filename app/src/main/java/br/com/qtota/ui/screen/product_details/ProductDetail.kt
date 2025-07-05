package br.com.qtota.ui.screen.product_details

import br.com.qtota.data.remote.product.StoreResponse
import java.time.LocalDate

data class ProductDetail(
    val id: Long,
    val name: String,
    val description: String,
    val bestPrice: Double,
    val highestPrice: Double,
    val weight: Int,
    val type: String,
    val origin: String,
    val expiration: LocalDate,
    val stores: List<StoreResponse>
)
