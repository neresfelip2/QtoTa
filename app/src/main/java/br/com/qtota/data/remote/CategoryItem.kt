package br.com.qtota.data.remote

import com.google.gson.annotations.SerializedName

data class CategoryItem(

    @SerializedName("id")
    val id: Int,

    @SerializedName("name")
    val name: String,

    @SerializedName("url_icon")
    val urlIcon: String?,
)