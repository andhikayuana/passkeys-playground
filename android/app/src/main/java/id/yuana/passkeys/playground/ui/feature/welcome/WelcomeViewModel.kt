package id.yuana.passkeys.playground.ui.feature.welcome

import android.annotation.SuppressLint
import android.util.Log
import androidx.credentials.CreatePublicKeyCredentialRequest
import androidx.lifecycle.viewModelScope
import id.yuana.passkeys.playground.base.BaseViewModel
import id.yuana.passkeys.playground.data.repository.AppRepository
import id.yuana.passkeys.playground.di.json
import id.yuana.passkeys.playground.navigation.navigation.UiEvent
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.getAndUpdate
import kotlinx.coroutines.launch
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.jsonPrimitive

class WelcomeViewModel(
    private val appRepository: AppRepository,
) : BaseViewModel<WelcomeEvent>() {

    private val _state = MutableStateFlow(WelcomeState())
    val state: StateFlow<WelcomeState> = _state.asStateFlow()

    @SuppressLint("PublicKeyCredential")
    override fun onEvent(event: WelcomeEvent) {
        when (event) {
            WelcomeEvent.OnLoginClick -> {

            }

            WelcomeEvent.OnRegisterClick -> viewModelScope.launch {
                try {
                    _state.getAndUpdate { it.copy(isLoading = true) }

                    val response = appRepository.registerStart(state.value.username)

                    Log.d("YUANA", "RESPONSE")
                    Log.d("YUANA", response["challenge"]?.jsonPrimitive?.content.orEmpty())

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

                    val result = appRepository.registerFinish(
                        username = state.value.username,
                        response = json.decodeFromString(event.createPublicKeyCredentialResponse.registrationResponseJson)
                    )

                    //save token

                    _state.getAndUpdate {
                        it.copy(
                            isLoading = false,
                            createPublicKeyCredentialRequest = null
                        )
                    }

                    sendUiEvent(UiEvent.ShowMessage(message = "Success"))

                } catch (e: Exception) {
                    handleError(e)
                    _state.getAndUpdate { it.copy(isLoading = false) }
                }

            }
        }
    }
}