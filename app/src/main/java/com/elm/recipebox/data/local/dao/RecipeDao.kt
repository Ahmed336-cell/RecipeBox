package com.elm.recipebox.data.local.dao

import androidx.room.*
import com.elm.recipebox.data.local.entity.*
import kotlinx.coroutines.flow.Flow

@Dao
interface RecipeDao {
    
    @Query("SELECT * FROM recipes ORDER BY createdAt DESC")
    fun getAllRecipes(): Flow<List<RecipeEntity>>
    
    @Query("SELECT * FROM recipes WHERE id = :recipeId")
    suspend fun getRecipeById(recipeId: Long): RecipeEntity?
    
    @Transaction
    @Query("SELECT * FROM recipes WHERE id = :recipeId")
    suspend fun getRecipeWithDetails(recipeId: Long): RecipeWithDetails?
    
    @Transaction
    @Query("SELECT * FROM recipes ORDER BY createdAt DESC")
    fun getAllRecipesWithDetails(): Flow<List<RecipeWithDetails>>
    
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertRecipe(recipe: RecipeEntity): Long
    
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertIngredients(ingredients: List<IngredientEntity>)
    
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertSteps(steps: List<StepEntity>)
    
    @Update
    suspend fun updateRecipe(recipe: RecipeEntity)
    
    @Delete
    suspend fun deleteRecipe(recipe: RecipeEntity)
    
    @Query("DELETE FROM ingredients WHERE recipeId = :recipeId")
    suspend fun deleteIngredientsForRecipe(recipeId: Long)
    
    @Query("DELETE FROM steps WHERE recipeId = :recipeId")
    suspend fun deleteStepsForRecipe(recipeId: Long)
    
    @Query("SELECT * FROM recipes WHERE title LIKE '%' || :query || '%' OR description LIKE '%' || :query || '%'")
    fun searchRecipes(query: String): Flow<List<RecipeEntity>>
    
    @Transaction
    suspend fun insertRecipeWithDetails(
        recipe: RecipeEntity,
        ingredients: List<IngredientEntity>,
        steps: List<StepEntity>
    ): Long {
        val recipeId = insertRecipe(recipe)
        
        val ingredientsWithRecipeId = ingredients.mapIndexed { index, ingredient ->
            ingredient.copy(recipeId = recipeId, order = index)
        }
        insertIngredients(ingredientsWithRecipeId)
        
        val stepsWithRecipeId = steps.mapIndexed { index, step ->
            step.copy(recipeId = recipeId, order = index)
        }
        insertSteps(stepsWithRecipeId)
        
        return recipeId
    }
    
    @Transaction
    suspend fun updateRecipeWithDetails(
        recipe: RecipeEntity,
        ingredients: List<IngredientEntity>,
        steps: List<StepEntity>
    ) {
        updateRecipe(recipe.copy(updatedAt = System.currentTimeMillis()))
        
        deleteIngredientsForRecipe(recipe.id)
        deleteStepsForRecipe(recipe.id)
        
        val ingredientsWithRecipeId = ingredients.mapIndexed { index, ingredient ->
            ingredient.copy(recipeId = recipe.id, order = index)
        }
        insertIngredients(ingredientsWithRecipeId)
        
        val stepsWithRecipeId = steps.mapIndexed { index, step ->
            step.copy(recipeId = recipe.id, order = index)
        }
        insertSteps(stepsWithRecipeId)
    }
}