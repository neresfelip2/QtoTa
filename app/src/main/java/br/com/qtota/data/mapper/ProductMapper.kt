package br.com.qtota.data.mapper

import br.com.qtota.data.local.entity.Product
import br.com.qtota.data.remote.product.ProductResponse
import br.com.qtota.ui.screen.product_details.ProductDetail
import java.time.LocalDate

object ProductMapper {

    fun ProductResponse.toProduct(storeName: String? = null): Product {
        return Product(
            id = this.id,
            name = this.name ?: "",
            description = this.description ?: "",
            currentValue = this.currentValue ?: 0.0,
            previousValue = null,
            storeName = this.storeName ?: "",
            distance = this.distance ?: 0,
            expirationOffer = this.expirationProduct ?: LocalDate.now(),
            logo = this.logo
        )
    }

    fun ProductResponse.toProductDetail() : ProductDetail {

        return ProductDetail(
            id = this.id,
            name = this.name ?: "",
            description = this.description ?: "",
            bestPrice = this.currentValue ?: 0.0,
            highestPrice = 0.0,
            weight = this.weight ?: 0,
            type = this.type ?: "",
            origin = this.origin ?: "",
            expiration = this.expirationProduct ?: LocalDate.now(),
            stores = listOf()
        )
    }

    fun Product.calculateDiscount(): Int {
        if(this.previousValue == null) return 0
        return (100 - 100 * (this.currentValue / this.previousValue)).toInt()
    }

}