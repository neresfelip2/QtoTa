package br.com.qtota.data.remote.store

import com.google.gson.annotations.SerializedName

data class StoreDetailProductResponse(

    @SerializedName("id")
    val id: Long,

    @SerializedName("name")
    val name: String,

    @SerializedName("url_image")
    val urlImage: String?,

    @SerializedName("price")
    val price: Double,

    @SerializedName("percentage")
    val percentageOfAverage: Int,
)
