package com.rehan.androidktorlearning.domain.usecase

import com.rehan.androidktorlearning.domain.model.MealDetail
import com.rehan.androidktorlearning.domain.repository.MealRepository
import javax.inject.Inject

/**
 * DOMAIN LAYER — USE CASE
 *
 * Same idea as GetMealsUseCase, but for the detail screen: takes a meal id,
 * returns the full MealDetail. Kept separate from GetMealsUseCase so each
 * screen's ViewModel only injects the one use case it actually needs.
 */
class GetMealDetailUseCase @Inject constructor(
    private val repository: MealRepository
) {
    suspend operator fun invoke(id: String): Result<MealDetail> {
        return repository.getMealDetail(id)
    }
}
