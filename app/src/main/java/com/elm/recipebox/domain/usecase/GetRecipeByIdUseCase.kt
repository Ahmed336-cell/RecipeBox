package com.elm.recipebox.domain.usecase

import com.elm.recipebox.domain.model.Recipe
import com.elm.recipebox.domain.repository.RecipeRepository
import javax.inject.Inject

class GetRecipeByIdUseCase @Inject constructor(
    private val repository: RecipeRepository
) {
    suspend operator fun invoke(recipeId: Long): Recipe {
        return repository.getRecipeById(recipeId) 
            ?: throw IllegalArgumentException("Recipe with ID $recipeId not found")
    }
}