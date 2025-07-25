package br.com.qtota.data.remote.store

import com.google.gson.annotations.SerializedName

data class StoreBranchResponse(

    @SerializedName("id")
    val id: Long,

    @SerializedName("id_store")
    val idStore: Long,

    @SerializedName("description")
    val description: String,

    @SerializedName("distance")
    val distance: Int,

    @SerializedName("address")
    val address: String,

    @SerializedName("latitude")
    val latitude: Double,

    @SerializedName("longitude")
    val longitude: Double

)
