package com.adcastnetwork.kioskapp.base

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.adcastnetwork.kioskapp.navigation.UiEvent
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch

abstract class KioskAppViewModel<Event> : ViewModel() {

    private val _uiEvent = Channel<UiEvent>()
    val uiEvent = _uiEvent.receiveAsFlow()

    protected fun sendUiEvent(event: UiEvent) {
        viewModelScope.launch {
            _uiEvent.send(event)
        }
    }

    abstract fun onEvent(event: Event)

    protected fun handleError(t: Throwable) {
        sendUiEvent(UiEvent.ShowMessage(t.localizedMessage ?: "Oops, something went wrong"))
    }
}