package com.rehan.androidktorlearning.data.di

import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import io.ktor.client.HttpClient
import io.ktor.client.engine.android.Android
import io.ktor.client.plugins.HttpTimeout
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.plugins.logging.LogLevel
import io.ktor.client.plugins.logging.Logging
import io.ktor.serialization.kotlinx.json.json
import kotlinx.serialization.json.Json
import javax.inject.Singleton

/**
 * DATA LAYER — DI (Ktor client setup)
 *
 * This is where the Ktor HttpClient is actually built, as a single Hilt-managed
 * singleton shared by the whole app. Compare this to Retrofit: Retrofit hides
 * most of this behind a builder with sensible defaults. Ktor makes you choose
 * an ENGINE and install PLUGINS explicitly — that explicitness is exactly what's
 * worth studying here.
 */
@Module
@InstallIn(SingletonComponent::class)
object NetworkModule {

    @Provides
    @Singleton
    fun provideHttpClient(): HttpClient {
        return HttpClient(Android) {
            // ENGINE: "Android" runs on top of HttpURLConnection. Ktor also has
            // OkHttp, CIO, and other engines — swapping this line is often the
            // only change needed to switch engines, since everything below
            // (plugins) is engine-agnostic.

            // PLUGIN: ContentNegotiation — automatically converts JSON <-> Kotlin
            // objects using kotlinx.serialization, based on the DTO's @Serializable
            // annotations. Without this plugin, .body<T>() wouldn't know how to
            // parse the response.
            install(ContentNegotiation) {
                json(Json {
                    ignoreUnknownKeys = true  // don't crash if TheMealDB adds new fields later
                    isLenient = true
                })
            }

            // PLUGIN: Logging — prints full request/response info to Logcat
            // (filter by tag "HttpClient"). Extremely useful while learning Ktor,
            // since you can literally see the URL, headers, and JSON body Ktor
            // sends and receives for every call.
            install(Logging) {
                level = LogLevel.BODY
            }

            // PLUGIN: HttpTimeout — sets how long to wait before giving up.
            install(HttpTimeout) {
                requestTimeoutMillis = 15_000
                connectTimeoutMillis = 15_000
            }
        }
    }
}
