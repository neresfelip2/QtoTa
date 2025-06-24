package br.com.qtota.ui.screen.login

sealed class LoginState {
    data object Loading: LoginState()
    data class Success(val authToken: String): LoginState()
    data class Error(val description: String): LoginState()
}