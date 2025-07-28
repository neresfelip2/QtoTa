package br.com.qtota.data.repository

import android.location.Location
import br.com.qtota.data.mapper.StoreMapper.toStoreDetail
import br.com.qtota.data.remote.APIService
import br.com.qtota.data.remote.store.StoreResponse
import br.com.qtota.ui.screen.store_detail.model.StoreDetail

class StoreRespository(
    private val apiService: APIService,
) : RepositoryBase() {

    suspend fun getNearbyStores(location: Location, onError: (String) -> Unit, onSuccess: (List<StoreResponse>) -> Unit) {
        return performRequest(
            { apiService.getNearbyStores(location.latitude, location.longitude) },
            onError,
            onSuccess
        )
    }

    suspend fun getNearbyStoreBranches(storeId: Long?, location: Location, onError: (String) -> Unit, onSuccess: (List<StoreResponse>) -> Unit) {
        return performRequest(
            { apiService.getNearbyStoreBranches(storeId, location.latitude, location.longitude) },
            onError,
            onSuccess
        )
    }

    suspend fun getStoreDetail(id: Long, location: Location, onError: (String) -> Unit, onSuccess: (StoreDetail) -> Unit) {
        return performRequest(
            { apiService.getStoreDetail(id, location.latitude, location.longitude) },
            onError,
            { onSuccess(it.toStoreDetail()) }
        )
    }

}