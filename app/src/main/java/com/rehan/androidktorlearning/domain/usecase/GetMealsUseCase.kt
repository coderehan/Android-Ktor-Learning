package com.rehan.androidktorlearning.domain.usecase

import com.rehan.androidktorlearning.domain.model.MealSummary
import com.rehan.androidktorlearning.domain.repository.MealRepository
import javax.inject.Inject

/**
 * DOMAIN LAYER — USE CASE
 *
 * This is the "lightweight" part of lightweight-clean-architecture: a use case
 * is just a class with one job, invoked like a function via `operator fun invoke`.
 * Here it does nothing fancy — just forwards to the repository — but in a bigger
 * app this is where you'd put business rules, e.g. "only show meals that have a
 * thumbnail" or "combine results from two repositories."
 *
 * The ViewModel calls `getMealsUseCase("chicken")` instead of talking to the
 * repository directly. That keeps the ViewModel from depending on the repository
 * interface for every operation — each screen only depends on the use cases it needs.
 */
class GetMealsUseCase @Inject constructor(
    private val repository: MealRepository
) {
    suspend operator fun invoke(query: String): Result<List<MealSummary>> {
        return repository.getMeals(query)
    }
}
