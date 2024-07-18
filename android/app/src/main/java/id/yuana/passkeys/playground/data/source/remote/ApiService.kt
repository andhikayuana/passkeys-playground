package id.yuana.passkeys.playground.data.source.remote

import id.yuana.passkeys.playground.data.model.User
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.JsonObject
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST

interface ApiService {

    @POST("api/register-start")
    suspend fun registerStart(
        @Body request: RequestStart
    ): JsonObject

    @POST("api/register-finish")
    suspend fun registerFinish(
        @Body request: RequestFinish
    ): JsonObject

    @POST("api/login-start")
    suspend fun loginStart(
        @Body request: RequestStart
    ): JsonObject

    @POST("api/login-finish")
    suspend fun loginFinish(
        @Body request: RequestFinish
    ): JsonObject

    @GET("api/profile")
    suspend fun getProfile(): GetProfileResponse
}

@Serializable
data class RequestStart(
    val username: String
)

@Serializable
data class RequestFinish(
    val username: String,
    val response: JsonObject
)

@Serializable
data class GetProfileResponse(
    val message: String,
    val profile: User
)