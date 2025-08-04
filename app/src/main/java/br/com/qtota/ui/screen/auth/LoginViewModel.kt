package br.com.qtota.ui.screen.auth

import android.util.Patterns
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import br.com.qtota.data.mapper.UserMapper.tokenToUser
import br.com.qtota.data.remote.login.LoginRequest
import br.com.qtota.data.repository.UserRepository
import br.com.qtota.ui.screen.menu.User
import br.com.qtota.ui.state_handler.UIState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class LoginViewModel @Inject constructor(
    private val userRepository: UserRepository
) : ViewModel() {

    private val _emailState = MutableStateFlow("")
    val emailState = _emailState.asStateFlow()

    private val _validEmail = MutableStateFlow(true)
    val validEmail = _validEmail.asStateFlow()

    private val _passwordState = MutableStateFlow("")
    val passwordState = _passwordState.asStateFlow()

    private val _validPassword = MutableStateFlow(true)
    val validPassword = _validPassword.asStateFlow()

    private val _loginState = MutableStateFlow<UIState<User>?>(null)
    val loginState = _loginState.asStateFlow()

    fun setEmail(email: String) {
        _emailState.value = email
    }

    fun setPassword(password: String) {
        _passwordState.value = password
    }

    fun submitLogin() {
        if(validFields()) {
            _loginState.value = UIState.Loading
            viewModelScope.launch {
                val request = LoginRequest(_emailState.value, _passwordState.value)
                userRepository.login(request,
                    { _loginState.value = UIState.Error(it) },
                    { result -> _loginState.value = result.accessToken.let {
                        UIState.Success(it.tokenToUser())
                    }
                })
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

}