package id.yuana.passkeys.playground.ui.feature.welcome

import androidx.credentials.CreatePublicKeyCredentialResponse
import androidx.credentials.PublicKeyCredential

sealed class WelcomeEvent {
    data class OnUsernameChange(val value: String) : WelcomeEvent()
    data class OnCreateCredentialResponseSuccess(
        val createPublicKeyCredentialResponse: CreatePublicKeyCredentialResponse
    ) : WelcomeEvent()

    data class OnGetCredentialResponse(
        val publicKeyCredential: PublicKeyCredential
    ) : WelcomeEvent()

    object OnRegisterClick : WelcomeEvent()
    object OnLoginClick : WelcomeEvent()
}