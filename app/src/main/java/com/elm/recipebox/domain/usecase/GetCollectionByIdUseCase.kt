package com.elm.recipebox.domain.usecase

import com.elm.recipebox.domain.model.RecipeCollection
import com.elm.recipebox.domain.repository.CollectionRepository
import javax.inject.Inject

class GetCollectionByIdUseCase @Inject constructor(
    private val repository: CollectionRepository
) {
    suspend operator fun invoke(collectionId: Long): Result<RecipeCollection?> {
        return try {
            val collection = repository.getCollectionWithRecipes(collectionId)
            Result.success(collection)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}
