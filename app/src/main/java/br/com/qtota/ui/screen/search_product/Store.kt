package br.com.qtota.ui.screen.search_product

import com.google.gson.annotations.SerializedName

data class Store(

    @SerializedName("store")
    val id: Long,

    @SerializedName("name")
    val name: String,

    @SerializedName("logo")
    val urlLogo: String?,
)
