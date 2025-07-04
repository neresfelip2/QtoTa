package br.com.qtota.data.remote.login

import com.google.gson.annotations.SerializedName

data class LoginResponse(

    @SerializedName("token")
    val authToken: String,

)
