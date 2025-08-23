package com.elm.recipebox.domain.repository

import com.elm.recipebox.domain.model.RecipeCollection
import kotlinx.coroutines.flow.Flow

interface CollectionRepository {
    fun getAllCollections(): Flow<List<RecipeCollection>>
    fun getAllCollectionsWithRecipes(): Flow<List<RecipeCollection>>
    suspend fun getCollectionById(id: Long): RecipeCollection?
    suspend fun getCollectionWithRecipes(id: Long): RecipeCollection?
    suspend fun insertCollection(collection: RecipeCollection): Long
    suspend fun updateCollection(collection: RecipeCollection)
    suspend fun deleteCollection(collection: RecipeCollection)
    suspend fun addRecipeToCollection(recipeId: Long, collectionId: Long)
    suspend fun removeRecipeFromCollection(recipeId: Long, collectionId: Long)
    suspend fun isRecipeInCollection(recipeId: Long, collectionId: Long): Boolean
}