package br.com.qtota.data.remote.home_response

import br.com.qtota.data.remote.product.ProductResponse
import br.com.qtota.data.remote.store.StoreResponse
import com.google.gson.annotations.SerializedName

data class HomeResponse(

    @SerializedName("products")
    val products: List<ProductResponse>,

    @SerializedName("nearby_stores")
    val nearbyStores: List<StoreResponse>
)