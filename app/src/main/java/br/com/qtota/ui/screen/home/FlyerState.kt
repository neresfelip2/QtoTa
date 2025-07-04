package br.com.qtota.ui.screen.home

sealed class FlyerState {
    object Sending: FlyerState()
    object Error: FlyerState()
}