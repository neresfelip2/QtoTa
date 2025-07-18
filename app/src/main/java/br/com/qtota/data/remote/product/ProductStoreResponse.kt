package br.com.qtota.data.remote.product

import com.google.gson.annotations.SerializedName
import java.time.LocalDate

data class ProductStoreResponse(

    @SerializedName("id")
    val id: Long,

    @SerializedName("name")
    val name: String,

    @SerializedName("branch")
    val branch: String,

    @SerializedName("current_price")
    val currentPrice: Double,

    @SerializedName("discount_percentage")
    val discountPercentage: Int,

    @SerializedName("previous_price")
    val previousPrice: Double?,

    @SerializedName("expiration_offer")
    val expirationOffer: LocalDate,

    @SerializedName("distance")
    val distance: Int,

    @SerializedName("logo")
    val logo: String?
)