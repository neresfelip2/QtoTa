package br.com.qtota.data.remote.product

import com.google.gson.annotations.SerializedName

data class ProductResponse(
    val id: Long,

    @SerializedName("product_name")
    val name: String,
    val description: String,
    val weight: Int,
    val type: String,
    val origin: String,
    val expirationProduct: Int,
    val stores: List<StoreResponse>
)
