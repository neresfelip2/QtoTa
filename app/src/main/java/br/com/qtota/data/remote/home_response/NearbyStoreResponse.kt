package br.com.qtota.data.remote.home_response

data class NearbyStoreResponse(
    val id: Long,
    val name: String,
    val distance: Int,
    val logo: String?,
)
