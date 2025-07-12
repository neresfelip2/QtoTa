package br.com.qtota.data.remote.home_response

data class NearbyStoreResponse(
    val id: Long,
    val name: String,
    val branch: String,
    val distance: Int,
    val logo: String?,
)
