package com.rehan.androidktorlearning.app.detail

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import coil.compose.AsyncImage
import com.rehan.androidktorlearning.domain.model.Ingredient
import com.rehan.androidktorlearning.domain.model.MealDetail

/**
 * APP LAYER — DETAIL SCREEN (UI)
 *
 * Shows the recipe image, category/area, full instructions, and the ingredient
 * list built from TheMealDB's 20 numbered fields (mapped in MealRepositoryImpl).
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MealDetailScreen(
    onBack: () -> Unit,
    viewModel: MealDetailViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Recipe Detail") },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        }
    ) { padding ->
        Box(modifier = Modifier.padding(padding).fillMaxSize()) {
            when (val state = uiState) {
                is MealDetailUiState.Loading ->
                    CircularProgressIndicator(Modifier.align(Alignment.Center))

                is MealDetailUiState.Error -> Text(
                    "Couldn't load recipe: ${state.message}",
                    modifier = Modifier.align(Alignment.Center).padding(24.dp)
                )

                is MealDetailUiState.Success -> DetailContent(state.meal)
            }
        }
    }
}

@Composable
private fun DetailContent(meal: MealDetail) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(16.dp)
    ) {
        AsyncImage(
            model = meal.thumbnailUrl,
            contentDescription = meal.name,
            modifier = Modifier
                .fillMaxWidth()
                .height(220.dp)
                .clip(RoundedCornerShape(16.dp))
        )
        Spacer(Modifier.height(16.dp))
        Text(meal.name, style = MaterialTheme.typography.headlineSmall)
        Spacer(Modifier.height(4.dp))
        Text(
            "${meal.category} - ${meal.area}",
            style = MaterialTheme.typography.bodyMedium
        )

        Spacer(Modifier.height(16.dp))
        Text("Ingredients", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
        Spacer(Modifier.height(8.dp))
        meal.ingredients.forEach { IngredientRow(it) }

        Spacer(Modifier.height(16.dp))
        Text("Instructions", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
        Spacer(Modifier.height(8.dp))
        Text(meal.instructions, style = MaterialTheme.typography.bodyMedium)
        Spacer(Modifier.height(24.dp))
    }
}

@Composable
private fun IngredientRow(ingredient: Ingredient) {
    Row(modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp)) {
        Text(ingredient.name, modifier = Modifier.weight(1f))
        Text(ingredient.measure, modifier = Modifier.weight(1f))
    }
}
