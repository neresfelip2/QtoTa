package br.com.qtota.data.remote.login

import com.google.gson.annotations.SerializedName

data class LoginResponse(

    @SerializedName("access_token")
    val accessToken: String,

    @SerializedName("refresh_token")
    val refreshToken: String,

    @SerializedName("token_type")
    val tokenType: String,

)
