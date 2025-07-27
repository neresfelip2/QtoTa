package br.com.qtota.ui.screen.main_navigation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import br.com.qtota.data.repository.UserRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MainNavigationViewModel @Inject constructor(
    private val userRepository: UserRepository,
) : ViewModel() {

    private val _isFirstAccess = MutableStateFlow<Boolean?>(null)
    val isFirstAccess = _isFirstAccess.asStateFlow()

    init {
        viewModelScope.launch {
            _isFirstAccess.value = userRepository.getIsFirstAccess()
        }
    }

    internal fun checkIfLogged(onResult: (Boolean) -> Unit) {
        viewModelScope.launch {
            val isLogged = userRepository.authTokenFlow
                .map { !it.isNullOrEmpty() }
                .first()
            onResult(isLogged)
        }
    }

}