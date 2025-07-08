package br.com.qtota.data.remote.product

import com.google.gson.annotations.SerializedName

data class ProductResponse(

    @SerializedName("id")
    val id: Long,

    @SerializedName("name")
    val name: String,

    @SerializedName("description")
    val description: String,

    @SerializedName("weight")
    val weight: Int,

    @SerializedName("type")
    val type: String,

    @SerializedName("origin")
    val origin: String,

    @SerializedName("expiration")
    val expirationProduct: Int,

    @SerializedName("stores")
    val stores: List<StoreResponse>
)
