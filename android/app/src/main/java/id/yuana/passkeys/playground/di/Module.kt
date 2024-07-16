package id.yuana.passkeys.playground.di

import androidx.credentials.CredentialManager
import id.yuana.passkeys.playground.BuildConfig
import id.yuana.passkeys.playground.data.repository.AppRepository
import id.yuana.passkeys.playground.data.source.remote.ApiService
import id.yuana.passkeys.playground.ui.feature.welcome.WelcomeViewModel
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.json.Json
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module
import retrofit2.Retrofit
import retrofit2.converter.kotlinx.serialization.asConverterFactory

val appModule = module {
    single<CredentialManager> { CredentialManager.create(get()) }

    viewModel { WelcomeViewModel(get()) }
}

val dataModule = module {
    factory { provideOkHttpClient() }
    factory { provideRetrofit(get()) }

    single { provideApiService(get()) }
    single<AppRepository> { AppRepository.Impl(get()) }
}

@OptIn(ExperimentalSerializationApi::class)
val json = Json {
    ignoreUnknownKeys = true
    explicitNulls = false
    prettyPrint = true
}

internal fun provideOkHttpClient(): OkHttpClient = OkHttpClient.Builder()
    .addInterceptor(HttpLoggingInterceptor().apply {
        setLevel(
            when {
                BuildConfig.DEBUG -> HttpLoggingInterceptor.Level.BODY
                else -> HttpLoggingInterceptor.Level.NONE
            }
        )
    })
    .build()

internal fun provideRetrofit(okHttpClient: OkHttpClient): Retrofit = Retrofit.Builder()
    .baseUrl(BuildConfig.API_BASE_URL)
    .client(okHttpClient)
    .addConverterFactory(
        json.asConverterFactory(
            "application/json; charset=UTF8".toMediaType()
        )
    )
    .build()

internal fun provideApiService(retrofit: Retrofit): ApiService =
    retrofit.create(ApiService::class.java)