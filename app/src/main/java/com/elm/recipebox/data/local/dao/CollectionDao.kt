package com.elm.recipebox.data.local.dao

import androidx.room.*
import com.elm.recipebox.data.local.entity.CollectionEntity
import com.elm.recipebox.data.local.entity.RecipeCollectionCrossRef
import kotlinx.coroutines.flow.Flow

@Dao
interface CollectionDao {
    
    @Query("SELECT * FROM collections ORDER BY createdAt DESC")
    fun getAllCollections(): Flow<List<CollectionEntity>>
    
    @Query("SELECT * FROM collections WHERE id = :collectionId")
    suspend fun getCollectionById(collectionId: Long): CollectionEntity?
    
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertCollection(collection: CollectionEntity): Long
    
    @Update
    suspend fun updateCollection(collection: CollectionEntity)
    
    @Delete
    suspend fun deleteCollection(collection: CollectionEntity)
    
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun addRecipeToCollection(crossRef: RecipeCollectionCrossRef)
    
    @Delete
    suspend fun removeRecipeFromCollection(crossRef: RecipeCollectionCrossRef)
    
    @Query("SELECT * FROM recipe_collection_cross_ref WHERE collectionId = :collectionId")
    suspend fun getRecipesInCollection(collectionId: Long): List<RecipeCollectionCrossRef>
}