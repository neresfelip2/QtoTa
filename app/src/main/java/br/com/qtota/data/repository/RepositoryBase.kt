package br.com.qtota.data.repository

import android.util.Log
import retrofit2.Response

open class RepositoryBase {

    protected suspend fun <RESPONSE, OBJ> performRequest(executeRequest: suspend () -> Response<RESPONSE>, executeMapper: (RESPONSE) -> OBJ) : OBJ? {
        return try {
            val response = executeRequest()
            if (response.isSuccessful) {
                val body = response.body()
                if (body != null) {
                    executeMapper(body)
                } else {
                    Log.d(this::class.simpleName, "${executeRequest.javaClass}: CORPO DA RESPOSTA VAZIO")
                    null
                }
            } else {
                Log.d(this::class.simpleName, "${executeRequest.javaClass}: ERRO ${response.code()}: ${response.message()}")
                null
            }
        } catch (e: Exception) {
            Log.d(this::class.simpleName, "${executeRequest.javaClass}: ERRO ${e.message}")
            null
        }
    }

}