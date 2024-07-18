package id.yuana.passkeys.playground.ui.feature.welcome

import androidx.credentials.CreatePublicKeyCredentialRequest
import androidx.credentials.GetPublicKeyCredentialOption

data class WelcomeState(
    val username: String = "",
    val isLoading: Boolean = false,
    val createPublicKeyCredentialRequest: CreatePublicKeyCredentialRequest? = null,
    val getPublicKeyCredentialOption: GetPublicKeyCredentialOption? = null,
)
