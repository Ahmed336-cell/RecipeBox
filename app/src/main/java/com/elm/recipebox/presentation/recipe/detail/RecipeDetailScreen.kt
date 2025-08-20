package com.elm.recipebox.presentation.recipe.detail

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Share
import androidx.compose.material3.*
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
import androidx.compose.runtime.*
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import coil.compose.rememberAsyncImagePainter
import com.elm.recipebox.R
import com.elm.recipebox.domain.model.Recipe

data class RecipeDetail(
    val title: String,
    val description: String,
    val ingredients: List<String>,
    val steps: List<String>,
    val servings: Int,
    val cookTimeHours: String,
    val cookTimeMinutes: String,
    val difficulty: String?,
    val dishTypes: Set<String>,
    val dietTypes: Set<String>,
    val imageUri: String? = null
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RecipeDetailScreen(
    recipeId: Long,
    onBackClick: () -> Unit = {}
) {
    val viewModel: RecipeDetailViewModel = hiltViewModel()
    val recipe by viewModel.recipe.collectAsState()
    val uiState by viewModel.uiState.collectAsState()
    
    LaunchedEffect(recipeId) {
        viewModel.loadRecipe(recipeId)
    }
    
    when (uiState) {
        is RecipeDetailUiState.Loading -> {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator()
            }
        }
        is RecipeDetailUiState.Error -> {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                Text("Error loading recipe: ${(uiState as RecipeDetailUiState.Error).message}")
            }
        }
        is RecipeDetailUiState.Success -> {
            RecipeDetailContent(
                recipe = recipe,
                onBackClick = onBackClick
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun RecipeDetailContent(
    recipe: Recipe?,
    onBackClick: () -> Unit = {}
) {
    if (recipe == null) return
    
    val recipeDetail = RecipeDetail(
        title = recipe.title,
        description = recipe.description,
        ingredients = recipe.ingredients.map { it.name },
        steps = recipe.steps.map { it.description },
        servings = recipe.servings,
        cookTimeHours = recipe.cookTimeHours,
        cookTimeMinutes = recipe.cookTimeMinutes,
        difficulty = recipe.difficulty,
        dishTypes = recipe.dishTypes,
        dietTypes = recipe.dietTypes,
        imageUri = recipe.imageUri
    )
    
    var currentTab by remember { mutableStateOf(0) }
    val tabs = listOf("Introduction", "Ingredients", "Steps")
    
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White)
            .padding(bottom = 60.dp)
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(200.dp)
        ) {
            Image(
                painter = if (recipe.imageUri != null) {
                    rememberAsyncImagePainter(recipe.imageUri)
                } else {
                    painterResource(id = R.drawable.dish1) // default image
                },
                contentDescription = "Recipe Image",
                modifier = Modifier.fillMaxSize(),
                contentScale = ContentScale.Crop
            )
            
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(
                        Brush.verticalGradient(
                            colors = listOf(
                                Color.Black.copy(alpha = 0.3f),
                                Color.Black.copy(alpha = 0.7f)
                            )
                        )
                    )
            )
            
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .statusBarsPadding()
                    .padding(16.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(
                    onClick = onBackClick,
                    modifier = Modifier
                        .background(Color.Black.copy(alpha = 0.5f), CircleShape)
                ) {
                    Icon(
                        Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription = "Back",
                        tint = Color.White
                    )
                }
                
                Row {
                    IconButton(
                        onClick = { /* Share */ },
                        modifier = Modifier
                            .background(Color.Black.copy(alpha = 0.5f), CircleShape)
                    ) {
                        Icon(
                            Icons.Default.Share,
                            contentDescription = "Share",
                            tint = Color.White
                        )
                    }
                    
                    Spacer(modifier = Modifier.width(8.dp))
                    
                    IconButton(
                        onClick = { /* Favorite */ },
                        modifier = Modifier
                            .background(Color.Black.copy(alpha = 0.5f), CircleShape)
                    ) {
                        Icon(
                            Icons.Default.Favorite,
                            contentDescription = "Favorite",
                            tint = Color.White
                        )
                    }
                }
            }
        }
        
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = Color(0xFFEDEDED))
        ) {
            Column(
                modifier = Modifier.padding(16.dp)
            ) {
                Box(
                    modifier = Modifier.fillMaxWidth().height(40.dp)
                        .background(Color(0xFF4058A0), RoundedCornerShape(8.dp)),

                    contentAlignment = Alignment.Center

                ) {
                    Text(
                        text = recipeDetail.title,
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                }
                
                Spacer(modifier = Modifier.height(8.dp))
                
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceEvenly
                ) {
                    InfoChip(
                        icon = R.drawable.user,
                        text = "${recipeDetail.servings} Serving",
                        backgroundColor = Color.White
                    )
                    
                    InfoChip(
                        icon = R.drawable.star,
                        text = recipeDetail.difficulty ?: "Easy",
                        backgroundColor = Color.White
                    )
                    
                    val cookTime = "${recipeDetail.cookTimeHours}h ${recipeDetail.cookTimeMinutes}m"
                    InfoChip(
                        icon = R.drawable.star,
                        text = cookTime,
                        backgroundColor = Color.White
                    )
                }
            }
        }
        
        TabRow(
            selectedTabIndex = currentTab,
            modifier = Modifier.fillMaxWidth(),
            containerColor = Color.White,
            contentColor = Color.White,
            indicator = { tabPositions ->
                Box(
                    modifier = Modifier
                        .tabIndicatorOffset(tabPositions[currentTab])
                        .height(3.dp)
                        .background(Color(0xFFFF6339), RoundedCornerShape(1.5.dp))
                )
            }
        ) {
            tabs.forEachIndexed { index, title ->
                Tab(
                    selected = currentTab == index,
                    onClick = { currentTab = index },
                    text = {
                        Text(
                            text = title,
                            color = if (currentTab == index) Color.Black else Color.Black.copy(alpha = 0.7f),
                            fontWeight = if (currentTab == index) FontWeight.Bold else FontWeight.Normal
                        )
                    }
                )
            }
        }
        
        when (currentTab) {
            0 -> IntroductionTab(recipeDetail)
            1 -> IngredientsTab(recipeDetail.ingredients)
            2 -> StepsTab(recipeDetail.steps)
        }
    }
}

@Composable
fun InfoChip(
    icon: Int,
    text: String,
    backgroundColor: Color
) {
    Row(
        modifier = Modifier
            .background(backgroundColor, RoundedCornerShape(12.dp))
            .padding(horizontal = 8.dp, vertical = 4.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            painter = painterResource(icon),
            contentDescription = null,
            modifier = Modifier.size(16.dp),
            tint = Color(0xFF4058A0)
        )
        Spacer(modifier = Modifier.width(4.dp))
        Text(
            text = text,
            fontSize = 12.sp,
            color = Color(0xFF4058A0),
            fontWeight = FontWeight.Medium
        )
    }
}

@Composable
fun IntroductionTab(recipe: RecipeDetail) {
    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        item {
            Text(
                text = "Introduction",
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                color = Color.Black
            )
        }
        
        item {
            Text(
                text = "Prep Introduction",
                fontSize = 14.sp,
                color = Color.Black
            )
        }
        
        item {
            Text(
                text = "${recipe.steps.size} Steps",
                fontSize = 16.sp,
                fontWeight = FontWeight.Medium,
                color = Color.Black
            )
        }
        
        itemsIndexed(recipe.steps.take(8)) { index, step ->
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.Top
            ) {
                Box(
                    modifier = Modifier
                        .size(24.dp)
                        .background(Color(0xFFFF6339), CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = (index + 1).toString(),
                        color = Color.White,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
                
                Spacer(modifier = Modifier.width(12.dp))
                
                Text(
                    text = step.ifEmpty { "Step ${index + 1} description" },
                    color = Color.Black,
                    fontSize = 14.sp,
                    modifier = Modifier.weight(1f)
                )
            }
        }
    }
}

@Composable
fun IngredientsTab(ingredients: List<String>) {
    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        item {
            Text(
                text = "Ingredients",
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                color = Color.Black
            )
        }
        
        item {
            Text(
                text = "${ingredients.filter { it.isNotEmpty() }.size} Ingredients",
                fontSize = 16.sp,
                fontWeight = FontWeight.Medium,
                color = Color.Black
            )
        }
        
        itemsIndexed(ingredients.filter { it.isNotEmpty() }) { index, ingredient ->
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier
                        .size(24.dp)
                        .background(Color(0xFFFF6339), CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = (index + 1).toString(),
                        color = Color.White,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
                
                Spacer(modifier = Modifier.width(12.dp))
                
                Text(
                    text = ingredient,
                    color = Color.Black,
                    fontSize = 14.sp,
                    lineHeight = 20.sp,
                    modifier = Modifier.weight(1f)

                )
            }
        }
    }
}

@Composable
fun StepsTab(steps: List<String>) {
    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        item {
            Text(
                text = "Steps",
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                color = Color.Black
            )
        }
        
        item {
            Text(
                text = "${steps.filter { it.isNotEmpty() }.size} Steps",
                fontSize = 16.sp,
                fontWeight = FontWeight.Medium,
                color = Color.Black
            )
        }
        
        itemsIndexed(steps.filter { it.isNotEmpty() }) { index, step ->
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White.copy(alpha = 0.1f))
            ) {
                Row(
                    modifier = Modifier.padding(16.dp),
                    verticalAlignment = Alignment.Top
                ) {
                    Box(
                        modifier = Modifier
                            .size(32.dp)
                            .background(Color(0xFFFF6339), CircleShape),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = (index + 1).toString(),
                            color = Color.White,
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                    
                    Spacer(modifier = Modifier.width(12.dp))
                    
                    Text(
                        text = step,
                        color = Color.Black,
                        fontSize = 14.sp,
                        modifier = Modifier.weight(1f),
                        lineHeight = 20.sp
                    )
                }
            }
        }
    }
}