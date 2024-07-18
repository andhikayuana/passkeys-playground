package id.yuana.passkeys.playground.data.repository

import android.content.SharedPreferences
import androidx.core.content.edit
import id.yuana.passkeys.playground.data.model.User
import id.yuana.passkeys.playground.data.source.remote.ApiService
import id.yuana.passkeys.playground.data.source.remote.RequestFinish
import id.yuana.passkeys.playground.data.source.remote.RequestStart
import id.yuana.passkeys.playground.di.json
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.jsonPrimitive

interface AppRepository {

    suspend fun registerStart(
        username: String
    ): JsonObject

    suspend fun registerFinish(
        username: String,
        response: JsonObject
    ): JsonObject

    suspend fun loginStart(
        username: String
    ): JsonObject

    suspend fun loginFinish(
        username: String,
        response: JsonObject
    ): JsonObject

    fun getToken(): String

    suspend fun logout()

    suspend fun getProfile(): User

    class Impl(
        private val apiService: ApiService,
        private val sharedPreferences: SharedPreferences
    ) : AppRepository {

        companion object {
            const val KEY_TOKEN = "token"
            const val KEY_PROFILE = "user"
        }

        override suspend fun registerStart(username: String): JsonObject =
            apiService.registerStart(RequestStart(username))

        override suspend fun registerFinish(
            username: String,
            response: JsonObject
        ): JsonObject = apiService.registerFinish(RequestFinish(username, response)).also {
            sharedPreferences.edit {
                putString(KEY_TOKEN, it["token"]?.jsonPrimitive?.content.orEmpty())
            }
        }

        override suspend fun loginStart(username: String): JsonObject =
            apiService.loginStart(RequestStart(username))

        override suspend fun loginFinish(username: String, response: JsonObject): JsonObject =
            apiService.loginFinish(RequestFinish(username, response)).also {
                sharedPreferences.edit {
                    putString(KEY_TOKEN, it["token"]?.jsonPrimitive?.content.orEmpty())
                }
            }

        override fun getToken(): String {
            return sharedPreferences.getString(KEY_TOKEN, "").orEmpty()
        }

        override suspend fun logout() {
            sharedPreferences.edit { clear() }
        }

        override suspend fun getProfile(): User {
            val profile: User = sharedPreferences.getString(KEY_PROFILE, null)?.let {
                json.decodeFromString<User>(it)
            } ?: apiService.getProfile().let {
                sharedPreferences.edit {
                    putString(KEY_PROFILE, json.encodeToString(it.profile))
                }
                it.profile
            }
            return profile
        }

    }
}