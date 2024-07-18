package id.yuana.passkeys.playground.ui.feature.splash

import id.yuana.passkeys.playground.base.BaseViewModel
import id.yuana.passkeys.playground.data.repository.AppRepository
import id.yuana.passkeys.playground.navigation.navigation.Screen
import id.yuana.passkeys.playground.navigation.navigation.UiEvent

class SplashViewModel(
    private val appRepository: AppRepository
) : BaseViewModel<SplashEvent>() {

    override fun onEvent(event: SplashEvent) {
        when (event) {
            SplashEvent.OnLoad -> {
                val alreadyLogin = appRepository.getToken().isNotEmpty()
                sendUiEvent(UiEvent.Navigate(
                    destinationRoute = if (alreadyLogin) Screen.Home.route else Screen.Welcome.route
                ) {
                    popUpTo(Screen.Splash.route) {
                        inclusive = true
                    }
                })
            }
        }
    }
}