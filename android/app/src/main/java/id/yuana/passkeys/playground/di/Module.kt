package id.yuana.passkeys.playground.di

import android.content.Context
import android.content.SharedPreferences
import androidx.credentials.CredentialManager
import id.yuana.passkeys.playground.BuildConfig
import id.yuana.passkeys.playground.data.repository.AppRepository
import id.yuana.passkeys.playground.data.source.remote.ApiService
import id.yuana.passkeys.playground.ui.feature.home.HomeViewModel
import id.yuana.passkeys.playground.ui.feature.splash.SplashViewModel
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


val dataModule = module {
    factory { provideOkHttpClient(get()) }
    factory { provideRetrofit(get()) }

    single { provideApiService(get()) }
    single<SharedPreferences> { provideSharedPreferences(get()) }
    single<AppRepository> { AppRepository.Impl(get(), get()) }
}

val appModule = module {
    single<CredentialManager> { CredentialManager.create(get()) }

    viewModel { SplashViewModel(get()) }
    viewModel { WelcomeViewModel(get()) }
    viewModel { HomeViewModel(get()) }
}

@OptIn(ExperimentalSerializationApi::class)
val json = Json {
    ignoreUnknownKeys = true
    explicitNulls = false
    prettyPrint = true
}

internal fun provideOkHttpClient(sharedPreferences: SharedPreferences): OkHttpClient =
    OkHttpClient.Builder()
        .addInterceptor { chain ->
            val requestBuilder = chain.request()
                .newBuilder()
                .addHeader("Accept", "application/json")
                .addHeader(
                    "Authorization",
                    "Bearer ${sharedPreferences.getString("token", "")}"
                )

            chain.proceed(requestBuilder.build())
        }
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

internal fun provideSharedPreferences(context: Context): SharedPreferences =
    context.getSharedPreferences(
        "${BuildConfig.APPLICATION_ID}_cache",
        Context.MODE_PRIVATE
    )