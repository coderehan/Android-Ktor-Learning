package com.rehan.androidktorlearning.data.remote

import com.rehan.androidktorlearning.data.remote.dto.MealListResponseDto
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.get
import io.ktor.client.request.parameter
import javax.inject.Inject

/**
 * DATA LAYER — REMOTE DATA SOURCE (this is the main Ktor learning file)
 *
 * This class talks directly to TheMealDB using the Ktor HttpClient that gets
 * injected by Hilt (built in data/di/NetworkModule.kt).
 *
 * The pattern to notice on every call:
 *   1. client.get(url) { ... }   — builds and sends a GET request. The trailing
 *      lambda is Ktor's "request builder" — this is where you'd add headers,
 *      query parameters, etc.
 *   2. .body<T>()                — suspends until the response arrives, then
 *      deserializes the JSON body into the type T using the ContentNegotiation
 *      plugin (kotlinx.serialization) installed on the client.
 *
 * There's no interface/annotation/codegen step like Retrofit — you're just
 * calling a suspend function that returns already-parsed Kotlin data.
 */
class MealApiService @Inject constructor(
    private val client: HttpClient
) {
    companion object {
        private const val BASE_URL = "https://www.themealdb.com/api/json/v1/1"
    }

    /**
     * Searches meals by name, e.g. query = "chicken".
     * Endpoint: GET /search.php?s={query}
     */
    suspend fun searchMeals(query: String): MealListResponseDto {
        return client.get("$BASE_URL/search.php") {
            parameter("s", query)   // adds ?s=chicken to the URL
        }.body()
    }

    /**
     * Looks up one meal by id, e.g. id = "52772".
     * Endpoint: GET /lookup.php?i={id}
     * Reuses MealListResponseDto because TheMealDB wraps a single result the
     * same way it wraps a list: {"meals": [ {...} ]}.
     */
    suspend fun getMealById(id: String): MealListResponseDto {
        return client.get("$BASE_URL/lookup.php") {
            parameter("i", id)
        }.body()
    }
}
