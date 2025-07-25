package br.com.qtota.ui.state_handler

sealed class UIState<out T> {
    object Loading: UIState<Nothing>()
    data class Success<T>(val data: T) : UIState<T>()
    data class Error(val description: String) : UIState<Nothing>()
}