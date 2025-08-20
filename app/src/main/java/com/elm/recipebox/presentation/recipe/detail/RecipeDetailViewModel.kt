package com.elm.recipebox.presentation.recipe.detail

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.elm.recipebox.domain.model.Recipe
import com.elm.recipebox.domain.usecase.GetRecipeByIdUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class RecipeDetailViewModel @Inject constructor(
    private val getRecipeByIdUseCase: GetRecipeByIdUseCase
) : ViewModel() {
    
    private val _recipe = MutableStateFlow<Recipe?>(null)
    val recipe: StateFlow<Recipe?> = _recipe.asStateFlow()
    
    private val _uiState = MutableStateFlow<RecipeDetailUiState>(RecipeDetailUiState.Loading)
    val uiState: StateFlow<RecipeDetailUiState> = _uiState.asStateFlow()
    
    fun loadRecipe(recipeId: Long) {
        viewModelScope.launch {
            _uiState.value = RecipeDetailUiState.Loading
            try {
                val recipe = getRecipeByIdUseCase(recipeId)
                _recipe.value = recipe
                _uiState.value = RecipeDetailUiState.Success
            } catch (e: Exception) {
                _uiState.value = RecipeDetailUiState.Error(e.message ?: "Unknown error")
            }
        }
    }
}

sealed class RecipeDetailUiState {
    object Loading : RecipeDetailUiState()
    object Success : RecipeDetailUiState()
    data class Error(val message: String) : RecipeDetailUiState()
}