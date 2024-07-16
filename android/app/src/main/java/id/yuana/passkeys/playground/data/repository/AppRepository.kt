package id.yuana.passkeys.playground.data.repository

import id.yuana.passkeys.playground.data.source.remote.ApiService
import id.yuana.passkeys.playground.data.source.remote.RequestFinish
import id.yuana.passkeys.playground.data.source.remote.RequestStart
import kotlinx.serialization.json.JsonObject

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

    class Impl(
        private val apiService: ApiService
    ) : AppRepository {
        override suspend fun registerStart(username: String): JsonObject =
            apiService.registerStart(RequestStart(username))

        override suspend fun registerFinish(
            username: String,
            response: JsonObject
        ): JsonObject = apiService.registerFinish(RequestFinish(username, response))

        override suspend fun loginStart(username: String): JsonObject =
            apiService.loginStart(RequestStart(username))

        override suspend fun loginFinish(username: String, response: JsonObject): JsonObject =
            apiService.loginFinish(RequestFinish(username, response))

    }
}