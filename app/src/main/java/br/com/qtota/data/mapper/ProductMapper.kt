package br.com.qtota.data.mapper

import br.com.qtota.data.local.entity.Product
import br.com.qtota.data.remote.product.ProductResponse
import br.com.qtota.ui.screen.product_details.ProductDetail
import java.time.LocalDate

object ProductMapper {

    fun ProductResponse.toProduct(): Product {
        val store = this.stores.minBy { it.currentPrice }
        return Product(
            id = this.id,
            storeId = store.id,
            name = this.name,
            description = this.description,
            currentPrice = store.currentPrice,
            discountPercentage = store.discountPercentage,
            previousPrice = null,
            storeName = store.name,
            storeBranch = store.branch,
            distance = store.distance,
            expirationOffer = LocalDate.now(),
            logo = store.logo
        )
    }

    fun ProductResponse.toProductDetail() : ProductDetail {
        return ProductDetail(
            id = this.id,
            name = this.name,
            description = this.description,
            bestPrice = this.stores.minOf { it.currentPrice },
            highestPrice = this.stores.maxOf { it.currentPrice },
            weight = this.measure,
            type = this.type,
            measureType = this.measureType,
            origin = this.origin,
            expiration = this.expirationProduct,
            urlImage = this.urlImage,
            stores = this.stores.sortedBy { it.currentPrice }
        )
    }

    fun ProductDetail.toProduct() : Product {

        val store = this.stores[0]

        return Product(
            id = this.id,
            storeId = store.id,
            name = this.name,
            description = this.description,
            currentPrice = store.currentPrice,
            discountPercentage = store.discountPercentage,
            previousPrice = store.previousPrice,
            storeName = store.name,
            storeBranch = store.branch,
            distance = store.distance,
            expirationOffer = store.expirationOffer,
            logo = store.logo
        )

    }

}