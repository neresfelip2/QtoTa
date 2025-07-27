package br.com.qtota.data.repository

import android.util.Log
import okhttp3.ResponseBody
import retrofit2.Response

open class RepositoryBase {

    protected suspend fun <RESPONSE, OBJ> performRequest(executeRequest: suspend () -> Response<RESPONSE>, onError: suspend (ResponseBody?) -> Unit = {}, onSuccess: suspend (RESPONSE) -> OBJ) {
        try {
            val response = executeRequest()
            if (response.isSuccessful) {
                val body = response.body()
                if (body != null) {
                    onSuccess(body)
                } else {
                    Log.d(this::class.simpleName, "${executeRequest.javaClass}: CORPO DA RESPOSTA VAZIO")
                    onError(body)
                }
            } else {
                Log.d(this::class.simpleName, "${executeRequest.javaClass}: ERRO ${response.code()}: ${response.message()}")
                onError(response.errorBody())
            }
        } catch (e: Exception) {
            Log.d(this::class.simpleName, "${executeRequest.javaClass}: ERRO ${e.message}")
            onError(null)
        }
    }

}