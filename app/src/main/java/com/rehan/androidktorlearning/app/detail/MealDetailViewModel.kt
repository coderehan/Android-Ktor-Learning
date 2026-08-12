package com.rehan.androidktorlearning.app.detail

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.rehan.androidktorlearning.domain.model.MealDetail
import com.rehan.androidktorlearning.domain.usecase.GetMealDetailUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * APP LAYER — DETAIL SCREEN VIEWMODEL
 *
 * Same shape as MealListViewModel: injects only GetMealDetailUseCase (domain),
 * never touches Ktor directly. The meal id is read from SavedStateHandle, which
 * Navigation-Compose populates from the route argument (see navigation/NavGraph.kt).
 */
sealed class MealDetailUiState {
    data object Loading : MealDetailUiState()
    data class Success(val meal: MealDetail) : MealDetailUiState()
    data class Error(val message: String) : MealDetailUiState()
}

@HiltViewModel
class MealDetailViewModel @Inject constructor(
    private val getMealDetailUseCase: GetMealDetailUseCase,
    savedStateHandle: SavedStateHandle
) : ViewModel() {

    private val mealId: String = checkNotNull(savedStateHandle["mealId"])

    private val _uiState = MutableStateFlow<MealDetailUiState>(MealDetailUiState.Loading)
    val uiState: StateFlow<MealDetailUiState> = _uiState.asStateFlow()

    init {
        viewModelScope.launch {
            getMealDetailUseCase(mealId)
                .onSuccess { meal -> _uiState.value = MealDetailUiState.Success(meal) }
                .onFailure { error ->
                    _uiState.value = MealDetailUiState.Error(error.message ?: "Unknown error")
                }
        }
    }
}
