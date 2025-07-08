package br.com.qtota.data.repository

import android.content.Context
import android.location.Location
import android.net.Uri
import android.util.Log
import br.com.qtota.data.local.dao.ProductDAO
import br.com.qtota.data.local.entity.Product
import br.com.qtota.data.mapper.ProductMapper.toProduct
import br.com.qtota.data.mapper.ProductMapper.toProductDetail
import br.com.qtota.data.remote.APIService
import br.com.qtota.ui.screen.product_details.ProductDetail
import br.com.qtota.utils.Utils
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class ProductRepository(
    private val apiService: APIService,
    private val dao: ProductDAO
) {

    suspend fun insert(product: Product) {
        dao.insert(product)
    }

    fun getSavedProducts() : Flow<List<Product>> {
        return dao.getAll().map { it.onEach { product -> product.isSaved = true } }
    }

    suspend fun delete(product: Product) {
        dao.delete(product)
    }

    suspend fun getProducts(location: Location, storeId: Long? = null, page: Int): Result<List<Product>> {

        return try {
            val response = apiService.getProduct(location.latitude, location.longitude, storeId, page)
            if (response.isSuccessful) {
                val body = response.body()
                if (body != null) {
                    val products = body.map { it.toProduct(storeId) }
                    Result.success(products)
                } else {
                    Log.e(ProductRepository::class.simpleName, "Corpo da resposta vazio")
                    Result.failure(Exception("Corpo da resposta vazio"))
                }
            } else {
                Log.e(ProductRepository::class.simpleName, "Erro ${response.code()}: ${response.message()}")
                Result.failure(Exception("Erro ${response.code()}: ${response.message()}"))
            }
        } catch (e: Exception) {
            Log.e(ProductRepository::class.simpleName, e.toString())
            Result.failure(e)
        }

    }

    suspend fun getProductById(id: Long, latitude: Double, longitude: Double) : Result<ProductDetail> {

        return try {
            val response = apiService.productDetail(id, latitude, longitude)

            if(response.isSuccessful) {
                response.body()?.let {
                    Result.success(it.toProductDetail())
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

    suspend fun sendFlyer(imageUri: Uri, context: Context): Result<List<Product>> {

        val multipartUri = Utils.uriToMultipart(
            context = context,
            uri = imageUri,
            fieldName = "flyer"
        )

        return try {
            val response = apiService.sendFlyer(multipartUri)
            if (response.isSuccessful) {
                val body = response.body()
                if (body != null) {
                    val products = body.map { it.toProduct() }
                    Result.success(products)
                } else {
                    Result.failure(Exception("Corpo da resposta vazio"))
                }
            } else {
                Result.failure(Exception("Erro ${response.code()}: ${response.message()}"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }

    }

}