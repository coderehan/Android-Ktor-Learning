package com.rehan.androidktorlearning.domain.model

/**
 * DOMAIN LAYER — MODELS
 *
 * These are the models the rest of the app (ViewModels, UI) actually works with.
 * Notice this file imports NOTHING from Ktor, kotlinx.serialization, or Android.
 * That's the whole point of a domain layer: it doesn't know or care that data
 * comes from TheMealDB API over HTTP using Ktor. If you swapped Ktor for Retrofit,
 * or swapped the API for a different recipe API entirely, this file would not change.
 *
 * The "translation" from the API's raw JSON shape into these clean models happens
 * in the data layer (see data/repository/MealRepositoryImpl.kt).
 */

/** A lightweight summary used in the list screen. */
data class MealSummary(
    val id: String,
    val name: String,
    val thumbnailUrl: String
)

/** The full detail used in the detail screen. */
data class MealDetail(
    val id: String,
    val name: String,
    val thumbnailUrl: String,
    val category: String,
    val area: String,
    val instructions: String,
    val ingredients: List<Ingredient>
)

data class Ingredient(
    val name: String,
    val measure: String
)
