package br.com.qtota.ui.screen.login

import android.util.Patterns
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.navigation.NavHostController
import br.com.qtota.data.remote.login.LoginRequest
import br.com.qtota.data.repository.UserRepository
import br.com.qtota.ui.navigation.AppRoutes
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class LoginViewModel @Inject constructor(
    private val userRepository: UserRepository
) : ViewModel() {

    private val _loginState = MutableStateFlow<LoginState?>(null)
    val loginState = _loginState.asStateFlow()

    private val _emailState = MutableStateFlow("")

    private val _validEmail = MutableStateFlow(true)
    val validEmail = _validEmail.asStateFlow()

    private val _passwordState = MutableStateFlow("")

    private val _validPassword = MutableStateFlow(true)
    val validPassword = _validPassword.asStateFlow()

    fun setEmail(email: String) {
        _emailState.value = email
    }

    fun setPassword(password: String) {
        _passwordState.value = password
    }

    fun submitLogin() {
        if(validFields()) {
            _loginState.value = LoginState.Loading
            viewModelScope.launch {
                val result = userRepository.login(LoginRequest(_emailState.value, _passwordState.value))
                val response = result.getOrNull()
                if (result.isSuccess && response != null) {
                    _loginState.value = response.authToken.let { LoginState.Success(it) }
                } else {
                    _loginState.value = LoginState.Error("Não foi possível efetuar o login")
                }
            }
        }
    }

    fun resetLoginState() {
        _loginState.value = null
    }

    private fun validFields(): Boolean {
        _validEmail.value = Patterns.EMAIL_ADDRESS.matcher(_emailState.value).matches()
        _validPassword.value = _passwordState.value.length >= 6
        return _validEmail.value && _validPassword.value
    }

    fun setNotFirstAccess() {
        viewModelScope.launch {
            userRepository.setNotFirstAccess()
        }
    }

    internal fun navigateToNextScreen(navController: NavHostController) {
        if (navController.previousBackStackEntry == null) {
            navController.navigate(AppRoutes.Home.route) {
                popUpTo(navController.graph.startDestinationId) { inclusive = true }
                launchSingleTop = true
            }
        } else {
            navController.popBackStack()
        }
    }

}