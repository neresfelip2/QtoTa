package br.com.qtota.data.repository

import android.location.Location
import br.com.qtota.data.mapper.StoreMapper.toStoreDetail
import br.com.qtota.data.remote.APIService
import br.com.qtota.data.remote.store.StoreResponse
import br.com.qtota.ui.screen.store_detail.model.StoreDetail

class StoreRespository(
    private val apiService: APIService,
) : RepositoryBase() {

    suspend fun getNearbyStores(latitude: Double, longitude: Double): List<StoreResponse>? {
        return performRequest({
            apiService.getNearbyStores(latitude, longitude)
        }) { it }
    }

    suspend fun getNearbyStoreBranches(storeId: Long?, latitude: Double, longitude: Double): List<StoreResponse>? {
        return performRequest({
            apiService.getNearbyStoreBranches(storeId, latitude, longitude)
        }) { it }
    }

    suspend fun getStoreDetail(id: Long, location: Location): StoreDetail? {
        return performRequest({
            apiService.getStoreDetail(id, location.latitude, location.longitude)
        }) { it.toStoreDetail() }
    }

}