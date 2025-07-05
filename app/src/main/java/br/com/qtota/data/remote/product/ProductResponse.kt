package br.com.qtota.data.remote.product

import com.google.gson.annotations.SerializedName
import java.time.LocalDate

data class ProductResponse(
    val id: Long,
    val name: String?,
    val description: String?,
    val weight: Int?,
    val type: String?,
    val origin: String?,
    @SerializedName("expiration_product")
    val expirationProduct: LocalDate?,
    @SerializedName("current_value")
    val currentValue: Double?,
    @SerializedName("previous_value")
    val previousValue: Double?,
    @SerializedName("store_name")
    val storeName: String?,
    val distance: Int?,
    val logo: String?,
)
