package id.yuana.passkeys.playground.ui.feature.welcome

import androidx.credentials.CreatePublicKeyCredentialRequest

data class WelcomeState(
    val username: String = "",
    val isLoading: Boolean = false,
    val createPublicKeyCredentialRequest: CreatePublicKeyCredentialRequest? = null,
)
