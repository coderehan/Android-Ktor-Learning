package com.rehan.androidktorlearning.data.repository

import com.rehan.androidktorlearning.data.remote.MealApiService
import com.rehan.androidktorlearning.data.remote.dto.MealDto
import com.rehan.androidktorlearning.domain.model.Ingredient
import com.rehan.androidktorlearning.domain.model.MealDetail
import com.rehan.androidktorlearning.domain.model.MealSummary
import com.rehan.androidktorlearning.domain.repository.MealRepository
import io.ktor.client.plugins.ClientRequestException
import io.ktor.client.plugins.ServerResponseException
import java.io.IOException
import javax.inject.Inject

/**
 * DATA LAYER — REPOSITORY IMPLEMENTATION
 *
 * This is the concrete class that actually implements the domain's
 * MealRepository interface. It:
 *   1. Calls MealApiService (Ktor) to get raw DTOs.
 *   2. Maps those DTOs into clean domain models (toDomainSummary / toDomainDetail).
 *   3. Wraps everything in Kotlin's built-in Result so callers get a simple
 *      success/failure without needing to know Ktor's exception types.
 *
 * Hilt binds this class to the MealRepository interface in data/di/RepositoryModule.kt,
 * so anything that injects MealRepository (i.e. the use cases) gets THIS instance,
 * without ever importing Ktor themselves.
 */
class MealRepositoryImpl @Inject constructor(
    private val api: MealApiService
) : MealRepository {

    override suspend fun getMeals(query: String): Result<List<MealSummary>> = safeCall {
        val response = api.searchMeals(query)
        (response.meals ?: emptyList()).map { it.toDomainSummary() }
    }

    override suspend fun getMealDetail(id: String): Result<MealDetail> = safeCall {
        val response = api.getMealById(id)
        val dto = response.meals?.firstOrNull()
            ?: throw NoSuchElementException("No meal found for id=$id")
        dto.toDomainDetail()
    }

    /**
     * Runs [block], catching Ktor's exception types plus general IO/network
     * failures, and turns everything into Result.success / Result.failure.
     * This is the ONLY place in the app that knows about Ktor's exception types
     * (ClientRequestException = 4xx, ServerResponseException = 5xx).
     */
    private inline fun <T> safeCall(block: () -> T): Result<T> {
        return try {
            Result.success(block())
        } catch (e: ClientRequestException) {
            Result.failure(Exception("Request error: ${e.response.status}"))
        } catch (e: ServerResponseException) {
            Result.failure(Exception("Server error: ${e.response.status}"))
        } catch (e: IOException) {
            Result.failure(Exception("Network error: ${e.message}"))
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    /** Maps a MealDto -> the lightweight domain model used by the list screen. */
    private fun MealDto.toDomainSummary() = MealSummary(
        id = id,
        name = name,
        thumbnailUrl = thumbnailUrl
    )

    /** Maps a MealDto -> the full domain model used by the detail screen. */
    private fun MealDto.toDomainDetail(): MealDetail {
        val ingredients = ingredientMeasurePairs()
            .filter { (ingredientName, _) -> !ingredientName.isNullOrBlank() }
            .map { (ingredientName, measure) ->
                Ingredient(name = ingredientName!!.trim(), measure = measure?.trim().orEmpty())
            }

        return MealDetail(
            id = id,
            name = name,
            thumbnailUrl = thumbnailUrl,
            category = category.orEmpty(),
            area = area.orEmpty(),
            instructions = instructions.orEmpty(),
            ingredients = ingredients
        )
    }
}
