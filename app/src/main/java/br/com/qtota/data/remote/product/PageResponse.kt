package br.com.qtota.data.remote.product

import com.google.gson.annotations.SerializedName

data class PageResponse (
    @SerializedName("products")
    val products: List<ProductResponse>,

    @SerializedName("page")
    val page: Int,

    @SerializedName("page_size")
    val pageSize: Int,

    @SerializedName("total_products")
    val totalProducts: Int,

    @SerializedName("total_pages")
    val totalPages: Int

)
