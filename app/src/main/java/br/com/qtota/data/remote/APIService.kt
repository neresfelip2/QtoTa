package br.com.qtota.data.remote

import br.com.qtota.data.remote.login.LoginRequest
import br.com.qtota.data.remote.login.LoginResponse
import br.com.qtota.data.remote.product.PageResponse
import br.com.qtota.data.remote.product.ProductResponse
import br.com.qtota.data.remote.store_tabs.StoreItem
import okhttp3.MultipartBody
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.Multipart
import retrofit2.http.POST
import retrofit2.http.Part
import retrofit2.http.Path
import retrofit2.http.Query

interface APIService {

    @POST("login")
    suspend fun login(@Body loginRequest: LoginRequest): Response<LoginResponse>

    @GET("product")
    suspend fun getProduct(
        @Query("id_store") storeId: Long?,
        @Query("lat") latitude: Double,
        @Query("lon") longitude: Double,
        @Query("page") page: Int,
    ): Response<PageResponse>

    @GET("product/{id}")
    suspend fun productDetail(
        @Path("id") id: Long,
        @Query("lat") latitude: Double,
        @Query("lon") longitude: Double,
    ): Response<ProductResponse>

    @GET("product/categories")
    suspend fun getCategories(): Response<List<CategoryItem>>

    @GET("product/nearby-stores")
    suspend fun nearbyStores(
        @Query("lat") latitude: Double,
        @Query("lon") longitude: Double,
    ): Response<List<StoreItem>>

    @Multipart
    @POST("send-flyer")
    suspend fun sendFlyer(
        @Part flyer: MultipartBody.Part
    ): Response<List<ProductResponse>>

}