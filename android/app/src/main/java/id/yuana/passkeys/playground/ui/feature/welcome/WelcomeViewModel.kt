package id.yuana.passkeys.playground.ui.feature.welcome

import android.annotation.SuppressLint
import androidx.credentials.CreatePublicKeyCredentialRequest
import androidx.credentials.GetPublicKeyCredentialOption
import androidx.lifecycle.viewModelScope
import id.yuana.passkeys.playground.base.BaseViewModel
import id.yuana.passkeys.playground.data.repository.AppRepository
import id.yuana.passkeys.playground.di.json
import id.yuana.passkeys.playground.navigation.navigation.Screen
import id.yuana.passkeys.playground.navigation.navigation.UiEvent
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.getAndUpdate
import kotlinx.coroutines.launch
import kotlinx.serialization.encodeToString

class WelcomeViewModel(
    private val appRepository: AppRepository,
) : BaseViewModel<WelcomeEvent>() {

    private val _state = MutableStateFlow(WelcomeState())
    val state: StateFlow<WelcomeState> = _state.asStateFlow()

    @SuppressLint("PublicKeyCredential")
    override fun onEvent(event: WelcomeEvent) {
        when (event) {
            WelcomeEvent.OnLoginClick -> viewModelScope.launch {
                try {
                    _state.getAndUpdate { it.copy(isLoading = true) }
                    val response = appRepository.loginStart(state.value.username)
                    _state.getAndUpdate {
                        it.copy(
                            isLoading = false,
                            getPublicKeyCredentialOption = GetPublicKeyCredentialOption(
                                requestJson = json.encodeToString(
                                    response
                                )
                            )
                        )
                    }
                } catch (e: Exception) {
                    handleError(e)
                    _state.getAndUpdate { it.copy(isLoading = false) }
                }
            }

            WelcomeEvent.OnRegisterClick -> viewModelScope.launch {
                try {
                    _state.getAndUpdate { it.copy(isLoading = true) }
                    val response = appRepository.registerStart(state.value.username)
                    _state.getAndUpdate {
                        it.copy(
                            isLoading = false,
                            createPublicKeyCredentialRequest = CreatePublicKeyCredentialRequest(
                                requestJson = json.encodeToString(response)
                            )
                        )
                    }
                } catch (e: Exception) {
                    handleError(e)
                    _state.getAndUpdate { it.copy(isLoading = false) }
                }
            }

            is WelcomeEvent.OnUsernameChange -> _state.getAndUpdate { it.copy(username = event.value) }
            is WelcomeEvent.OnCreateCredentialResponseSuccess -> viewModelScope.launch {
                try {
                    _state.getAndUpdate { it.copy(isLoading = true) }
                    appRepository.registerFinish(
                        username = state.value.username,
                        response = json.decodeFromString(event.createPublicKeyCredentialResponse.registrationResponseJson)
                    )
                    _state.getAndUpdate {
                        it.copy(
                            isLoading = false,
                            createPublicKeyCredentialRequest = null
                        )
                    }

                    sendUiEvent(UiEvent.Navigate(
                        destinationRoute = Screen.Home.route
                    ) {
                        popUpTo(Screen.Welcome.route) {
                            inclusive = true
                        }
                    })

                } catch (e: Exception) {
                    handleError(e)
                    _state.getAndUpdate { it.copy(isLoading = false) }
                }

            }

            is WelcomeEvent.OnGetCredentialResponse -> viewModelScope.launch {
                try {
                    _state.getAndUpdate { it.copy(isLoading = true) }
                    appRepository.loginFinish(
                        username = state.value.username,
                        response = json.decodeFromString(event.publicKeyCredential.authenticationResponseJson)
                    )

                    _state.getAndUpdate {
                        it.copy(
                            isLoading = false,
                            getPublicKeyCredentialOption = null
                        )
                    }

                    sendUiEvent(UiEvent.Navigate(
                        destinationRoute = Screen.Home.route
                    ) {
                        popUpTo(Screen.Welcome.route) {
                            inclusive = true
                        }
                    })

                } catch (e: Exception) {
                    handleError(e)
                    _state.getAndUpdate { it.copy(isLoading = false) }
                }
            }
        }
    }
}