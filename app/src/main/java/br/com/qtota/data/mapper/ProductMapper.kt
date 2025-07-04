package br.com.qtota.data.mapper

import br.com.qtota.data.local.entity.Product
import br.com.qtota.data.remote.product.ProductResponse
import br.com.qtota.ui.screen.product_details.ProductDetail
import java.time.LocalDate

object ProductMapper {

    fun ProductResponse.toProduct(): Product {
        val store = this.stores.minBy { it.price }

        return Product(
            id = this.id,
            name = this.name,
            description = this.description,
            currentValue = store.price,
            previousValue = null,
            storeName = store.name,
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
            bestPrice = this.stores.minOf { it.price },
            highestPrice = this.stores.maxOf { it.price },
            weight = this.weight,
            type = this.type,
            origin = this.origin,
            expiration = this.expirationProduct,
            stores = this.stores
        )
    }

    fun Product.calculateDiscount(): Int {
        if(this.previousValue == null) return 0
        return (100 - 100 * (this.currentValue / this.previousValue)).toInt()
    }

}