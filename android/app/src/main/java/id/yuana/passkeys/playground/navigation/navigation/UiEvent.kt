package coder.anthony.treasure.hunt.navigation

import androidx.navigation.NavOptionsBuilder

sealed class UiEvent {
    object PopBackStack : UiEvent()
    data class ShowToast(val message: String? = null) : UiEvent()
    object Loading : UiEvent()
    data class Navigate(
        val destinationRoute: String,
        val forcePopBackStack: Boolean = false,
        val navOptionsBuilder: (NavOptionsBuilder.() -> Unit)? = null
    ) : UiEvent()

    data class ShowDialog(val message: String? = null) : UiEvent()
}