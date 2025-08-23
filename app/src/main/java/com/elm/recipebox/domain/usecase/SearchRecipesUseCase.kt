package com.elm.recipebox.domain.usecase

import com.elm.recipebox.domain.model.Recipe
import com.elm.recipebox.domain.repository.RecipeRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

enum class SortOption(val displayName: String) {
    TITLE("Title"),
    DIFFICULTY("Difficulty"),
    COOK_TIME("Cook Time"),
    RECENT("Recently Added"),
    SERVINGS("Servings")
}

data class SearchFilters(
    val query: String = "",
    val difficulty: String? = null,
    val dishTypes: Set<String> = emptySet(),
    val maxCookTime: Int = 60, // in minutes
    val sortBy: SortOption = SortOption.TITLE
)

class SearchRecipesUseCase @Inject constructor(
    private val repository: RecipeRepository
) {
    operator fun invoke(filters: SearchFilters): Flow<List<Recipe>> {
        return repository.getAllRecipes().map { recipes ->
            var filtered = recipes
            
            // Apply text search
            if (filters.query.isNotBlank()) {
                filtered = filtered.filter { recipe ->
                    recipe.title.contains(filters.query, ignoreCase = true) ||
                    recipe.description.contains(filters.query, ignoreCase = true) ||
                    recipe.ingredients.any { it.name.contains(filters.query, ignoreCase = true) }
                }
            }
            
            // Apply difficulty filter
            if (filters.difficulty != null) {
                filtered = filtered.filter { recipe ->
                    recipe.difficulty?.equals(filters.difficulty, ignoreCase = true) == true
                }
            }
            
            // Apply dish type filter
            if (filters.dishTypes.isNotEmpty()) {
                filtered = filtered.filter { recipe ->
                    recipe.dishTypes.any { it in filters.dishTypes }
                }
            }
            
            // Apply cook time filter
            filtered = filtered.filter { recipe ->
                val totalMinutes = (recipe.cookTimeHours.toIntOrNull() ?: 0) * 60 + 
                                 (recipe.cookTimeMinutes.toIntOrNull() ?: 0)
                totalMinutes <= filters.maxCookTime
            }
            
            // Apply sorting
            when (filters.sortBy) {
                SortOption.TITLE -> filtered.sortedBy { it.title.lowercase() }
                SortOption.DIFFICULTY -> filtered.sortedBy { it.difficulty ?: "Easy" }
                SortOption.COOK_TIME -> filtered.sortedBy { 
                    (it.cookTimeHours.toIntOrNull() ?: 0) * 60 + (it.cookTimeMinutes.toIntOrNull() ?: 0)
                }
                SortOption.RECENT -> filtered.sortedByDescending { it.createdAt }
                SortOption.SERVINGS -> filtered.sortedBy { it.servings }
            }
        }
    }
    
    // Simple search with just query
    fun searchByQuery(query: String): Flow<List<Recipe>> {
        return repository.searchRecipes(query)
    }
    
    // Get all recipes without filters
    fun getAllRecipes(): Flow<List<Recipe>> {
        return repository.getAllRecipes()
    }
}