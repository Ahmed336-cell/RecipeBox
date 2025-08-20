package com.elm.recipebox.domain.model

data class Ingredient(
    val id: Long = 0,
    val name: String,
    val order: Int = 0
)