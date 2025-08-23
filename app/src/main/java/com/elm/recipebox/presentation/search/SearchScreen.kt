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

    var isFilterOpen by rememberSaveable { mutableStateOf(false) }
    val focusManager = LocalFocusManager.current

    Box(
        modifier = Modifier
            .fillMaxSize()
            .systemBarsPadding()
    ) {
        // Main content
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

            SearchResults(
                recipes = filteredRecipes,
                isLoading = isLoading,
                onRecipeClick = onRecipeClick
            )
        }

        // Scrim ABOVE content but BELOW drawer, so it doesn't block the drawer
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

        // Drawer (topmost)
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
        Text(
            text = "Recipes (${recipes.size})",
            style = MaterialTheme.typography.titleMedium,
            modifier = Modifier.padding(8.dp)
        )

        Spacer(modifier = Modifier.height(8.dp))

        when {
            isLoading -> {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) { CircularProgressIndicator() }
            }

            recipes.isEmpty() -> {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "No recipes found",
                        style = MaterialTheme.typography.bodyLarge,
                        color = Color.Gray
                    )
                }
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

        // bottom gradient overlay for legibility
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

        // Difficulty chip
        Row(
            modifier = Modifier
                .align(Alignment.TopStart)
                .padding(8.dp)
                .background(Color.Black.copy(alpha = 0.5f), RoundedCornerShape(8.dp))
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
                fontSize = 12.sp
            )
        }

        // Cook time chip
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
                fontSize = 10.sp
            )
        }

        // Title
        Text(
            text = recipe.title,
            color = Color.White,
            fontSize = 14.sp,
            fontWeight = FontWeight.Medium,
            maxLines = 2,
            modifier = Modifier
                .align(Alignment.BottomStart)
                .padding(8.dp)
        )
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
        // Header
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

        // Cook Time
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
                // Use minutes directly (0..240) for finer control
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

        // Difficulty
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