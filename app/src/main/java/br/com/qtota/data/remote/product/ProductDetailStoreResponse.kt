package br.com.qtota.data.remote.product

import com.google.gson.annotations.SerializedName
import java.time.LocalDate

data class ProductDetailStoreResponse(

    @SerializedName("id")
    val id: Long,

    @SerializedName("name")
    val name: String,

    @SerializedName("branch")
    val branch: String,

    @SerializedName("price")
    val currentPrice: Double,

    @SerializedName("expiration_offer")
    val expirationOffer: LocalDate,

    @SerializedName("distance")
    val distance: Int,

    @SerializedName("logo")
    val logo: String?
)