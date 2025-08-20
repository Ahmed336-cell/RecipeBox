package com.elm.recipebox.data.repository

import com.elm.recipebox.data.local.dao.RecipeDao
import com.elm.recipebox.data.mapper.*
import com.elm.recipebox.domain.model.Recipe
import com.elm.recipebox.domain.repository.RecipeRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class RecipeRepositoryImpl @Inject constructor(
    private val recipeDao: RecipeDao
) : RecipeRepository {
    
    override fun getAllRecipes(): Flow<List<Recipe>> {
        return recipeDao.getAllRecipesWithDetails().map { recipeList ->
            recipeList.map { it.toDomain() }
        }
    }
    
    override suspend fun getRecipeById(id: Long): Recipe? {
        return recipeDao.getRecipeWithDetails(id)?.toDomain()
    }
    
    override suspend fun insertRecipe(recipe: Recipe): Long {
        val recipeEntity = recipe.toEntity()
        val ingredientEntities = recipe.ingredients.map { it.toEntity(0) }
        val stepEntities = recipe.steps.map { it.toEntity(0) }
        
        return recipeDao.insertRecipeWithDetails(
            recipeEntity,
            ingredientEntities,
            stepEntities
        )
    }
    
    override suspend fun updateRecipe(recipe: Recipe) {
        val recipeEntity = recipe.toEntity()
        val ingredientEntities = recipe.ingredients.map { it.toEntity(recipe.id) }
        val stepEntities = recipe.steps.map { it.toEntity(recipe.id) }
        
        recipeDao.updateRecipeWithDetails(
            recipeEntity,
            ingredientEntities,
            stepEntities
        )
    }
    
    override suspend fun deleteRecipe(recipe: Recipe) {
        recipeDao.deleteRecipe(recipe.toEntity())
    }
    
    override fun searchRecipes(query: String): Flow<List<Recipe>> {
        return recipeDao.searchRecipes(query).map { recipeList ->
            recipeList.map { it.toDomain() }
        }
    }
}