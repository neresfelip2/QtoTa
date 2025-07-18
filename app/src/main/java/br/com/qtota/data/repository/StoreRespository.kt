package br.com.qtota.data.repository

import br.com.qtota.data.remote.APIService
import br.com.qtota.data.remote.store.StoreResponse

class StoreRespository(
    private val apiService: APIService,
) : RepositoryBase() {

    suspend fun getNearbyStores(latitude: Double, longitude: Double): List<StoreResponse>? {
        return performRequest({
            apiService.getNearbyStores(latitude, longitude)
        }) { it }
    }

}