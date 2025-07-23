package br.com.qtota.data.remote.store

import com.google.gson.annotations.SerializedName

data class StoreResponse(

    @SerializedName("id")
    val id: Long,

    @SerializedName("name")
    val name: String,

    @SerializedName("branch")
    val branch: String,

    @SerializedName("latitude")
    val latitude: Double,

    @SerializedName("longitude")
    val longitude: Double,

    @SerializedName("distance")
    val distance: Int,

    @SerializedName("logo")
    val logo: String?,
)