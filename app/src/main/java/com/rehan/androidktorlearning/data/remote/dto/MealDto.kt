package com.rehan.androidktorlearning.data.remote.dto

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

/**
 * DATA LAYER — DTOs (Data Transfer Objects)
 *
 * These classes mirror TheMealDB's JSON exactly, field-for-field, including its
 * quirky naming (strMeal, strMealThumb, strIngredient1..20, strMeasure1..20).
 * This is deliberately ugly/API-shaped — that ugliness is CONTAINED here and
 * never leaks past the data layer. MealRepositoryImpl converts these into the
 * clean domain models (Meal.kt) that the rest of the app uses.
 *
 * Every top-level TheMealDB response is wrapped in {"meals": [...]}, so both
 * endpoints share this envelope.
 *
 * Endpoints used:
 *   Search by name : https://www.themealdb.com/api/json/v1/1/search.php?s={query}
 *   Lookup by id   : https://www.themealdb.com/api/json/v1/1/lookup.php?i={id}
 */
@Serializable
data class MealListResponseDto(
    val meals: List<MealDto>? = null   // null when the API finds no matches
)

@Serializable
data class MealDto(
    @SerialName("idMeal") val id: String,
    @SerialName("strMeal") val name: String,
    @SerialName("strMealThumb") val thumbnailUrl: String,
    @SerialName("strCategory") val category: String? = null,
    @SerialName("strArea") val area: String? = null,
    @SerialName("strInstructions") val instructions: String? = null,

    // TheMealDB does NOT return ingredients as a list — it returns 20 separate
    // numbered fields instead (strIngredient1, strIngredient2, ... strIngredient20,
    // strMeasure1, strMeasure2, ... strMeasure20), most of which are blank.
    // Mapping these 40 fields into a clean List<Ingredient> happens in the
    // repository (see MealRepositoryImpl.toDomainDetail()).
    @SerialName("strIngredient1") val ingredient1: String? = null,
    @SerialName("strIngredient2") val ingredient2: String? = null,
    @SerialName("strIngredient3") val ingredient3: String? = null,
    @SerialName("strIngredient4") val ingredient4: String? = null,
    @SerialName("strIngredient5") val ingredient5: String? = null,
    @SerialName("strIngredient6") val ingredient6: String? = null,
    @SerialName("strIngredient7") val ingredient7: String? = null,
    @SerialName("strIngredient8") val ingredient8: String? = null,
    @SerialName("strIngredient9") val ingredient9: String? = null,
    @SerialName("strIngredient10") val ingredient10: String? = null,
    @SerialName("strIngredient11") val ingredient11: String? = null,
    @SerialName("strIngredient12") val ingredient12: String? = null,
    @SerialName("strIngredient13") val ingredient13: String? = null,
    @SerialName("strIngredient14") val ingredient14: String? = null,
    @SerialName("strIngredient15") val ingredient15: String? = null,
    @SerialName("strIngredient16") val ingredient16: String? = null,
    @SerialName("strIngredient17") val ingredient17: String? = null,
    @SerialName("strIngredient18") val ingredient18: String? = null,
    @SerialName("strIngredient19") val ingredient19: String? = null,
    @SerialName("strIngredient20") val ingredient20: String? = null,

    @SerialName("strMeasure1") val measure1: String? = null,
    @SerialName("strMeasure2") val measure2: String? = null,
    @SerialName("strMeasure3") val measure3: String? = null,
    @SerialName("strMeasure4") val measure4: String? = null,
    @SerialName("strMeasure5") val measure5: String? = null,
    @SerialName("strMeasure6") val measure6: String? = null,
    @SerialName("strMeasure7") val measure7: String? = null,
    @SerialName("strMeasure8") val measure8: String? = null,
    @SerialName("strMeasure9") val measure9: String? = null,
    @SerialName("strMeasure10") val measure10: String? = null,
    @SerialName("strMeasure11") val measure11: String? = null,
    @SerialName("strMeasure12") val measure12: String? = null,
    @SerialName("strMeasure13") val measure13: String? = null,
    @SerialName("strMeasure14") val measure14: String? = null,
    @SerialName("strMeasure15") val measure15: String? = null,
    @SerialName("strMeasure16") val measure16: String? = null,
    @SerialName("strMeasure17") val measure17: String? = null,
    @SerialName("strMeasure18") val measure18: String? = null,
    @SerialName("strMeasure19") val measure19: String? = null,
    @SerialName("strMeasure20") val measure20: String? = null
) {
    /** Zips ingredient1..20 with measure1..20 into pairs, for easy looping. */
    fun ingredientMeasurePairs(): List<Pair<String?, String?>> = listOf(
        ingredient1 to measure1, ingredient2 to measure2, ingredient3 to measure3,
        ingredient4 to measure4, ingredient5 to measure5, ingredient6 to measure6,
        ingredient7 to measure7, ingredient8 to measure8, ingredient9 to measure9,
        ingredient10 to measure10, ingredient11 to measure11, ingredient12 to measure12,
        ingredient13 to measure13, ingredient14 to measure14, ingredient15 to measure15,
        ingredient16 to measure16, ingredient17 to measure17, ingredient18 to measure18,
        ingredient19 to measure19, ingredient20 to measure20
    )
}
