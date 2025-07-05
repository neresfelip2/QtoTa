package br.com.qtota.data.remote

import br.com.qtota.data.remote.login.LoginRequest
import br.com.qtota.data.remote.login.LoginResponse
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

    @GET("product.php")
    suspend fun getProduct(
        @Query("lat") latitude: Double,
        @Query("long") longitude: Double,
        @Query("store") store: String?,
        @Query("page") page: Int
    ): Response<List<ProductResponse>>

    @GET("product.php/{id}")
    suspend fun productDetail(@Path("id") id: Long): Response<ProductResponse>

    @Multipart
    @POST("send-flyer.php")
    suspend fun sendFlyer(
        @Part flyer: MultipartBody.Part
    ): Response<List<ProductResponse>>

}