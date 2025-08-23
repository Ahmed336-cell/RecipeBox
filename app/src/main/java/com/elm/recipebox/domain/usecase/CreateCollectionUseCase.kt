package com.elm.recipebox.domain.usecase

import com.elm.recipebox.domain.model.RecipeCollection
import com.elm.recipebox.domain.repository.CollectionRepository
import javax.inject.Inject

class CreateCollectionUseCase @Inject constructor(
    private val repository: CollectionRepository
) {
    suspend operator fun invoke(
        name: String,
        description: String = ""
    ): Result<Long> {
        return try {
            if (name.isBlank()) {
                return Result.failure(IllegalArgumentException("Collection name cannot be empty"))
            }
            
            val collection = RecipeCollection(
                name = name.trim(),
                description = description.trim()
            )
            
            val collectionId = repository.insertCollection(collection)
            Result.success(collectionId)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}