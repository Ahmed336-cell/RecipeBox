package com.elm.recipebox.domain.usecase

import com.elm.recipebox.domain.repository.CollectionRepository
import javax.inject.Inject

class RemoveFromCollectionUseCase @Inject constructor(
    private val repository: CollectionRepository
) {
    suspend operator fun invoke(recipeId: Long, collectionId: Long): Result<Unit> {
        return try {
            repository.removeRecipeFromCollection(recipeId, collectionId)
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}