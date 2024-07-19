package id.yuana.passkeys.playground.navigation

import androidx.navigation.NavOptionsBuilder

sealed class UiEvent {
    object PopBackStack : UiEvent()
    object Loading : UiEvent()
    data class Navigate(
        val destinationRoute: String,
        val forcePopBackStack: Boolean = false,
        val navOptionsBuilder: (NavOptionsBuilder.() -> Unit)? = null
    ) : UiEvent()

    data class ShowMessage(
        val type: Type = Type.Success,
        val message: String
    ) : UiEvent() {
        sealed class Type {
            object Error : Type()
            object Success : Type()
        }
    }
}