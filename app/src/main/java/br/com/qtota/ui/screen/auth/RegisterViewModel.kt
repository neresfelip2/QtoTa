package br.com.qtota.ui.screen.auth

import android.util.Patterns
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import br.com.qtota.data.mapper.UserMapper.tokenToUser
import br.com.qtota.data.remote.login.RegisterRequest
import br.com.qtota.data.repository.UserRepository
import br.com.qtota.ui.screen.menu.User
import br.com.qtota.ui.state_handler.UIState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class RegisterViewModel @Inject constructor(
    private val userRepository: UserRepository
) : ViewModel() {

    private val _nameState = MutableStateFlow("")
    val nameState = _nameState.asStateFlow()

    private val _validName = MutableStateFlow(true)
    val validName = _validName.asStateFlow()

    private val _emailState = MutableStateFlow("")
    val emailState = _emailState.asStateFlow()

    private val _validEmail = MutableStateFlow(true)
    val validEmail = _validEmail.asStateFlow()

    private val _passwordState = MutableStateFlow("")
    val passwordState = _passwordState.asStateFlow()

    private val _validPassword = MutableStateFlow(true)
    val validPassword = _validPassword.asStateFlow()

    private val _confirmPasswordState = MutableStateFlow("")
    val confirmPasswordState = _confirmPasswordState.asStateFlow()

    private val _validConfirmPassword = MutableStateFlow(true)
    val validConfirmPassword = _validConfirmPassword.asStateFlow()

    private val _registerState = MutableStateFlow<UIState<User>?>(null)
    val registerState = _registerState.asStateFlow()

    fun setName(name: String) {
        _nameState.value = name
    }

    fun setEmail(email: String) {
        _emailState.value = email
    }

    fun setPassword(password: String) {
        _passwordState.value = password
    }

    fun setConfirmPassword(password: String) {
        _confirmPasswordState.value = password
    }

    fun submitRegister() {
        if(validFields()) {
            _registerState.value = UIState.Loading
            viewModelScope.launch {
                val request = RegisterRequest(_nameState.value, _emailState.value, _passwordState.value)
                userRepository.register(request,
                    { _registerState.value = UIState.Error(it) }
                ) {
                    _registerState.value = UIState.Success(it.accessToken.tokenToUser())
                }
            }
        }
    }

    private fun validFields() : Boolean {
        _validName.value = _nameState.value.isNotBlank()
        _validEmail.value = Patterns.EMAIL_ADDRESS.matcher(_emailState.value).matches()
        _validPassword.value = _passwordState.value.length >= 6
        _validConfirmPassword.value = _passwordState.value == _confirmPasswordState.value
        return _validEmail.value && _validPassword.value && _validConfirmPassword.value
    }

    fun resetRegisterState() {
        _registerState.value = null
    }

}