package com.elm.recipebox.domain.model

data class Recipe(
    val id: Long = 0,
    val title: String,
    val description: String,
    val hashtags: String = "",
    val servings: Int,
    val cookTimeHours: String,
    val cookTimeMinutes: String,
    val difficulty: String?,
    val dishTypes: Set<String>,
    val dietTypes: Set<String>,
    val imageUri: String?,
    val ingredients: List<Ingredient> = emptyList(),
    val steps: List<Step> = emptyList(),
    val createdAt: Long = System.currentTimeMillis(),
    val updatedAt: Long = System.currentTimeMillis()
)