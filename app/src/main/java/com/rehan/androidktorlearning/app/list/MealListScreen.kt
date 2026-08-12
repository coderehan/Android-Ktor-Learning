package com.rehan.androidktorlearning.app.list

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import coil.compose.AsyncImage
import com.rehan.androidktorlearning.domain.model.MealSummary

/**
 * APP LAYER — MAIN/LIST SCREEN (UI)
 *
 * Pure Compose UI. Reads MealListUiState from the ViewModel and renders it.
 * Has a search bar so you can type any recipe keyword (e.g. "pasta", "beef",
 * "soup") and re-trigger the Ktor call through the ViewModel -> use case ->
 * repository chain — good for seeing multiple real requests fire while testing.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MealListScreen(
    onMealClick: (String) -> Unit,
    viewModel: MealListViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    var query by remember { mutableStateOf("chicken") }

    Scaffold(topBar = { TopAppBar(title = { Text("Recipes") }) }) { padding ->
        Column(modifier = Modifier.padding(padding).fillMaxSize()) {

            // Search bar
            Row(
                modifier = Modifier.fillMaxWidth().padding(12.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                OutlinedTextField(
                    value = query,
                    onValueChange = { query = it },
                    label = { Text("Search recipes") },
                    singleLine = true,
                    modifier = Modifier.weight(1f)
                )
                Spacer(Modifier.width(8.dp))
                Button(onClick = { viewModel.search(query) }) { Text("Go") }
            }

            Box(modifier = Modifier.fillMaxSize()) {
                when (val state = uiState) {
                    is MealListUiState.Loading ->
                        CircularProgressIndicator(Modifier.align(Alignment.Center))

                    is MealListUiState.Error -> Column(
                        modifier = Modifier.align(Alignment.Center).padding(24.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text("Couldn't load recipes: ${state.message}")
                        Spacer(Modifier.height(8.dp))
                        Button(onClick = { viewModel.search(query) }) { Text("Retry") }
                    }

                    is MealListUiState.Success -> {
                        if (state.meals.isEmpty()) {
                            Text(
                                "No recipes found for \"$query\"",
                                modifier = Modifier.align(Alignment.Center).padding(24.dp)
                            )
                        } else {
                            LazyColumn {
                                items(state.meals, key = { it.id }) { meal ->
                                    MealRow(meal, onClick = { onMealClick(meal.id) })
                                    Divider()
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun MealRow(meal: MealSummary, onClick: () -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .padding(12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        AsyncImage(
            model = meal.thumbnailUrl,
            contentDescription = meal.name,
            modifier = Modifier.size(64.dp).clip(RoundedCornerShape(8.dp))
        )
        Spacer(Modifier.width(12.dp))
        Text(meal.name, style = MaterialTheme.typography.titleMedium)
    }
}
