package com.rehan.androidktorlearning.app.list

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.rehan.androidktorlearning.domain.model.MealSummary
import com.rehan.androidktorlearning.domain.usecase.GetMealsUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * APP LAYER — MAIN/LIST SCREEN VIEWMODEL
 *
 * Notice the constructor only injects GetMealsUseCase — a domain-layer class.
 * There is no import of Ktor, HttpClient, or MealApiService anywhere in this
 * file or the whole app/ package. That's the architectural boundary at work:
 * this ViewModel genuinely does not know HOW meals are fetched, only that it
 * can call `getMealsUseCase(query)` and get a Result back.
 */
sealed class MealListUiState {
    data object Loading : MealListUiState()
    data class Success(val meals: List<MealSummary>) : MealListUiState()
    data class Error(val message: String) : MealListUiState()
}

@HiltViewModel
class MealListViewModel @Inject constructor(
    private val getMealsUseCase: GetMealsUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow<MealListUiState>(MealListUiState.Loading)
    val uiState: StateFlow<MealListUiState> = _uiState.asStateFlow()

    init {
        // Load an initial set of recipes on first launch.
        search("chicken")
    }

    /** Called from the UI's search bar (see MealListScreen.kt). */
    fun search(query: String) {
        if (query.isBlank()) return

        viewModelScope.launch {
            _uiState.value = MealListUiState.Loading
            getMealsUseCase(query)
                .onSuccess { meals -> _uiState.value = MealListUiState.Success(meals) }
                .onFailure { error ->
                    _uiState.value = MealListUiState.Error(error.message ?: "Unknown error")
                }
        }
    }
}
