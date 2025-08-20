package com.elm.recipebox.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "recipes")
data class RecipeEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val title: String,
    val description: String,
    val hashtags: String,
    val servings: Int,
    val cookTimeHours: String,
    val cookTimeMinutes: String,
    val difficulty: String?,
    val dishTypes: String,
    val dietTypes: String,
    val imageUri: String?,
    val createdAt: Long = System.currentTimeMillis(),
    val updatedAt: Long = System.currentTimeMillis()
)