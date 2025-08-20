package com.elm.recipebox.presentation.recipe.add

import android.net.Uri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.elm.recipebox.domain.usecase.AddRecipeUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

data class RecipeFormData(
    val title: String = "",
    val description: String = "",
    val hashtags: String = "",
    val servings: Int = 1,
    val cookTimeHours: String = "00",
    val cookTimeMinutes: String = "00",
    val difficulty: String? = null,
    val dishTypes: Set<String> = emptySet(),
    val dietTypes: Set<String> = emptySet(),
    val ingredients: List<String> = listOf(""),
    val steps: List<String> = listOf(""),
    val imageUri: Uri? = null
)

data class AddRecipeUiState(
    val isLoading: Boolean = false,
    val error: String? = null,
    val isSuccess: Boolean = false,
    val createdRecipeId: Long? = null
)

@HiltViewModel
class AddRecipeViewModel @Inject constructor(
    private val addRecipeUseCase: AddRecipeUseCase
) : ViewModel() {
    
    private val _recipeData = MutableStateFlow(RecipeFormData())
    val recipeData: StateFlow<RecipeFormData> = _recipeData.asStateFlow()
    
    private val _currentStep = MutableStateFlow(0)
    val currentStep: StateFlow<Int> = _currentStep.asStateFlow()
    
    private val _uiState = MutableStateFlow(AddRecipeUiState())
    val uiState: StateFlow<AddRecipeUiState> = _uiState.asStateFlow()
    
    fun updateTitle(title: String) {
        _recipeData.value = _recipeData.value.copy(title = title)
    }
    
    fun updateDescription(description: String) {
        _recipeData.value = _recipeData.value.copy(description = description)
    }
    
    fun updateHashtags(hashtags: String) {
        _recipeData.value = _recipeData.value.copy(hashtags = hashtags)
    }
    
    fun updateServings(servings: Int) {
        _recipeData.value = _recipeData.value.copy(servings = servings)
    }
    
    fun updateCookTime(hours: String, minutes: String) {
        _recipeData.value = _recipeData.value.copy(
            cookTimeHours = hours,
            cookTimeMinutes = minutes
        )
    }
    
    fun updateDifficulty(difficulty: String?) {
        _recipeData.value = _recipeData.value.copy(difficulty = difficulty)
    }
    
    fun updateDishTypes(dishTypes: Set<String>) {
        _recipeData.value = _recipeData.value.copy(dishTypes = dishTypes)
    }
    
    fun updateDietTypes(dietTypes: Set<String>) {
        _recipeData.value = _recipeData.value.copy(dietTypes = dietTypes)
    }
    
    fun updateIngredients(ingredients: List<String>) {
        _recipeData.value = _recipeData.value.copy(ingredients = ingredients)
    }
    
    fun updateSteps(steps: List<String>) {
        _recipeData.value = _recipeData.value.copy(steps = steps)
    }
    
    fun updateImageUri(uri: Uri?) {
        _recipeData.value = _recipeData.value.copy(imageUri = uri)
    }
    
    fun nextStep() {
        if (_currentStep.value < 3) {
            _currentStep.value = _currentStep.value + 1
        }
    }
    
    fun previousStep() {
        if (_currentStep.value > 0) {
            _currentStep.value = _currentStep.value - 1
        }
    }
    
    fun goToStep(step: Int) {
        if (step in 0..3) {
            _currentStep.value = step
        }
    }
    
    fun canProceedToNextStep(): Boolean {
        val data = _recipeData.value
        return when (_currentStep.value) {
            0 -> data.title.isNotBlank() && data.description.isNotBlank()
            1 -> data.ingredients.any { it.isNotBlank() }
            2 -> data.steps.any { it.isNotBlank() }
            3 -> true
            else -> false
        }
    }
    
    fun validateRecipe(): Boolean {
        val data = _recipeData.value
        return data.title.isNotBlank() &&
                data.description.isNotBlank() &&
                data.ingredients.any { it.isNotBlank() } &&
                data.steps.any { it.isNotBlank() }
    }
    
    fun saveRecipe() {
        if (!validateRecipe()) {
            _uiState.value = _uiState.value.copy(
                error = "Please fill in all required fields"
            )
            return
        }
        
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true, error = null)
            
            val data = _recipeData.value
            val result = addRecipeUseCase(
                title = data.title,
                description = data.description,
                hashtags = data.hashtags,
                servings = data.servings,
                cookTimeHours = data.cookTimeHours,
                cookTimeMinutes = data.cookTimeMinutes,
                difficulty = data.difficulty,
                dishTypes = data.dishTypes,
                dietTypes = data.dietTypes,
                imageUri = data.imageUri?.toString(),
                ingredientNames = data.ingredients,
                stepDescriptions = data.steps
            )
            
            result.fold(
                onSuccess = { recipeId ->
                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        isSuccess = true,
                        createdRecipeId = recipeId
                    )
                },
                onFailure = { error ->
                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        error = error.message ?: "Unknown error occurred"
                    )
                }
            )
        }
    }
    
    fun clearError() {
        _uiState.value = _uiState.value.copy(error = null)
    }
    
    fun resetForm() {
        _recipeData.value = RecipeFormData()
        _currentStep.value = 0
        _uiState.value = AddRecipeUiState()
    }
}