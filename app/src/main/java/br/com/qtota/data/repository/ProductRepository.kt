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
import br.com.qtota.data.remote.CategoryItem
import br.com.qtota.data.remote.store_tabs.StoreItem
import br.com.qtota.ui.screen.product_details.ProductDetail
import br.com.qtota.utils.Utils
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import retrofit2.Response

class ProductRepository(
    private val apiService: APIService,
    private val dao: ProductDAO,
) {

    suspend fun insert(product: Product) {
        dao.insert(product)
    }

    fun getSavedProducts(): Flow<List<Product>> {
        return dao.getAll().map { productList ->
            productList.onEach { it.isSaved = true }
        }
    }

    suspend fun delete(product: Product) {
        dao.delete(product)
    }

    suspend fun getProducts(storeId: Long? = null, location: Location, page: Int): List<Product>? {
        return performRequest({
            apiService.getProduct(storeId, location.latitude, location.longitude, page)
        }) { pageResponse ->
            pageResponse.products.map { it.toProduct(storeId) }
        }
    }

    suspend fun getProductById(id: Long, location: Location) : ProductDetail? {
        return performRequest({
            apiService.productDetail(id, location.latitude, location.longitude)
        }) { productResponse ->
            productResponse.toProductDetail()
        }

    }

    suspend fun getCategories() : List<CategoryItem>? {
        return performRequest(
            { apiService.getCategories() }
        ) { listCategories ->
            listCategories
        }
    }

    suspend fun getNearbyStores(location: Location) : List<StoreItem>? {
        return performRequest({
            apiService.nearbyStores(location.latitude, location.longitude)
        }) { listNearbyStores ->
            listNearbyStores
        }
    }

    suspend fun sendFlyer(imageUri: Uri, context: Context): List<Product>? {

        val multipartUri = Utils.uriToMultipart(
            context = context,
            uri = imageUri,
            fieldName = "flyer"
        )

        return performRequest({
            apiService.sendFlyer(multipartUri)
        }) { listProductsResponse ->
            listProductsResponse.map {it.toProduct()}
        }
    }

    private suspend fun <RESPONSE, OBJ> performRequest(executeRequest: suspend () -> Response<RESPONSE>, executeMapper: (RESPONSE) -> OBJ) : OBJ? {
        return try {
            val response = executeRequest()
            if (response.isSuccessful) {
                val body = response.body()
                if (body != null) {
                    executeMapper(body)
                } else {
                    Log.d(ProductRepository::class.simpleName, "${executeRequest.javaClass}: CORPO DA RESPOSTA VAZIO")
                    null
                }
            } else {
                Log.d(ProductRepository::class.simpleName, "${executeRequest.javaClass}: ERRO ${response.code()}: ${response.message()}")
                null
            }
        } catch (e: Exception) {
            Log.d(ProductRepository::class.simpleName, "${executeRequest.javaClass}: ERRO ${e.message}")
            null
        }
    }

}