package com.elm.recipebox.presentation.search

import android.provider.CalendarContract
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.systemBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import coil.compose.AsyncImage
import com.elm.recipebox.R

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
    
    var isFilterOpen by remember { mutableStateOf(false) }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .systemBarsPadding()
    ) {
        Column(modifier = Modifier
            .fillMaxWidth()
            .systemBarsPadding()) {
            SearchBar(
                searchQuery = searchQuery,
                onSearchQueryChange = viewModel::updateSearchQuery,
                onFilterClick = { isFilterOpen = true }
            )

            SearchResults(
                recipes = filteredRecipes,
                isLoading = isLoading,
                onRecipeClick = onRecipeClick
            )
        }
        AnimatedVisibility(
            visible = isFilterOpen ,
            enter = slideInHorizontally(initialOffsetX = { it }),
            exit = slideOutHorizontally(targetOffsetX = { it })
        ) {

            Box(
                modifier = Modifier
                    .fillMaxHeight()
                    .width(300.dp)
                    .align(Alignment.TopEnd)
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
){
    Box(
        modifier = androidx.compose.ui.Modifier
            .fillMaxWidth()
            .height(100.dp)
            .background(
                Color(0xFF4058A0),
                shape = RoundedCornerShape(bottomEnd = 16.dp, bottomStart = 16.dp)
            )
    ){
        Row {
            OutlinedTextField(
                leadingIcon = {
                    Icon(
                        modifier = Modifier.size(30.dp),
                        painter = painterResource(R.drawable.search),
                        contentDescription = "Search Icon"
                    )
                },
                value = searchQuery,
                onValueChange = onSearchQueryChange,
                placeholder = { Text("Search recipes") },
                modifier = Modifier
                    .padding(vertical = 16.dp, horizontal = 8.dp)
                    .width(310.dp)
                    .background(Color.White)

            )


            Box(

                modifier = Modifier
                    .padding(vertical = 16.dp, horizontal = 8.dp)
                    .size(50.dp)
                    .background(Color(0xFFDEE21B), shape = RoundedCornerShape(10.dp))
                    .clip(RoundedCornerShape(10.dp))
                    .clickable { onFilterClick() } // Add click action if needed

            ) {
                Icon(
                    painter = painterResource(R.drawable.filter),
                    contentDescription = "Search Icon",
                    modifier = Modifier
                        .padding(8.dp)
                        .size(45.dp)


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
            .padding(8.dp)
    ) {
        Text(
            text = "Recipes (${recipes.size})",
            style = MaterialTheme.typography.titleMedium,
            modifier = Modifier.padding(8.dp)
        )

        Spacer(modifier = Modifier.height(8.dp))

        if (isLoading) {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator()
            }
        } else if (recipes.isEmpty()) {
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
        } else {
            LazyVerticalGrid(
                columns = GridCells.Fixed(2),
                modifier = Modifier
                    .fillMaxSize()
                    .padding(bottom = 70.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(recipes) { recipe ->
                    RealRecipeCard(
                        recipe = recipe,
                        onClick = { onRecipeClick(recipe.id) }
                    )
                }
            }
        }
    }
}


data class Recipe(
    val title: String,
    val imageUrl: String,
    val rating: Double
)

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

        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    Brush.verticalGradient(
                        colors = listOf(Color.Transparent, Color.Black.copy(alpha = 0.6f)),
                        startY = 200f
                    )
                )
        )

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

        Row(
            modifier = Modifier
                .align(Alignment.TopEnd)
                .padding(8.dp)
                .background(Color(0xFFFF6339).copy(alpha = 0.8f), RoundedCornerShape(8.dp))
                .padding(horizontal = 6.dp, vertical = 2.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "${recipe.cookTimeHours}h ${recipe.cookTimeMinutes}m",
                color = Color.White,
                fontSize = 10.sp
            )
        }

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
fun RecipeCard(recipe: Recipe) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .aspectRatio(0.8f)
            .clip(RoundedCornerShape(12.dp))
    ) {
        AsyncImage(
            model = recipe.imageUrl,
            contentDescription = recipe.title,
            modifier = Modifier.fillMaxSize(),
            contentScale = ContentScale.Crop
        )


        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    Brush.verticalGradient(
                        colors = listOf(Color.Transparent, Color.Black.copy(alpha = 0.6f)),
                        startY = 200f
                    )
                )
        )

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
                contentDescription = "Rating",
                tint = Color.White,
                modifier = Modifier.size(14.dp)
            )
            Spacer(modifier = Modifier.width(4.dp))
            Text(text = recipe.rating.toString(), color = Color.White, fontSize = 12.sp)
        }

        Text(
            text = recipe.title,
            color = Color.White,
            fontSize = 14.sp,
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
    val sliderState = maxCookTime / 60f
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
        ){
            Row {
                Icon(
                    painter = painterResource(R.drawable.filter),
                    contentDescription = "filter icon",
                    modifier = Modifier
                        .size(24.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text("Filter", style = MaterialTheme.typography.titleLarge , fontWeight = FontWeight.Bold)

            }

        }

        Spacer(modifier = Modifier.height(16.dp))

        Text("Cook Time",
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
              .background(Color(0xFF4058A0), shape = RoundedCornerShape(topStart = 40.dp , bottomStart = 12.dp, topEnd = 12.dp, bottomEnd = 12.dp,))
              .padding(16.dp)
        ){
            Column {
                Slider(
                    valueRange= 0f..1f,
                    value = sliderState,
                    onValueChange = { onCookTimeChange((it * 60).toInt()) },
                    colors = SliderDefaults.colors(
                        thumbColor = Color(0xFFDEE21B),
                        activeTrackColor = Color(0xFFDEE21B),
                        inactiveTrackColor = Color(0xFFDEE21B)

                    ),
                    modifier = Modifier.fillMaxWidth(),
                    enabled = true,
                )
                Text(
                    text = "$maxCookTime minutes",
                    style = TextStyle(color = Color.White, fontSize = 14.sp),
                    modifier = Modifier.padding(top = 8.dp)
                        .align(Alignment.CenterHorizontally)
                )
            }


        }

        Text("Difficulty",
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
                        topStart = 40.dp, bottomStart = 12.dp,
                        topEnd = 12.dp, bottomEnd = 12.dp
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
                                onDifficultyChange(if (selectedDifficulty == diff) null else diff)
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
        Text("Dish Type",
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
                        topStart = 40.dp, bottomStart = 12.dp,
                        topEnd = 12.dp, bottomEnd = 12.dp
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
                                if (isSelected) {
                                    newSet.remove(type)
                                } else {
                                    newSet.add(type)
                                }
                                onDishTypesChange(newSet)
                            }
                            .padding(8.dp)
                    ) {
                        Text(
                            text = type,
                            color = if (isSelected) Color.Black else Color.White,
                            fontSize = if (selectedDishTypes.contains("BreakFast") || selectedDishTypes.contains("Appetizer") ) 8.sp else 14.sp
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
                onClick = {
                    onClearFilters()
                },
                border = BorderStroke(1.dp, Color.Black),
                colors = ButtonDefaults.buttonColors(containerColor = Color.White ,)
            ) {
                Text("Clear All", color = Color.Gray)
            }

            Button(
                onClick = onClose,
                colors = ButtonDefaults.buttonColors(containerColor = Color.Black)
            ) {
                Text("Confirm", color = Color.White)
            }
        }
    }
}
@Preview(showBackground = true , name = "SearchScreenPreview")
@Composable
fun PreviewSearchScreen() {
    FilterDrawerContent(
        selectedDifficulty = null,
        selectedDishTypes = emptySet(),
        maxCookTime = 60,
        onDifficultyChange = {},
        onDishTypesChange = {},
        onCookTimeChange = {},
        onClearFilters = {},
        onClose = {}
    )
}