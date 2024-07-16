package id.yuana.passkeys.playground.base

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import id.yuana.passkeys.playground.di.json
import id.yuana.passkeys.playground.navigation.navigation.UiEvent
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.jsonPrimitive
import retrofit2.HttpException

abstract class BaseViewModel<Event> : ViewModel() {

    private val _uiEvent = Channel<UiEvent>()
    val uiEvent = _uiEvent.receiveAsFlow()

    protected fun sendUiEvent(event: UiEvent) {
        viewModelScope.launch {
            _uiEvent.send(event)
        }
    }

    abstract fun onEvent(event: Event)

    fun handleError(t: Throwable) {
        var errorMessage = t.localizedMessage ?: "Oops, something went wrong"
        if (t is HttpException) {
            try {
                val errorResponse: JsonObject =
                    json.decodeFromString(t.response()?.errorBody()?.string() ?: "{}")
                errorMessage =
                    errorResponse["message"]?.jsonPrimitive?.content ?: "Oops, something went wrong"
            } catch (e: Exception) {
                errorMessage = "Oops, something went wrong"
            }

        }
        sendUiEvent(
            UiEvent.ShowMessage(
                type = UiEvent.ShowMessage.Type.Error,
                message = errorMessage
            )
        )
    }
}