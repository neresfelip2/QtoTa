package br.com.qtota.data.remote

import br.com.qtota.data.remote.login.LoginRequest
import br.com.qtota.data.remote.login.LoginResponse
import br.com.qtota.data.remote.product.ProductResponse
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path

interface APIService {

    @POST("login")
    suspend fun login(@Body loginRequest: LoginRequest): Response<LoginResponse>

    @GET("product")
    suspend fun getProduct(): Response<List<ProductResponse>>

    @GET("product/{id}")
    suspend fun productDetail(@Path("id") id: Long): Response<ProductResponse>

}