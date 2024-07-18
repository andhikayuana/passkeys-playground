package id.yuana.passkeys.playground.ui.feature.home

import androidx.lifecycle.viewModelScope
import id.yuana.passkeys.playground.base.BaseViewModel
import id.yuana.passkeys.playground.data.repository.AppRepository
import id.yuana.passkeys.playground.navigation.navigation.Screen
import id.yuana.passkeys.playground.navigation.navigation.UiEvent
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.getAndUpdate
import kotlinx.coroutines.launch

class HomeViewModel(
    private val appRepository: AppRepository
) : BaseViewModel<HomeEvent>() {

    private val _state = MutableStateFlow(HomeState())
    val state: StateFlow<HomeState> = _state.asStateFlow()

    override fun onEvent(event: HomeEvent) {
        when (event) {
            HomeEvent.OnLoad -> viewModelScope.launch {
                try {
                    val profile = appRepository.getProfile()
                    _state.getAndUpdate { it.copy(profile = profile) }
                } catch (e: Exception) {
                    handleError(e)
                }
            }

            HomeEvent.OnLogoutClick -> viewModelScope.launch {
                appRepository.logout()
                sendUiEvent(UiEvent.Navigate(
                    destinationRoute = Screen.Welcome.route
                ) {
                    popUpTo(Screen.Home.route) {
                        inclusive = true
                    }
                })
            }
        }
    }
}