package com.elm.recipebox.data.local.entity

import androidx.room.Embedded
import androidx.room.Relation
import androidx.room.Junction

data class RecipeWithDetails(
    @Embedded val recipe: RecipeEntity,
    @Relation(
        parentColumn = "id",
        entityColumn = "recipeId"
    )
    val ingredients: List<IngredientEntity>,
    @Relation(
        parentColumn = "id",
        entityColumn = "recipeId"
    )
    val steps: List<StepEntity>
)

data class CollectionWithRecipes(
    @Embedded val collection: CollectionEntity,
    @Relation(
        parentColumn = "id",
        entityColumn = "id",
        associateBy = Junction(
            value = RecipeCollectionCrossRef::class,
            parentColumn = "collectionId",
            entityColumn = "recipeId"
        )
    )
    val recipes: List<RecipeEntity>
)
