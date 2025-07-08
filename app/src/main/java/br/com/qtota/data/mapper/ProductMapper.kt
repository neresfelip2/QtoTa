package br.com.qtota.data.mapper

import br.com.qtota.data.local.entity.Product
import br.com.qtota.data.remote.product.ProductResponse
import br.com.qtota.ui.screen.product_details.ProductDetail
import java.time.LocalDate

object ProductMapper {

    fun ProductResponse.toProduct(storeId: Long? = null): Product {
        val store =
            if(storeId == null)
                this.stores.minBy { it.currentPrice }
            else
                this.stores.find { it.id == storeId }!!

        return Product(
            id = this.id,
            storeId = store.id,
            name = this.name,
            description = this.description,
            currentValue = store.currentPrice,
            previousValue = null,
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
            weight = this.weight,
            type = this.type,
            origin = this.origin,
            expiration = this.expirationProduct,
            stores = this.stores.sortedBy { it.currentPrice }
        )
    }

    fun Product.calculateDiscount(): Int {
        if(this.previousValue == null) return 0
        return (100 - 100 * (this.currentValue / this.previousValue)).toInt()
    }

}