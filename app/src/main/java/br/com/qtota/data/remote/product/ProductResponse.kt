package br.com.qtota.data.remote.product

import com.google.gson.annotations.SerializedName

data class ProductResponse(

    val id: Long,

    @SerializedName("product_name")
    val productName: String,

    @SerializedName("product_store")
    val productStore: String,

    @SerializedName("best_price")
    val bestPrice: Double,

    @SerializedName("highest_price")
    val highestPrice: Double,

    val weight: Int,
    val type: String,
    val origin: String,
    val expiration: Int,
    val stores: List<StoreResponse>
)
