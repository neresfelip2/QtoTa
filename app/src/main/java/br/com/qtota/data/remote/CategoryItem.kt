package br.com.qtota.data.remote

import com.google.gson.annotations.SerializedName

data class CategoryItem(

    @SerializedName("id_category")
    val id: Long,

    @SerializedName("name")
    val name: String,

    @SerializedName("url_icon")
    val urlIcon: String?,
)