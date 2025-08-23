package com.elm.recipebox.domain.usecase

import com.elm.recipebox.domain.model.RecipeCollection
import com.elm.recipebox.domain.repository.CollectionRepository
import javax.inject.Inject

class DeleteCollectionUseCase @Inject constructor(
    private val repository: CollectionRepository
) {
    suspend operator fun invoke(collection: RecipeCollection): Result<Unit> {
        return try {
            repository.deleteCollection(collection)
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}
