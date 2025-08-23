package com.elm.recipebox.data.repository

import com.elm.recipebox.data.local.dao.CollectionDao
import com.elm.recipebox.data.local.entity.RecipeCollectionCrossRef
import com.elm.recipebox.data.mapper.*
import com.elm.recipebox.domain.model.RecipeCollection
import com.elm.recipebox.domain.repository.CollectionRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class CollectionRepositoryImpl @Inject constructor(
    private val collectionDao: CollectionDao
) : CollectionRepository {
    
    override fun getAllCollections(): Flow<List<RecipeCollection>> {
        return collectionDao.getAllCollections().map { collections ->
            collections.map { it.toDomain() }
        }
    }
    
    override fun getAllCollectionsWithRecipes(): Flow<List<RecipeCollection>> {
        return collectionDao.getAllCollectionsWithRecipes().map { collectionsWithRecipes ->
            collectionsWithRecipes.map { collectionWithRecipes ->
                RecipeCollection(
                    id = collectionWithRecipes.collection.id,
                    name = collectionWithRecipes.collection.name,
                    description = collectionWithRecipes.collection.description,
                    recipes = collectionWithRecipes.recipes.map { it.toDomain() },
                    recipeCount = collectionWithRecipes.recipes.size,
                    createdAt = collectionWithRecipes.collection.createdAt,
                    updatedAt = collectionWithRecipes.collection.updatedAt
                )
            }
        }
    }
    
    override suspend fun getCollectionById(id: Long): RecipeCollection? {
        return collectionDao.getCollectionById(id)?.toDomain()
    }
    
    override suspend fun getCollectionWithRecipes(id: Long): RecipeCollection? {
        val collectionWithRecipes = collectionDao.getCollectionWithRecipes(id)
        return collectionWithRecipes?.let {
            RecipeCollection(
                id = it.collection.id,
                name = it.collection.name,
                description = it.collection.description,
                recipes = it.recipes.map { recipe -> recipe.toDomain() },
                recipeCount = it.recipes.size,
                createdAt = it.collection.createdAt,
                updatedAt = it.collection.updatedAt
            )
        }
    }
    
    override suspend fun insertCollection(collection: RecipeCollection): Long {
        return collectionDao.insertCollection(collection.toEntity())
    }
    
    override suspend fun updateCollection(collection: RecipeCollection) {
        collectionDao.updateCollection(collection.toEntity())
    }
    
    override suspend fun deleteCollection(collection: RecipeCollection) {
        collectionDao.deleteCollection(collection.toEntity())
    }
    
    override suspend fun addRecipeToCollection(recipeId: Long, collectionId: Long) {
        val crossRef = RecipeCollectionCrossRef(recipeId = recipeId, collectionId = collectionId)
        collectionDao.addRecipeToCollection(crossRef)
    }
    
    override suspend fun removeRecipeFromCollection(recipeId: Long, collectionId: Long) {
        val crossRef = RecipeCollectionCrossRef(recipeId = recipeId, collectionId = collectionId)
        collectionDao.removeRecipeFromCollection(crossRef)
    }
    
    override suspend fun isRecipeInCollection(recipeId: Long, collectionId: Long): Boolean {
        return collectionDao.isRecipeInCollection(recipeId, collectionId)
    }
}