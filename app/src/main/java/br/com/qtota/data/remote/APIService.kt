package br.com.qtota.data.remote

import br.com.qtota.data.remote.home_response.HomeResponse
import br.com.qtota.data.remote.store.StoreResponse
import br.com.qtota.data.remote.login.LoginRequest
import br.com.qtota.data.remote.login.LoginResponse
import br.com.qtota.data.remote.product.ProductDetailResponse
import br.com.qtota.data.remote.product.ProductResponse
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

    @GET("home")
    suspend fun getHome(
        @Query("lat") latitude: Double,
        @Query("lon") longitude: Double,
    ) : Response<HomeResponse>


    @GET("product")
    suspend fun getProducts(
        @Query("lat") latitude: Double,
        @Query("lon") longitude: Double,
        @Query("query") query: String?,
        @Query("id_category") categoryId: Int?,
        @Query("page") page: Int,
        @Query("limit") limit: Int
    ): Response<List<ProductResponse>>

    @GET("product/{id}")
    suspend fun productDetail(
        @Path("id") id: Long,
        @Query("lat") latitude: Double,
        @Query("lon") longitude: Double,
    ): Response<ProductDetailResponse>

    @GET("store")
    suspend fun getNearbyStores(
        @Query("lat") latitude: Double,
        @Query("lon") longitude: Double,
    ) : Response<List<StoreResponse>>


    @Multipart
    @POST("send-flyer")
    suspend fun sendFlyer(
        @Part flyer: MultipartBody.Part
    ): Response<List<ProductDetailResponse>>

}