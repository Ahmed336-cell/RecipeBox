package com.elm.recipebox.domain.model

data class RecipeCollection(
    val id: Long = 0,
    val name: String,
    val description: String = "",
    val recipes: List<Recipe> = emptyList(),
    val recipeCount: Int = recipes.size,
    val createdAt: Long = System.currentTimeMillis(),
    val updatedAt: Long = System.currentTimeMillis()
)
