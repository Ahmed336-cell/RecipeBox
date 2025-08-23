package com.elm.recipebox.presentation.search

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.elm.recipebox.domain.model.Recipe
import com.elm.recipebox.domain.usecase.SearchRecipesUseCase
import com.elm.recipebox.domain.usecase.SortOption
import com.elm.recipebox.domain.usecase.SearchFilters
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import kotlinx.coroutines.ExperimentalCoroutinesApi
import javax.inject.Inject



data class RecipeStats(
    val totalRecipes: Int = 0,
    val averageCookTime: Int = 0,
    val difficultyDistribution: Map<String, Int> = emptyMap(),
    val dishTypeDistribution: Map<String, Int> = emptyMap()
)

@OptIn(ExperimentalCoroutinesApi::class)
@HiltViewModel
class SearchViewModel @Inject constructor(
    private val searchRecipesUseCase: SearchRecipesUseCase
) : ViewModel() {
    
    private val _searchQuery = MutableStateFlow("")
    val searchQuery: StateFlow<String> = _searchQuery.asStateFlow()
    
    private val _selectedDifficulty = MutableStateFlow<String?>(null)
    val selectedDifficulty: StateFlow<String?> = _selectedDifficulty.asStateFlow()
    
    private val _selectedDishTypes = MutableStateFlow<Set<String>>(emptySet())
    val selectedDishTypes: StateFlow<Set<String>> = _selectedDishTypes.asStateFlow()
    
    private val _maxCookTime = MutableStateFlow(60)
    val maxCookTime: StateFlow<Int> = _maxCookTime.asStateFlow()
    
    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()
    
    private val _sortBy = MutableStateFlow(SortOption.TITLE)
    val sortBy: StateFlow<SortOption> = _sortBy.asStateFlow()
    
    val allRecipes: StateFlow<List<Recipe>> = searchRecipesUseCase.getAllRecipes()
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )
    
    val recipeStats: StateFlow<RecipeStats> = searchRecipesUseCase.getAllRecipes()
        .map { recipes ->
            RecipeStats(
                totalRecipes = recipes.size,
                averageCookTime = recipes.mapNotNull { recipe ->
                    val hours = recipe.cookTimeHours.toIntOrNull() ?: 0
                    val minutes = recipe.cookTimeMinutes.toIntOrNull() ?: 0
                    hours * 60 + minutes
                }.takeIf { it.isNotEmpty() }?.average()?.toInt() ?: 0,
                difficultyDistribution = recipes.groupBy { it.difficulty ?: "Unknown" }
                    .mapValues { it.value.size },
                dishTypeDistribution = recipes.flatMap { it.dishTypes }
                    .groupingBy { it }
                    .eachCount()
            )
        }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = RecipeStats()
        )
    
    // Filtered recipes using the use case
    val filteredRecipes: StateFlow<List<Recipe>> = combine(
        _searchQuery,
        _selectedDifficulty,
        _selectedDishTypes,
        _maxCookTime,
        _sortBy
    ) { query, difficulty, dishTypes, maxCookTime, sortBy ->
        val filters = SearchFilters(
            query = query,
            difficulty = difficulty,
            dishTypes = dishTypes,
            maxCookTime = maxCookTime,
            sortBy = sortBy
        )
        searchRecipesUseCase(filters)
    }.flatMapLatest { it }
        .stateIn(
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
    
    fun updateSortOption(sortOption: SortOption) {
        _sortBy.value = sortOption
    }
    
    fun clearFilters() {
        _selectedDifficulty.value = null
        _selectedDishTypes.value = emptySet()
        _maxCookTime.value = 60
        _sortBy.value = SortOption.TITLE
    }
    
    fun searchRecipes(query: String) {
        viewModelScope.launch {
            _isLoading.value = true
            updateSearchQuery(query)
            _isLoading.value = false
        }
    }
}