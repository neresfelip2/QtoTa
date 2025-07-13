package br.com.qtota.data.remote.store_tabs

import com.google.gson.annotations.SerializedName

data class StoreItem(

    @SerializedName("id_store")
    val storeId: Long,

    @SerializedName("name")
    val storeName: String
)