package com.elm.recipebox.presentation.recipe.add

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun ConfirmScreen(recipeData: RecipeFormData) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .verticalScroll(rememberScrollState())
            .padding(16.dp)
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(Color(0xFFFF6339), RoundedCornerShape(12.dp))
                .padding(16.dp),
            contentAlignment = Alignment.Center
        ) {
            Text(
                "Confirm Recipe Details",
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold,
                color = Color.White
            )
        }

        Spacer(modifier = Modifier.height(16.dp))

        ConfirmSection(
            title = "Recipe Name",
            content = recipeData.title.ifEmpty { "Not specified" }
        )

        if (recipeData.description.isNotEmpty()) {
            ConfirmSection(
                title = "Description",
                content = recipeData.description
            )
        }

        ConfirmSection(
            title = "Servings",
            content = "${recipeData.servings} people"
        )

        val cookTime = if (recipeData.cookTimeHours != "00" || recipeData.cookTimeMinutes != "00") {
            "${recipeData.cookTimeHours}h ${recipeData.cookTimeMinutes}m"
        } else {
            "Not specified"
        }
        ConfirmSection(
            title = "Cook Time",
            content = cookTime
        )

        if (recipeData.difficulty != null) {
            ConfirmSection(
                title = "Difficulty",
                content = recipeData.difficulty
            )
        }

        if (recipeData.dishTypes.isNotEmpty()) {
            ConfirmSection(
                title = "Dish Types",
                content = recipeData.dishTypes.joinToString(", ")
            )
        }

        if (recipeData.dietTypes.isNotEmpty()) {
            ConfirmSection(
                title = "Diet Types",
                content = recipeData.dietTypes.joinToString(", ")
            )
        }

        ConfirmSection(
            title = "Ingredients",
            content = if (recipeData.ingredients.isNotEmpty() && recipeData.ingredients.any { it.isNotEmpty() }) {
                recipeData.ingredients.filter { it.isNotEmpty() }.joinToString("\n") { "• $it" }
            } else {
                "No ingredients specified"
            }
        )

        ConfirmSection(
            title = "Steps",
            content = if (recipeData.steps.isNotEmpty() && recipeData.steps.any { it.isNotEmpty() }) {
                recipeData.steps.filter { it.isNotEmpty() }.mapIndexed { index, step ->
                    "${index + 1}. $step"
                }.joinToString("\n")
            } else {
                "No steps specified"
            }
        )
    }
}

@Composable
private fun ConfirmSection(
    title: String,
    content: String
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp)
    ) {
        Text(
            title,
            fontSize = 18.sp,
            fontWeight = FontWeight.Bold,
            color = Color(0xFFFF6339),
            modifier = Modifier.padding(bottom = 4.dp)
        )

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(
                    Color(0xFF4058A0),
                    RoundedCornerShape(8.dp)
                )
                .padding(12.dp)
        ) {
            Text(
                content,
                fontSize = 14.sp,
                color = Color.White,
                lineHeight = 20.sp
            )
        }
    }
}