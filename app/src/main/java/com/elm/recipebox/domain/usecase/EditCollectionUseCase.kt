package com.elm.recipebox.domain.usecase

import com.elm.recipebox.domain.model.RecipeCollection
import com.elm.recipebox.domain.repository.CollectionRepository
import javax.inject.Inject

class EditCollectionUseCase @Inject constructor(
    private val repository: CollectionRepository
) {
    suspend operator fun invoke(collection: RecipeCollection): Result<Unit> {
        return try {
            if (collection.name.isBlank()) {
                return Result.failure(IllegalArgumentException("Collection name cannot be empty"))
            }
            
            repository.updateCollection(collection)
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}