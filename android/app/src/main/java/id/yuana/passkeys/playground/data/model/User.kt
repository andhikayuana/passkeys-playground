package id.yuana.passkeys.playground.data.model

import kotlinx.serialization.Serializable

@Serializable
data class User(
    val username: String = "Loading..."
)
