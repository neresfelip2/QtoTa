package br.com.qtota.data.remote.product

import br.com.qtota.data.remote.store.StoreResponse
import com.google.gson.annotations.SerializedName

data class ProductResponse (

    @SerializedName("id")
    val id: Long,

    @SerializedName("name")
    val name: String,

    @SerializedName("description")
    val description: String,

    @SerializedName("price")
    val price: Double,

    @SerializedName("percentage")
    val percentageOfAverage: Int,

    @SerializedName("url_image")
    val urlImage: String?,

    @SerializedName("store")
    val store: StoreResponse

)