package br.com.qtota.ui.screen.auth

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import br.com.qtota.data.repository.UserRepository
import kotlinx.coroutines.launch
import javax.inject.Inject

class AuthViewModel @Inject constructor(
    private val userRepository: UserRepository
) : ViewModel() {

    fun setNotFirstAccess() {
        viewModelScope.launch {
            userRepository.setNotFirstAccess()
        }
    }

}