package com.elm.recipebox.presentation.search

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.elm.recipebox.domain.model.Recipe
import com.elm.recipebox.domain.repository.RecipeRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SearchViewModel @Inject constructor(
    private val recipeRepository: RecipeRepository
) : ViewModel() {
    
    private val _searchQuery = MutableStateFlow("")
    val searchQuery: StateFlow<String> = _searchQuery.asStateFlow()
    
    private val _selectedDifficulty = MutableStateFlow<String?>(null)
    val selectedDifficulty: StateFlow<String?> = _selectedDifficulty.asStateFlow()
    
    private val _selectedDishTypes = MutableStateFlow<Set<String>>(emptySet())
    val selectedDishTypes: StateFlow<Set<String>> = _selectedDishTypes.asStateFlow()
    
    private val _maxCookTime = MutableStateFlow(60) // in minutes
    val maxCookTime: StateFlow<Int> = _maxCookTime.asStateFlow()
    
    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()
    
    val filteredRecipes: StateFlow<List<Recipe>> = combine(
        recipeRepository.getAllRecipes(),
        searchQuery,
        selectedDifficulty,
        selectedDishTypes,
        maxCookTime
    ) { recipes, query, difficulty, dishTypes, cookTime ->
        var filtered = recipes
        
        if (query.isNotBlank()) {
            filtered = filtered.filter { recipe ->
                recipe.title.contains(query, ignoreCase = true) ||
                recipe.description.contains(query, ignoreCase = true) ||
                recipe.ingredients.any { it.name.contains(query, ignoreCase = true) }
            }
        }
        
        if (difficulty != null) {
            filtered = filtered.filter { recipe ->
                recipe.difficulty?.equals(difficulty, ignoreCase = true) == true
            }
        }
        
        if (dishTypes.isNotEmpty()) {
            filtered = filtered.filter { recipe ->
                recipe.dishTypes.any { it in dishTypes }
            }
        }
        
        filtered = filtered.filter { recipe ->
            val totalMinutes = (recipe.cookTimeHours.toIntOrNull() ?: 0) * 60 + 
                             (recipe.cookTimeMinutes.toIntOrNull() ?: 0)
            totalMinutes <= cookTime
        }
        
        filtered
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = emptyList()
    )
    
    fun updateSearchQuery(query: String) {
        _searchQuery.value = query
    }
    
    fun updateDifficulty(difficulty: String?) {
        _selectedDifficulty.value = difficulty
    }
    
    fun updateDishTypes(dishTypes: Set<String>) {
        _selectedDishTypes.value = dishTypes
    }
    
    fun updateMaxCookTime(minutes: Int) {
        _maxCookTime.value = minutes
    }
    
    fun clearFilters() {
        _selectedDifficulty.value = null
        _selectedDishTypes.value = emptySet()
        _maxCookTime.value = 60
    }
    
    fun searchRecipes(query: String) {
        viewModelScope.launch {
            _isLoading.value = true
            updateSearchQuery(query)
            _isLoading.value = false
        }
    }
}