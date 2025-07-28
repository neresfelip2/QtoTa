package br.com.qtota.data.repository

import android.content.Context
import android.graphics.Bitmap
import android.location.Location
import br.com.qtota.data.local.dao.ProductDAO
import br.com.qtota.data.local.entity.Product
import br.com.qtota.data.mapper.ProductMapper.toProductDetail
import br.com.qtota.data.mapper.ProductMapper.toProductEntity
import br.com.qtota.data.remote.APIService
import br.com.qtota.data.remote.home_response.CategoryResponse
import br.com.qtota.data.remote.home_response.HomeResponse
import br.com.qtota.data.remote.product.ProductResponse
import br.com.qtota.ui.screen.product_detail.ProductDetail
import br.com.qtota.ui.screen.saved_offers.SavedProductUI
import br.com.qtota.ui.state_handler.UIState
import br.com.qtota.utils.BitmapUtils
import kotlinx.coroutines.flow.Flow
import java.io.File
import java.io.FileOutputStream

class ProductRepository(
    private val context: Context,
    private val apiService: APIService,
    private val dao: ProductDAO,
) : RepositoryBase() {

    suspend fun insert(product: ProductResponse) {
        val filePath = generateFilePath(product.id, product.urlImage)
        dao.insert(product.toProductEntity(filePath))
    }

    suspend fun insert(product: ProductDetail) {
        val filePath = generateFilePath(product.id, product.urlImage)
        dao.insert(product.toProductEntity(filePath))
    }

    fun getSavedProducts(): Flow<List<Product>> {
        return dao.getAll()
    }

    suspend fun getSavedProductsWithOffers(productList: List<SavedProductUI>, onError: (String) -> Unit, onSuccess: (List<SavedProductUI>) -> Unit) {
        performRequest(
            { apiService.getOffers(productList.map { it.product.id }) },
            onError,
            {
                val list = productList.map { product ->
                    product.copy(offersState = UIState.Success(it[product.product.id] ?: -1))
                }
                onSuccess(list)
            }
        )
    }

    suspend fun delete(id: Long) {
        context.deleteFile("product_${id}.png")
        dao.delete(id)
    }

    suspend fun getHome(location: Location, onError: (String) -> Unit, onSuccess: (HomeResponse) -> Unit) {
        performRequest(
            { apiService.getHome(location.latitude, location.longitude) },
            onError,
            onSuccess
        )
    }

    suspend fun getProducts(location: Location, query: String? = null, storeId: Long? = null, categoryId: Int? = null, page: Int = 1, limit: Int = 5, onError: (String) -> Unit, onSuccess: (List<ProductResponse>) -> Unit) {
        performRequest({
            apiService.getProducts(location.latitude, location.longitude, if (query.isNullOrBlank()) null else query, storeId, categoryId, page, limit)
        },
            onError,
            onSuccess
        )
    }

    suspend fun getProductById(id: Long, location: Location, onError: (String) -> Unit, onSuccess: suspend (ProductDetail) -> Unit) {
        performRequest(
            { apiService.productDetail(id, location.latitude, location.longitude) },
            onError,
            { productResponse -> onSuccess(productResponse.toProductDetail()) }
        )

    }

    suspend fun getCategories(onError: (String) -> Unit, onSuccess: (List<CategoryResponse>) -> Unit) {
        performRequest(
            { apiService.getCategories() },
            onError,
            onSuccess
        )
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

    private suspend fun generateFilePath(id: Long, urlImage: String?): String {

        val bitmap = BitmapUtils.downloadImageFromUrl(urlImage, context)

        val filename = "product_${id}.png"
        val file = File(context.filesDir, filename)
        FileOutputStream(file).use { out ->
            bitmap?.compress(Bitmap.CompressFormat.PNG, 100, out)
        }
        return file.absolutePath
    }

}