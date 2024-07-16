package id.yuana.passkeys.playground.ui.feature.welcome

import androidx.credentials.CreatePublicKeyCredentialResponse

sealed class WelcomeEvent {
    data class OnUsernameChange(val value: String) : WelcomeEvent()
    data class OnCreateCredentialResponseSuccess(
        val createPublicKeyCredentialResponse: CreatePublicKeyCredentialResponse
    ) : WelcomeEvent()

    object OnRegisterClick : WelcomeEvent()
    object OnLoginClick : WelcomeEvent()
}