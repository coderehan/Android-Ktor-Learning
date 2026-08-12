package com.rehan.androidktorlearning

import android.app.Application
import dagger.hilt.android.HiltAndroidApp

/**
 * APP LAYER — Application class
 * @HiltAndroidApp triggers Hilt's code generation and creates the top-level
 * dependency container that NetworkModule and RepositoryModule plug into.
 */
@HiltAndroidApp
class KtorRecipeApp : Application()
