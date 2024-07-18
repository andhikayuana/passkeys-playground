package id.yuana.passkeys.playground.ui.feature.home

sealed class HomeEvent {
    object OnLoad : HomeEvent()
    object OnLogoutClick : HomeEvent()
}