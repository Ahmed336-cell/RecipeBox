package com.elm.recipebox.presentation.search

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.tween
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.zIndex
import androidx.hilt.navigation.compose.hiltViewModel
import coil.compose.AsyncImage
import com.elm.recipebox.R
import com.elm.recipebox.ui.theme.basicColor
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.lazy.LazyRow

@Composable
fun SearchScreen(
    onRecipeClick: (Long) -> Unit = {}
) {
    val viewModel: SearchViewModel = hiltViewModel()
    val searchQuery by viewModel.searchQuery.collectAsState()
    val filteredRecipes by viewModel.filteredRecipes.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()
    val selectedDifficulty by viewModel.selectedDifficulty.collectAsState()
    val selectedDishTypes by viewModel.selectedDishTypes.collectAsState()
    val maxCookTime by viewModel.maxCookTime.collectAsState()
    val sortBy by viewModel.sortBy.collectAsState()

    var isFilterOpen by rememberSaveable { mutableStateOf(false) }
    val focusManager = LocalFocusManager.current

    Box(
        modifier = Modifier
            .fillMaxSize()
            .systemBarsPadding()
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .zIndex(0f)
        ) {
            SearchBar(
                searchQuery = searchQuery,
                onSearchQueryChange = { viewModel.updateSearchQuery(it) },
                onFilterClick = {
                    focusManager.clearFocus()
                    isFilterOpen = true
                }
            )

            SavedRecipesHeader(
                totalRecipes = filteredRecipes.size,
                searchQuery = searchQuery
            )



            SearchResults(
                recipes = filteredRecipes,
                isLoading = isLoading,
                onRecipeClick = onRecipeClick
            )
        }

        if (isFilterOpen) {
            Box(
                modifier = Modifier
                    .matchParentSize()
                    .zIndex(0.5f)
                    .background(Color.Black.copy(alpha = 0.25f))
                    .clickable(
                        indication = null,
                        interactionSource = remember { MutableInteractionSource() }
                    ) {
                        isFilterOpen = false
                    }
            )
        }

        AnimatedVisibility(
            modifier = Modifier
                .align(Alignment.TopEnd)
                .zIndex(1f),
            visible = isFilterOpen,
            enter = slideInHorizontally(initialOffsetX = { it }, animationSpec = tween(250)),
            exit = slideOutHorizontally(targetOffsetX = { it }, animationSpec = tween(250))
        ) {
            Box(
                modifier = Modifier
                    .fillMaxHeight()
                    .width(300.dp)
                    .background(
                        Color.White,
                        RoundedCornerShape(topStart = 16.dp, bottomStart = 16.dp)
                    )
            ) {
                FilterDrawerContent(
                    selectedDifficulty = selectedDifficulty,
                    selectedDishTypes = selectedDishTypes,
                    maxCookTime = maxCookTime,
                    onDifficultyChange = viewModel::updateDifficulty,
                    onDishTypesChange = viewModel::updateDishTypes,
                    onCookTimeChange = viewModel::updateMaxCookTime,
                    onClearFilters = viewModel::clearFilters,
                    onClose = { isFilterOpen = false }
                )
            }
        }
    }
}

@Composable
fun SearchBar(
    searchQuery: String,
    onSearchQueryChange: (String) -> Unit,
    onFilterClick: () -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(100.dp)
            .background(
                Color(0xFF4058A0),
                shape = RoundedCornerShape(bottomEnd = 16.dp, bottomStart = 16.dp)
            )
            .padding(horizontal = 12.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            OutlinedTextField(
                leadingIcon = {
                    Icon(
                        modifier = Modifier.size(22.dp),
                        painter = painterResource(R.drawable.search),
                        contentDescription = "Search Icon",
                        tint = Color.Gray
                    )
                },
                value = searchQuery,
                onValueChange = onSearchQueryChange,
                placeholder = { Text("Search recipes") },
                singleLine = true,
                colors = OutlinedTextFieldDefaults.colors(
                    unfocusedBorderColor = Color.White,
                    focusedBorderColor = Color.White,
                ),
                textStyle = TextStyle(fontSize = 14.sp),
                modifier = Modifier
                    .weight(1f)
                    .height(52.dp)
                    .clip(RoundedCornerShape(10.dp))
                    .background(Color.White),
                shape = RoundedCornerShape(10.dp)
            )

            Spacer(modifier = Modifier.width(10.dp))

            Box(
                modifier = Modifier
                    .size(52.dp)
                    .clip(RoundedCornerShape(10.dp))
                    .background(Color(0xFFDEE21B))
                    .clickable { onFilterClick() },
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    painter = painterResource(R.drawable.filter),
                    contentDescription = "Open Filters",
                    tint = Color.Unspecified,
                    modifier = Modifier.size(28.dp)
                )
            }
        }
    }
}

@Composable
fun SearchResults(
    recipes: List<com.elm.recipebox.domain.model.Recipe>,
    isLoading: Boolean,
    onRecipeClick: (Long) -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 8.dp, vertical = 8.dp)
    ) {
        when {
            isLoading -> {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        CircularProgressIndicator(
                            color = Color(0xFF4058A0)
                        )
                        Text(
                            text = "Loading your recipes...",
                            style = MaterialTheme.typography.bodyLarge,
                            color = Color.Gray
                        )
                    }
                }
            }

            recipes.isEmpty() -> {
                EmptyRecipesState()
            }

            else -> {
                LazyVerticalGrid(
                    columns = GridCells.Fixed(2),
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(bottom = 96.dp, start = 4.dp, end = 4.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    items(items = recipes, key = { it.id }) { recipe ->
                        RealRecipeCard(
                            recipe = recipe,
                            onClick = { onRecipeClick(recipe.id) }
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun EmptyRecipesState() {
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(16.dp),
            modifier = Modifier.padding(32.dp)
        ) {
            Box(
                modifier = Modifier
                    .size(80.dp)
                    .background(
                        Color(0xFFF0F0F0),
                        CircleShape
                    ),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    painter = painterResource(R.drawable.recipebook),
                    contentDescription = "No Recipes",
                    modifier = Modifier.size(40.dp),
                    tint = Color.Gray
                )
            }

            Text(
                text = "No recipes yet",
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.Bold,
                color = Color(0xFF4058A0)
            )

            Text(
                text = "Start building your recipe collection by adding your first recipe!",
                style = MaterialTheme.typography.bodyLarge,
                color = Color.Gray,
                textAlign = androidx.compose.ui.text.style.TextAlign.Center
            )

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(
                        Color(0xFFF8F9FA),
                        RoundedCornerShape(12.dp)
                    )
                    .padding(16.dp)
            ) {
                Column(
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Text(
                        text = "Tips to get started:",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.SemiBold,
                        color = Color(0xFF4058A0)
                    )
                    Text(
                        text = "Add your favorite family recipes",
                        style = MaterialTheme.typography.bodyMedium,
                        color = Color.Gray
                    )
                    Text(
                        text = "Save recipes you find online",
                        style = MaterialTheme.typography.bodyMedium,
                        color = Color.Gray
                    )
                    Text(
                        text = "Organize recipes by collections",
                        style = MaterialTheme.typography.bodyMedium,
                        color = Color.Gray
                    )
                }
            }
        }
    }
}

@Composable
fun RealRecipeCard(
    recipe: com.elm.recipebox.domain.model.Recipe,
    onClick: () -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .aspectRatio(0.8f)
            .clip(RoundedCornerShape(12.dp))
            .clickable { onClick() }
            .border(
                width = 1.dp,
                color = Color.LightGray.copy(alpha = 0.3f),
                shape = RoundedCornerShape(12.dp)
            )
    ) {
        if (recipe.imageUri != null) {
            AsyncImage(
                model = recipe.imageUri,
                contentDescription = recipe.title,
                modifier = Modifier.fillMaxSize(),
                contentScale = ContentScale.Crop
            )
        } else {
            Image(
                painter = painterResource(R.drawable.dish1),
                contentDescription = recipe.title,
                modifier = Modifier.fillMaxSize(),
                contentScale = ContentScale.Crop
            )
        }

        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    Brush.verticalGradient(
                        colors = listOf(Color.Transparent, Color.Black.copy(alpha = 0.55f)),
                        startY = 250f
                    )
                )
        )

        Row(
            modifier = Modifier
                .align(Alignment.TopStart)
                .padding(8.dp)
                .background(Color.Black.copy(alpha = 0.7f), RoundedCornerShape(8.dp))
                .padding(horizontal = 6.dp, vertical = 2.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                painter = painterResource(R.drawable.star),
                contentDescription = "Difficulty",
                tint = Color.White,
                modifier = Modifier.size(14.dp)
            )
            Spacer(modifier = Modifier.width(4.dp))
            Text(
                text = recipe.difficulty ?: "Easy",
                color = Color.White,
                fontSize = 12.sp,
                fontWeight = FontWeight.Medium
            )
        }

        Row(
            modifier = Modifier
                .align(Alignment.TopEnd)
                .padding(8.dp)
                .background(Color(0xFFFF6339).copy(alpha = 0.9f), RoundedCornerShape(8.dp))
                .padding(horizontal = 6.dp, vertical = 2.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "${recipe.cookTimeHours}h ${recipe.cookTimeMinutes}m",
                color = Color.White,
                fontSize = 10.sp,
                fontWeight = FontWeight.Medium
            )
        }
        if (recipe.servings > 0) {
            Row(
                modifier = Modifier
                    .align(Alignment.BottomStart)
                    .padding(start = 8.dp, bottom = 40.dp)
                    .background(Color.Black.copy(alpha = 0.6f), RoundedCornerShape(6.dp))
                    .padding(horizontal = 4.dp, vertical = 2.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "👥 ${recipe.servings}",
                    color = Color.White,
                    fontSize = 10.sp,
                    fontWeight = FontWeight.Medium
                )
            }
        }

        Column(
            modifier = Modifier
                .align(Alignment.BottomStart)
                .padding(8.dp)
        ) {
            Text(
                text = recipe.title,
                color = Color.White,
                fontSize = 14.sp,
                fontWeight = FontWeight.Bold,
                maxLines = 2,
                lineHeight = 16.sp
            )
            
            if (recipe.description.isNotBlank()) {
                Spacer(modifier = Modifier.height(2.dp))
                Text(
                    text = recipe.description,
                    color = Color.White.copy(alpha = 0.9f),
                    fontSize = 11.sp,
                    maxLines = 1,
                    lineHeight = 12.sp
                )
            }
        }

        if (recipe.dishTypes.isNotEmpty()) {
            val firstDishType = recipe.dishTypes.first()
            Box(
                modifier = Modifier
                    .align(Alignment.BottomEnd)
                    .padding(end = 8.dp, bottom = 40.dp)
                    .background(Color(0xFF4058A0).copy(alpha = 0.8f), RoundedCornerShape(6.dp))
                    .padding(horizontal = 6.dp, vertical = 2.dp)
            ) {
                Text(
                    text = firstDishType,
                    color = Color.White,
                    fontSize = 9.sp,
                    fontWeight = FontWeight.Medium
                )
            }
        }
    }
}

@Composable
fun FilterDrawerContent(
    selectedDifficulty: String?,
    selectedDishTypes: Set<String>,
    maxCookTime: Int,
    onDifficultyChange: (String?) -> Unit,
    onDishTypesChange: (Set<String>) -> Unit,
    onCookTimeChange: (Int) -> Unit,
    onClearFilters: () -> Unit,
    onClose: () -> Unit
) {
    val dishTypes = listOf(
        "BreakFast", "Launch", "Snack", "Brunch", "Dessert", "Dinner", "Appetizer"
    )

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 16.dp)
                .background(Color(0xFFDEE21B), shape = RoundedCornerShape(8.dp))
                .padding(16.dp)
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    painter = painterResource(R.drawable.filter),
                    contentDescription = "Filter Icon",
                    modifier = Modifier.size(24.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    "Filter",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold
                )
            }
        }

        Spacer(modifier = Modifier.height(8.dp))

        Text(
            "Cook Time",
            style = MaterialTheme.typography.titleMedium,
            color = Color.White,
            modifier = Modifier
                .background(Color(0xFFFF6339), shape = RoundedCornerShape(8.dp))
                .padding(4.dp)
        )
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 16.dp)
                .background(
                    Color(0xFF4058A0),
                    shape = RoundedCornerShape(
                        topStart = 40.dp, bottomStart = 12.dp, topEnd = 12.dp, bottomEnd = 12.dp
                    )
                )
                .padding(16.dp)
        ) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Slider(
                    valueRange = 0f..240f,
                    value = maxCookTime.toFloat(),
                    onValueChange = { onCookTimeChange(it.toInt()) },
                    colors = SliderDefaults.colors(
                        thumbColor = Color(0xFFDEE21B),
                        activeTrackColor = Color(0xFFDEE21B),
                        inactiveTrackColor = Color(0x33DEE21B)
                    ),
                    modifier = Modifier.fillMaxWidth()
                )
                Text(
                    text = "$maxCookTime minutes",
                    style = TextStyle(color = Color.White, fontSize = 14.sp),
                    modifier = Modifier.padding(top = 8.dp)
                )
            }
        }

        Text(
            "Difficulty",
            style = MaterialTheme.typography.titleMedium,
            color = Color.White,
            modifier = Modifier
                .background(Color(0xFFFF6339), shape = RoundedCornerShape(8.dp))
                .padding(4.dp)
        )
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 16.dp)
                .background(
                    Color(0xFF4058A0),
                    shape = RoundedCornerShape(
                        topStart = 40.dp, bottomStart = 12.dp, topEnd = 12.dp, bottomEnd = 12.dp
                    )
                )
                .padding(16.dp)
        ) {
            val difficulties = listOf("Easy", "Medium", "Professional")
            Row(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.fillMaxWidth()
            ) {
                difficulties.forEach { diff ->
                    val isSelected = selectedDifficulty == diff
                    Box(
                        modifier = Modifier
                            .background(
                                if (isSelected) Color(0xFFDEE21B) else Color(0xFF4058A0),
                                shape = RoundedCornerShape(8.dp)
                            )
                            .border(
                                color = Color.White,
                                width = 1.dp,
                                shape = RoundedCornerShape(8.dp)
                            )
                            .clickable {
                                onDifficultyChange(if (isSelected) null else diff)
                            }
                            .padding(horizontal = 12.dp, vertical = 8.dp)
                    ) {
                        Text(
                            text = diff,
                            color = if (isSelected) Color.Black else Color.White,
                            fontSize = if (diff == "Professional") 10.sp else 14.sp
                        )
                    }
                }
            }
        }

        Text(
            "Dish Type",
            style = MaterialTheme.typography.titleMedium,
            color = Color.White,
            modifier = Modifier
                .background(Color(0xFFFF6339), shape = RoundedCornerShape(8.dp))
                .padding(4.dp)
        )
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 16.dp)
                .background(
                    Color(0xFF4058A0),
                    shape = RoundedCornerShape(
                        topStart = 40.dp, bottomStart = 12.dp, topEnd = 12.dp, bottomEnd = 12.dp
                    )
                )
                .padding(16.dp)
        ) {
            LazyVerticalGrid(
                columns = GridCells.Fixed(3),
                modifier = Modifier.padding(bottom = 20.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(dishTypes) { type ->
                    val isSelected = type in selectedDishTypes
                    Box(
                        modifier = Modifier
                            .background(
                                if (isSelected) Color(0xFFDEE21B) else Color(0xFF4058A0),
                                shape = RoundedCornerShape(8.dp)
                            )
                            .border(
                                color = Color.White,
                                width = 1.dp,
                                shape = RoundedCornerShape(8.dp)
                            )
                            .clickable {
                                val newSet = selectedDishTypes.toMutableSet()
                                if (isSelected) newSet.remove(type) else newSet.add(type)
                                onDishTypesChange(newSet)
                            }
                            .padding(8.dp)
                    ) {
                        Text(
                            text = type,
                            color = if (isSelected) Color.Black else Color.White,
                            fontSize = if (type.length > 9) 10.sp else 12.sp
                        )
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(8.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            Button(
                onClick = onClearFilters,
                border = BorderStroke(1.dp, Color.Black),
                colors = ButtonDefaults.buttonColors(containerColor = Color.White),
                shape = RoundedCornerShape(8.dp)
            ) {
                Text("Clear All", color = Color.Gray)
            }
            Button(
                onClick = onClose,
                colors = ButtonDefaults.buttonColors(containerColor = Color.Black),
                shape = RoundedCornerShape(8.dp)
            ) {
                Text("Confirm", color = Color.White)
            }
        }
    }
}

@Composable
fun SavedRecipesHeader(
    totalRecipes: Int,
    searchQuery: String
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .background(
                Color(0xFFF5F5F5),
                shape = RoundedCornerShape(bottomStart = 16.dp, bottomEnd = 16.dp)
            )
            .padding(16.dp)
    ) {
        Column {
            if (searchQuery.isBlank()) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(
                            text = "Your Recipe Collection",
                            style = MaterialTheme.typography.headlineSmall,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFF4058A0)
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = "$totalRecipes recipes saved",
                            style = MaterialTheme.typography.bodyMedium,
                            color = Color.Gray
                        )
                    }

                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .size(40.dp)
                                .background(
                                    Color(0xFFDEE21B),
                                    CircleShape
                                ),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = totalRecipes.toString(),
                                color = Color.Black,
                                fontWeight = FontWeight.Bold,
                                fontSize = 16.sp
                            )
                        }
                    }
                }
            } else {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(
                            text = "Search Results",
                            style = MaterialTheme.typography.headlineSmall,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFF4058A0)
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = "$totalRecipes recipes found for \"$searchQuery\"",
                            style = MaterialTheme.typography.bodyMedium,
                            color = Color.Gray
                        )
                    }
                }
            }
        }
    }
}

