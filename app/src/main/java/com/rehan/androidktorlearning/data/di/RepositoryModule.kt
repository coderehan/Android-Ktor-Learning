package com.rehan.androidktorlearning.data.di

import com.rehan.androidktorlearning.data.repository.MealRepositoryImpl
import com.rehan.androidktorlearning.domain.repository.MealRepository
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

/**
 * DATA LAYER — DI (interface binding)
 *
 * This is THE line that makes the architecture "clean": it tells Hilt that
 * whenever something asks for a `MealRepository` (the domain interface), give it
 * a `MealRepositoryImpl` (the Ktor-backed data-layer class).
 *
 * The use cases (domain layer) and ViewModels (app layer) only ever inject
 * `MealRepository` — the interface. They never see `MealRepositoryImpl`, and
 * therefore never see Ktor, HTTP, or JSON directly. That's the whole benefit of
 * having a domain layer: swap the implementation (different API, different
 * networking library, a fake for tests) and nothing above this file changes.
 *
 * @Binds (instead of @Provides) is used because we're just telling Hilt "use
 * this concrete class for this interface" — no actual object construction logic
 * needed here.
 */
@Module
@InstallIn(SingletonComponent::class)
abstract class RepositoryModule {

    @Binds
    @Singleton
    abstract fun bindMealRepository(
        impl: MealRepositoryImpl
    ): MealRepository
}
