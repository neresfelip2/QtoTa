package br.com.qtota

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import br.com.qtota.data.repository.UserRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MainViewModel @Inject constructor(
    userRepository: UserRepository
) : ViewModel() {

    private val _isFirstAccess = MutableStateFlow<Boolean?>(null)
    val isFirstAccess = _isFirstAccess.asStateFlow()

    init {
        viewModelScope.launch {
            _isFirstAccess.value = userRepository.getIsFirstAccess()
        }
    }

}