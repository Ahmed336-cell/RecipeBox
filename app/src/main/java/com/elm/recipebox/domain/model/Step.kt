package com.elm.recipebox.domain.model

data class Step(
    val id: Long = 0,
    val description: String,
    val order: Int = 0
)