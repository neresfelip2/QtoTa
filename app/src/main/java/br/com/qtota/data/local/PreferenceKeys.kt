package br.com.qtota.data.local

import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey

object UserPreferencesKeys {
    val IS_FIRST_ACCESS = booleanPreferencesKey("first_access")
    val AUTH_TOKEN = stringPreferencesKey("auth_token")
}