package br.com.qtota.data.repository

import android.location.Location
import br.com.qtota.data.local.dao.ProductDAO
import br.com.qtota.data.local.entity.Product
import br.com.qtota.data.mapper.ProductMapper.toProductDetail
import br.com.qtota.data.remote.APIService
import br.com.qtota.data.remote.home_response.CategoryResponse
import br.com.qtota.data.remote.home_response.HomeResponse
import br.com.qtota.data.remote.product.ProductResponse
import br.com.qtota.ui.screen.product_details.ProductDetail
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class ProductRepository(
    private val apiService: APIService,
    private val dao: ProductDAO,
) : RepositoryBase() {

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

    suspend fun getHome(latitude: Double, longitude: Double) : HomeResponse? {
        return performRequest({
            apiService.getHome(latitude, longitude)
        }) { homeResponse ->
            homeResponse
        }
    }

    suspend fun getProducts(location: Location, query: String? = null, storeId: Long? = null, categoryId: Int? = null, page: Int = 1, limit: Int = 5): List<ProductResponse>? {
        return performRequest({
            apiService.getProducts(location.latitude, location.longitude, if (query.isNullOrBlank()) null else query, storeId, categoryId, page, limit)
        }) { listProductResponse ->
            listProductResponse
        }
    }

    suspend fun getProductById(id: Long, location: Location) : ProductDetail? {
        return performRequest({
            apiService.productDetail(id, location.latitude, location.longitude)
        }) { productResponse ->
            productResponse.toProductDetail()
        }

    }

    suspend fun getCategories(): List<CategoryResponse>? {
        return performRequest({
            apiService.getCategories()
        }) { categoryList ->
            categoryList
        }
    }

    /*suspend fun sendFlyer(imageUri: Uri, context: Context): List<Product>? {
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
    }*/

}