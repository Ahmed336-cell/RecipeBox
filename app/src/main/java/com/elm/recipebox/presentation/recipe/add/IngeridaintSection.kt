package com.elm.recipebox.presentation.recipe.add

import androidx.compose.foundation.background
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun IngredientsSection(ingredients: List<String>, onChange: (List<String>) -> Unit) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp)
            .verticalScroll(rememberScrollState())
    ) {
        // Header
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(Color(0xFFFF6339), RoundedCornerShape(12.dp))
                .padding(16.dp),
            contentAlignment = Alignment.Center
        ) {
            Text(
                "Ingredients", 
                fontSize = 24.sp, 
                fontWeight = FontWeight.Bold,
                color = Color.White
            )
        }
        
        Spacer(modifier = Modifier.height(16.dp))
        
        // Ingredients List - Start with one empty card if list is empty
        val displayIngredients = if (ingredients.isEmpty()) listOf("") else ingredients
        
        Column(
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            displayIngredients.forEachIndexed { index, ingredient ->
                IngredientCard(
                    ingredient = ingredient,
                    index = index,
                    showDelete = displayIngredients.size > 1,
                    onValueChange = { newValue ->
                        val newList = displayIngredients.toMutableList()
                        newList[index] = newValue
                        onChange(newList)
                    },
                    onDelete = {
                        val newList = displayIngredients.toMutableList()
                        newList.removeAt(index)
                        onChange(newList)
                    }
                )
            }
        }
        
        Spacer(modifier = Modifier.height(16.dp))
        
        // Add Button
        OutlinedButton(
            onClick = { 
                val newList = displayIngredients.toMutableList()
                newList.add("")
                onChange(newList)
            },
            modifier = Modifier.fillMaxWidth(),
            colors = ButtonDefaults.outlinedButtonColors(
                contentColor = Color(0xFFFF6339)
            ),
            border = BorderStroke(
                width = 1.dp,
                color = Color(0xFFFF6339)
            )
        ) {
            Icon(
                Icons.Default.Add,
                contentDescription = "Add Ingredient",
                modifier = Modifier.size(20.dp)
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                "Add Ingredient",
                fontSize = 16.sp,
                fontWeight = FontWeight.Medium
            )
        }
    }
}

@Composable
private fun IngredientCard(
    ingredient: String,
    index: Int,
    showDelete: Boolean,
    onValueChange: (String) -> Unit,
    onDelete: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(
            containerColor = Color.White
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Ingredient ${index + 1}",
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Medium,
                    color = Color(0xFF4058A0)
                )
                
                if (showDelete) {
                    IconButton(
                        onClick = onDelete,
                        modifier = Modifier.size(24.dp)
                    ) {
                        Icon(
                            Icons.Default.Delete,
                            contentDescription = "Delete Ingredient",
                            tint = Color(0xFFFF6339),
                            modifier = Modifier.size(20.dp)
                        )
                    }
                }
            }
            
            Spacer(modifier = Modifier.height(8.dp))
            
            OutlinedTextField(
                value = ingredient,
                onValueChange = onValueChange,
                placeholder = {
                    Text(
                        "Enter ingredient name",
                        color = Color.Gray,
                        fontSize = 14.sp
                    )
                },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(8.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = Color(0xFF4058A0),
                    unfocusedBorderColor = Color.LightGray
                ),
                singleLine = true
            )
        }
    }
}