package br.com.qtota.data.repository

import br.com.qtota.data.local.dao.ProductDAO
import br.com.qtota.data.local.entity.Product
import kotlinx.coroutines.flow.Flow

class ProductRepository(
    private val dao: ProductDAO
) {

    suspend fun insert(product: Product) {
        dao.insert(product)
    }

    fun getAll() : Flow<List<Product>> {
        return dao.getAll()
    }

    fun getProductById(id: Long) : Product {
        return dao.getProductById(id)
    }

    suspend fun delete(product: Product) {
        dao.delete(product)
    }

}