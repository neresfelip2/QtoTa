package br.com.qtota.data.repository

import android.util.Log
import com.google.gson.Gson
import okhttp3.ResponseBody
import retrofit2.Response
import kotlin.jvm.java

open class RepositoryBase {

    protected suspend fun <RESPONSE, ERROR> performRequest(executeRequest: suspend () -> Response<RESPONSE>, errorClass: Class<ERROR>, onError: suspend (ERROR?) -> Unit = {}, onSuccess: suspend (RESPONSE) -> Unit) {
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
                val errorJson = response.errorBody()?.string()
                val error: ERROR = Gson().fromJson(errorJson, errorClass)
                onError(error)
            }
        } catch (e: Exception) {
            Log.d(this::class.simpleName, "${executeRequest.javaClass}: ERRO ${e.message}")
            onError(null)
        }
    }

    protected suspend fun <RESPONSE> performRequest(
        executeRequest: suspend () -> Response<RESPONSE>,
        onError: suspend (String) -> Unit = {},
        onSuccess: suspend (RESPONSE) -> Unit
    ) {
        performRequest(
            executeRequest = executeRequest,
            errorClass     = ResponseBody::class.java,
            onError        = { errBody ->
                onError(errBody?.string() ?: "Erro desconhecido")
            },
            onSuccess      = onSuccess
        )
    }

}