package com.rehan.androidktorlearning.app.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.rehan.androidktorlearning.app.detail.MealDetailScreen
import com.rehan.androidktorlearning.app.list.MealListScreen

/**
 * APP LAYER — NAVIGATION
 *
 * Two routes only: the main/list screen and the detail screen, which takes a
 * "mealId" string argument. MealDetailViewModel reads that argument back out via SavedStateHandle.
 */
private const val ROUTE_LIST = "list"
private const val ROUTE_DETAIL = "detail/{mealId}"

@Composable
fun AppNavGraph() {
    val navController = rememberNavController()

    NavHost(navController = navController, startDestination = ROUTE_LIST) {
        composable(ROUTE_LIST) {
            MealListScreen(
                onMealClick = { id -> navController.navigate("detail/$id") }
            )
        }
        composable(
            route = ROUTE_DETAIL,
            arguments = listOf(navArgument("mealId") { type = NavType.StringType })
        ) {
            MealDetailScreen(onBack = { navController.popBackStack() })
        }
    }
}
