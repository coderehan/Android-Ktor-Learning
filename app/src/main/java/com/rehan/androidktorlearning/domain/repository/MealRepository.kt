package com.rehan.androidktorlearning.domain.repository

import com.rehan.androidktorlearning.domain.model.MealDetail
import com.rehan.androidktorlearning.domain.model.MealSummary

/**
 * DOMAIN LAYER — REPOSITORY CONTRACT
 *
 * This is only an interface. It says WHAT the app can do (get a list of meals,
 * get one meal's detail) but says nothing about HOW — no Ktor, no URLs, no JSON.
 *
 * The "how" lives in data/repository/MealRepositoryImpl.kt, which implements
 * this interface using a Ktor HttpClient.
 *
 * Why bother with an interface for a small learning app?
 * - The ViewModel (app layer) will depend on THIS interface, not on the Ktor-backed
 *   implementation. That's the "clean" boundary: presentation layer never sees Ktor.
 * - It also means you could write a FakeMealRepository for tests/previews without
 *   touching the network at all.
 */
interface MealRepository {

    /** Fetches a list of meal summaries (used by the list screen). */
    suspend fun getMeals(query: String): Result<List<MealSummary>>

    /** Fetches full detail for a single meal by its id (used by the detail screen). */
    suspend fun getMealDetail(id: String): Result<MealDetail>
}
