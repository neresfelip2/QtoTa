package br.com.qtota.data.remote.home_response

import com.google.gson.annotations.SerializedName

data class CategoryResponse(

    @SerializedName("id")
    val id: Int,

    @SerializedName("name")
    val name: String,

    @SerializedName("url_icon")
    val urlIcon: String?,
)