package br.com.qtota.data.remote.store

import com.google.gson.annotations.SerializedName

data class StoreDetailResponse(

    @SerializedName("id")
    val id: Long,

    @SerializedName("name")
    val name: String,

    @SerializedName("logo")
    val logo: String?,

    @SerializedName("branches")
    val branches: List<StoreBranchResponse>,

    @SerializedName("products")
    val products: List<StoreDetailProductResponse>

)
