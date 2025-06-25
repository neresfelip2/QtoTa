package br.com.qtota.data.repository

import br.com.qtota.data.local.dao.ProductDAO
import br.com.qtota.data.local.entity.Product
import br.com.qtota.data.remote.APIService
import br.com.qtota.data.remote.product.ProductResponse
import kotlinx.coroutines.flow.Flow

class ProductRepository(
    private val apiService: APIService,
    private val dao: ProductDAO
) {

    suspend fun insert(product: Product) {
        dao.insert(product)
    }

    fun getAll() : Flow<List<Product>> {
        return dao.getAll()
    }

    suspend fun delete(product: Product) {
        dao.delete(product)
    }

    suspend fun getProductById(id: Long) : Result<ProductResponse> {

        return try {
            val response = apiService.productDetail(id)

            if(response.isSuccessful) {
                response.body()?.let {
                    Result.success(it)
                } ?: Result.failure(Exception("Corpo da resposta vazio"))
            } else {
                Result.failure(
                    Exception("Erro ${response.code()}: ${response.message()}")
                )
            }
        } catch (e: Exception) {
            Result.failure(e)
        }

    }

}