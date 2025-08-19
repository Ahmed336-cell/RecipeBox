package com.elm.recipebox.presentation.recipe.add

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun ConfirmScreen(title: String, desc: String, ingredients: List<String>, steps: List<String>) {
    Column {
        Text("Confirm Data", fontSize = 20.sp, fontWeight = FontWeight.Bold)
        Text("Title: $title")
        Text("Description: $desc")
        Spacer(modifier = Modifier.height(12.dp))
        Text("Ingredients:", fontWeight = FontWeight.Bold)
        ingredients.forEach { Text("- $it") }
        Spacer(modifier = Modifier.height(12.dp))
        Text("Steps:", fontWeight = FontWeight.Bold)
        steps.forEachIndexed { index, s -> Text("${index + 1}. $s") }
    }
}
