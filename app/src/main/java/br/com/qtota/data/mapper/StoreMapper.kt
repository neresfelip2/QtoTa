package br.com.qtota.data.mapper

import br.com.qtota.data.remote.store.StoreDetailResponse
import br.com.qtota.ui.screen.store_detail.StoreDetail
import br.com.qtota.ui.screen.store_detail.StoreDetailBranch
import br.com.qtota.ui.screen.store_detail.StoreDetailProduct
import com.google.android.gms.maps.model.LatLng

object StoreMapper {
    
    fun StoreDetailResponse.toStoreDetail(): StoreDetail {

        return StoreDetail(
            id = this.id,
            name = this.name,
            products = this.products.map {
                StoreDetailProduct(
                    id = it.id,
                    name = it.name,
                    urlImage = it.urlImage,
                    price = it.price,
                    percentageOfAverage = it.percentageOfAverage
                )
            },
            urlLogo = this.logo,
            branchList = this.branches.map {
                StoreDetailBranch(
                    description = it.description,
                    address = it.address,
                    distance = it.distance,
                    position = LatLng(it.latitude, it.longitude)
                )
            }
        )
    }
    
}