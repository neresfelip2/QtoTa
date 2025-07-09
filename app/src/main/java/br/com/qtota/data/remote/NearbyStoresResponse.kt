package br.com.qtota.data.remote

import com.google.gson.annotations.SerializedName

data class NearbyStoresResponse(

    @SerializedName("id_store")
    val storeId: Long,

    @SerializedName("name")
    val storeName: String
)