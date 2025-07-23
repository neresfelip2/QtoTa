package br.com.qtota.data.mapper

import br.com.qtota.data.local.entity.Product
import br.com.qtota.data.remote.product.ProductDetailResponse
import br.com.qtota.data.remote.product.ProductResponse
import br.com.qtota.ui.screen.product_details.ProductDetail

object ProductMapper {

    fun ProductDetailResponse.toProductDetail() : ProductDetail {
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

    fun ProductResponse.toProductEntity(pathImage: String?): Product {
        return Product(
            id = this.id,
            name = this.name,
            pathImage = pathImage,
        )
    }

    fun ProductDetail.toProductEntity(pathImage: String?) : Product {
        return Product(
            id = this.id,
            name = this.name,
            pathImage = pathImage,
        )
    }

}