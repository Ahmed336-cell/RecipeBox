package com.elm.recipebox.data.mapper

import com.elm.recipebox.data.local.entity.*
import com.elm.recipebox.domain.model.*
import kotlinx.serialization.encodeToString
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.json.Json

fun RecipeEntity.toDomain(): Recipe {
    return Recipe(
        id = id,
        title = title,
        description = description,
        hashtags = hashtags,
        servings = servings,
        cookTimeHours = cookTimeHours,
        cookTimeMinutes = cookTimeMinutes,
        difficulty = difficulty,
        dishTypes = try {
            Json.decodeFromString<Set<String>>(dishTypes)
        } catch (e: Exception) {
            emptySet()
        },
        dietTypes = try {
            Json.decodeFromString<Set<String>>(dietTypes)
        } catch (e: Exception) {
            emptySet()
        },
        imageUri = imageUri,
        ingredients = emptyList(),
        steps = emptyList(),
        createdAt = createdAt,
        updatedAt = updatedAt
    )
}

fun Recipe.toEntity(): RecipeEntity {
    return RecipeEntity(
        id = id,
        title = title,
        description = description,
        hashtags = hashtags,
        servings = servings,
        cookTimeHours = cookTimeHours,
        cookTimeMinutes = cookTimeMinutes,
        difficulty = difficulty,
        dishTypes = Json.encodeToString(dishTypes),
        dietTypes = Json.encodeToString(dietTypes),
        imageUri = imageUri,
        createdAt = createdAt,
        updatedAt = updatedAt
    )
}

fun RecipeWithDetails.toDomain(): Recipe {
    return recipe.toDomain().copy(
        ingredients = ingredients.map { it.toDomain() },
        steps = steps.map { it.toDomain() }
    )
}

fun IngredientEntity.toDomain(): Ingredient {
    return Ingredient(
        id = id,
        name = name,
        order = order
    )
}

fun Ingredient.toEntity(recipeId: Long): IngredientEntity {
    return IngredientEntity(
        id = id,
        recipeId = recipeId,
        name = name,
        order = order
    )
}

fun StepEntity.toDomain(): Step {
    return Step(
        id = id,
        description = description,
        order = order
    )
}

fun Step.toEntity(recipeId: Long): StepEntity {
    return StepEntity(
        id = id,
        recipeId = recipeId,
        description = description,
        order = order
    )
}


fun CollectionEntity.toDomain(): RecipeCollection {
    return RecipeCollection(
        id = id,
        name = name,
        description = description,
        createdAt = createdAt,
        updatedAt = updatedAt
    )
}

fun RecipeCollection.toEntity(): CollectionEntity {
    return CollectionEntity(
        id = id,
        name = name,
        description = description,
        createdAt = createdAt,
        updatedAt = updatedAt
    )
}