package br.com.qtota.data.repository

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import br.com.qtota.data.local.UserPreferencesKeys
import br.com.qtota.data.remote.APIService
import br.com.qtota.data.remote.login.LoginRequest
import br.com.qtota.data.remote.login.LoginResponse
import br.com.qtota.data.remote.login.LoginResponseError
import br.com.qtota.data.remote.login.RegisterRequest
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.map

class UserRepository(
    private val apiService: APIService,
    private val dataStore: DataStore<Preferences>
) : RepositoryBase() {

    val authTokenFlow: Flow<String?> = dataStore.data
        .map { prefs ->
            prefs[UserPreferencesKeys.AUTH_TOKEN]
        }


    suspend fun login(request: LoginRequest, onError: (message: String) -> Unit, onSuccess: (LoginResponse) -> Unit) {
        performRequest(
            { apiService.login(request) },
            LoginResponseError::class.java,
            { onError(it?.detail ?: "Erro desconhecido") }
        ) {
            saveAuthToken(it.accessToken)
            setNotFirstAccess()
            onSuccess(it)
        }
    }

    suspend fun register(request: RegisterRequest, onError: (message: String) -> Unit, onSuccess: (LoginResponse) -> Unit) {
        performRequest(
            { apiService.register(request) },
            LoginResponseError::class.java,
            { onError(it?.detail ?: "Erro desconhecido") }
        ) {
            saveAuthToken(it.accessToken)
            setNotFirstAccess()
            onSuccess(it)
        }
    }

    private suspend fun saveAuthToken(token: String) {
        dataStore.edit { prefs ->
            prefs[UserPreferencesKeys.AUTH_TOKEN] = token
        }
    }

    suspend fun deleteAuthToken() {
        dataStore.edit { prefs ->
            prefs.remove(UserPreferencesKeys.AUTH_TOKEN)
        }
    }

    suspend fun getIsFirstAccess() : Boolean {
        return dataStore.data
            .map { prefs ->
                prefs[UserPreferencesKeys.IS_FIRST_ACCESS]
            }.firstOrNull() != false
    }

    suspend fun setNotFirstAccess() {
        dataStore.edit { prefs ->
            prefs[UserPreferencesKeys.IS_FIRST_ACCESS] = false
        }
    }

}