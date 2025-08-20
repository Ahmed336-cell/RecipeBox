package com.elm.recipebox.presentation.recipe.add
import com.elm.recipebox.R
import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.systemBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll

import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.collectAsState
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.rememberAsyncImagePainter
import coil.request.ImageRequest

// Reusable composable functions
@Composable
fun OrangeLabel(text: String) {
    Text(
        text = text,
        style = MaterialTheme.typography.titleLarge,
        color = Color.White,
        modifier = Modifier
            .background(Color(0xFFFF6339), shape = RoundedCornerShape(8.dp))
            .padding(4.dp)
    )
}

@Composable
fun BlueContainer(
    modifier: Modifier = Modifier,
    content: @Composable () -> Unit
) {
    Box(
        modifier = modifier
            .fillMaxWidth()
            .padding(bottom = 16.dp)
            .background(
                Color(0xFF4058A0), 
                shape = RoundedCornerShape(
                    topStart = 12.dp, 
                    bottomStart = 12.dp, 
                    topEnd = 12.dp, 
                    bottomEnd = 12.dp
                )
            )
            .padding(16.dp)
    ) {
        content()
    }
}

@Composable
fun BlueContainerWithTopRadius(
    modifier: Modifier = Modifier,
    content: @Composable () -> Unit
) {
    Box(
        modifier = modifier
            .fillMaxWidth()
            .padding(bottom = 16.dp)
            .background(
                Color(0xFF4058A0),
                shape = RoundedCornerShape(
                    topStart = 40.dp, 
                    bottomStart = 12.dp,
                    topEnd = 12.dp, 
                    bottomEnd = 12.dp
                )
            )
            .padding(16.dp)
    ) {
        content()
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddNewRecipeStepper(
    viewModel: AddRecipeViewModel = hiltViewModel(),
    onRecipeCreated: (Long) -> Unit = {}
) {
    val recipeData by viewModel.recipeData.collectAsState()
    val currentStep by viewModel.currentStep.collectAsState()
    val uiState by viewModel.uiState.collectAsState()
    
    // Handle UI state changes
    LaunchedEffect(uiState.isSuccess) {
        if (uiState.isSuccess) {
            uiState.createdRecipeId?.let { recipeId ->
                onRecipeCreated(recipeId)
            }
        }
    }
    
    // Show error if any
    uiState.error?.let { error ->
        LaunchedEffect(error) {
            // Show error snackbar or dialog
            viewModel.clearError()
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
            .systemBarsPadding()
    ) {
        Stepper(
            totalSteps = 4,
            currentStep = currentStep
        )

        Spacer(modifier = Modifier.height(24.dp))
        
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f)
                .padding(bottom = 80.dp)
        ) {
                        when (currentStep) {
                0 -> DetailsSection(
                    recipeData = recipeData,
                    viewModel = viewModel,
                    onImageUriChange = { uri -> viewModel.updateImageUri(uri) }
                )
                1 -> IngredientsSection(recipeData.ingredients) { newIngredients ->
                    viewModel.updateIngredients(newIngredients)
                }
                2 -> StepsSection(recipeData.steps) { newSteps ->
                    viewModel.updateSteps(newSteps)
                }
                3 -> ConfirmScreen(recipeData)
            }
            
            Row(
                horizontalArrangement = Arrangement.SpaceBetween,
                modifier = Modifier
                    .fillMaxWidth()
                    .align(Alignment.BottomCenter)
                    .padding(top = 30.dp)
            ) {
                if (currentStep > 0) {
                    Button(onClick = { viewModel.previousStep() }) {
                        Text("Back")
                    }
                }
                if (currentStep < 3) {
                    Button(onClick = { viewModel.nextStep() }) {
                        Text("Next")
                    }
                } else {
                    Button(onClick = { viewModel.saveRecipe() }) {
                        Text("Finish")
                    }
                }
            }
        }
    }
}

@Composable
fun Stepper(totalSteps: Int, currentStep: Int) {
    val steps = listOf("Recipe Details", "Ingredients", "Steps", "Confirm")

    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        for (i in 0 until totalSteps) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Box(
                    modifier = Modifier
                        .size(60.dp)
                        .clip(CircleShape)
                        .background(
                            if (i <= currentStep) MaterialTheme.colorScheme.primary
                            else Color.Gray
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = if (i == currentStep) steps[i] else (i + 1).toString(),
                        color = Color.White,
                        fontWeight = FontWeight.Bold,
                        textAlign = TextAlign.Center,
                        style = TextStyle(fontSize = 12.sp),
                        modifier = Modifier.padding(4.dp)
                    )
                }

                if (i < totalSteps - 1) {
                    HorizontalDivider(
                        color = if (i < currentStep) MaterialTheme.colorScheme.primary else Color.Gray,
                        thickness = 2.dp,
                        modifier = Modifier
                            .height(2.dp)
                            .width(50.dp)
                    )
                }
            }
        }
    }
}

@Composable
fun DetailsSection(
    recipeData: RecipeFormData,
    viewModel: AddRecipeViewModel,
    onImageUriChange: (Uri?) -> Unit
) {
    val galleryLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? -> onImageUriChange(uri) }
    
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .verticalScroll(rememberScrollState())
    ) {
        // Image Section
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .height(200.dp),
            shape = RoundedCornerShape(12.dp),
            colors = CardDefaults.cardColors(
                containerColor = Color(0xFFF5F5F5)
            )
        ) {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                if (recipeData.imageUri != null) {
                    Image(
                        painter = rememberAsyncImagePainter(
                            ImageRequest.Builder(LocalContext.current)
                                .data(recipeData.imageUri)
                                .build()
                        ),
                        contentDescription = "Recipe Image",
                        modifier = Modifier.fillMaxSize(),
                        contentScale = ContentScale.Crop
                    )
                } else {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center
                    ) {
                        Image(
                            painter = painterResource(R.drawable.dish1),
                            contentDescription = "Placeholder",
                            modifier = Modifier.size(80.dp),
                            alpha = 0.3f
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            "No Image Selected",
                            color = Color.Gray,
                            fontSize = 14.sp
                        )
                    }
                }
                
                // Floating action button for image selection
                FloatingActionButton(
                    onClick = { galleryLauncher.launch("image/*") },
                    modifier = Modifier
                        .align(Alignment.BottomEnd)
                        .padding(16.dp)
                        .size(48.dp),
                    containerColor = Color(0xFFFF6339),
                    contentColor = Color.White
                ) {
                    Icon(
                        painter = painterResource(R.drawable.add),
                        contentDescription = "Add Image",
                        modifier = Modifier.size(24.dp)
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Recipe Name
        CustomTextField(
            label = "Recipe Name",
            labelOrange = "Name",
            value = recipeData.title,
            onValueChange = viewModel::updateTitle
        )
        
        Spacer(modifier = Modifier.height(8.dp))
        
        // Recipe Description
        CustomTextField(
            label = "Recipe Description",
            labelOrange = "Description",
            value = recipeData.description,
            onValueChange = viewModel::updateDescription
        )
        
        Spacer(modifier = Modifier.height(8.dp))
        
        // Servings
        ServingsSection(
            labelOrange = "Number",
            servings = recipeData.servings,
            onServingsChange = viewModel::updateServings
        )
        
        Spacer(modifier = Modifier.height(8.dp))
        
        // Cook Time
        CookTimeSection(
            labelOrange = "Cook Time",
            hours = recipeData.cookTimeHours,
            minutes = recipeData.cookTimeMinutes,
            onTimeChange = { hours, minutes -> viewModel.updateCookTime(hours, minutes) }
        )
        
        Spacer(modifier = Modifier.height(8.dp))
        
        // Difficulty
        DifficultyCard(
            selectedDifficulty = recipeData.difficulty,
            onDifficultyChange = viewModel::updateDifficulty
        )
        
        Spacer(modifier = Modifier.height(8.dp))
        
        // Dish Type
        DishTypeCard(
            selectedDishTypes = recipeData.dishTypes,
            onDishTypesChange = viewModel::updateDishTypes
        )
        
        Spacer(modifier = Modifier.height(8.dp))
        
        // Diet Type
        DietTypeCard(
            selectedDietTypes = recipeData.dietTypes,
            onDietTypesChange = viewModel::updateDietTypes
        )
        
        Spacer(modifier = Modifier.height(8.dp))
        
        // Hashtags
        CustomTextField(
            label = "#eat#food",
            labelOrange = "HashTags",
            value = recipeData.hashtags,
            onValueChange = viewModel::updateHashtags
        )
    }
}

@Composable
fun CustomTextField(
    label: String,
    labelOrange: String,
    value: String,
    onValueChange: (String) -> Unit
) {
    OrangeLabel(labelOrange)
    BlueContainer {
        OutlinedTextField(
            value = value,
            onValueChange = onValueChange,
            label = { Text(label, style = TextStyle(color = Color.Gray)) },
            modifier = Modifier
                .fillMaxWidth()
                .border(
                    width = 1.dp,
                    color = Color.White,
                    shape = RoundedCornerShape(20.dp)
                )
                .background(Color.White)
        )
    }
}

@Composable
fun ServingsSection(
    labelOrange: String,
    servings: Int,
    onServingsChange: (Int) -> Unit
) {
    OrangeLabel(labelOrange)
    BlueContainer {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(
                "Serving For",
                style = TextStyle(
                    color = Color.White, 
                    fontSize = 16.sp, 
                    fontWeight = FontWeight.Bold
                ),
                modifier = Modifier.padding(end = 8.dp)
            )
            
            Icon(
                painter = painterResource(R.drawable.minus),
                contentDescription = "Decrement",
                tint = Color.White,
                modifier = Modifier
                    .size(30.dp)
                    .padding(end = 8.dp)
                    .clickable { 
                        if (servings > 1) onServingsChange(servings - 1)
                    }
            )
            
            Text(
                text = servings.toString(),
                style = TextStyle(
                    color = Color.White, 
                    fontSize = 16.sp, 
                    fontWeight = FontWeight.Bold
                ),
                modifier = Modifier.padding(horizontal = 16.dp)
            )
            
            Icon(
                painter = painterResource(R.drawable.add),
                contentDescription = "Increment",
                tint = Color.White,
                modifier = Modifier
                    .size(30.dp)
                    .padding(start = 8.dp)
                    .clickable { 
                        onServingsChange(servings + 1)
                    }
            )
            
            Text(
                text = "people",
                style = TextStyle(
                    color = Color.White, 
                    fontSize = 16.sp, 
                    fontWeight = FontWeight.Bold
                ),
                modifier = Modifier.padding(start = 8.dp)
            )
        }
    }
}

@Composable
fun CookTimeSection(
    labelOrange: String,
    hours: String,
    minutes: String,
    onTimeChange: (String, String) -> Unit
) {
    OrangeLabel(labelOrange)
    BlueContainer {
        Row(
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.fillMaxWidth()
        ) {
            OutlinedTextField(
                value = hours,
                onValueChange = { onTimeChange(it, minutes) },
                modifier = Modifier
                    .border(
                        width = 1.dp,
                        color = Color.White,
                        shape = RoundedCornerShape(20.dp)
                    )
                    .width(168.dp)
                    .background(Color(0xFF4058A0))
                    .padding(end = 8.dp),
                suffix = {
                    Text(
                        text = "h",
                        style = TextStyle(
                            color = Color.White, 
                            fontSize = 16.sp, 
                            fontWeight = FontWeight.Bold
                        ),
                        modifier = Modifier.padding(start = 8.dp)
                    )
                }
            )
            
            OutlinedTextField(
                value = minutes,
                onValueChange = { onTimeChange(hours, it) },
                modifier = Modifier
                    .border(
                        width = 1.dp,
                        color = Color.White,
                        shape = RoundedCornerShape(20.dp)
                    )
                    .padding(start = 8.dp)
                    .background(Color(0xFF4058A0)),
                suffix = {
                    Text(
                        text = "m",
                        style = TextStyle(
                            color = Color.White, 
                            fontSize = 16.sp, 
                            fontWeight = FontWeight.Bold
                        ),
                        modifier = Modifier.padding(start = 8.dp)
                    )
                }
            )
        }
    }
}

@Composable
fun DifficultyCard(
    selectedDifficulty: String?,
    onDifficultyChange: (String?) -> Unit
) {
    OrangeLabel("Difficulty")
    BlueContainerWithTopRadius {
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
                        .clickable { onDifficultyChange(diff) }
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
}

@Composable
fun DishTypeCard(
    selectedDishTypes: Set<String>,
    onDishTypesChange: (Set<String>) -> Unit
) {
    OrangeLabel("Dish Type")
    BlueContainerWithTopRadius {
        val dishTypes = listOf(
            "BreakFast", "Launch", "Snack", "Brunch", "Dessert", "Dinner", "Appetizer"
        )

        Column(
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            // Split items into rows of 3
            dishTypes.chunked(3).forEach { rowItems ->
                Row(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    rowItems.forEach { type ->
                        val isSelected = type in selectedDishTypes
                        Box(
                            modifier = Modifier
                                .weight(1f)
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
                                    val newSelection = selectedDishTypes.toMutableSet()
                                    if (isSelected) {
                                        newSelection.remove(type)
                                    } else {
                                        newSelection.add(type)
                                    }
                                    onDishTypesChange(newSelection)
                                }
                                .padding(8.dp)
                        ) {
                            Text(
                                text = type,
                                color = if (isSelected) Color.Black else Color.White,
                                fontSize = if (type.length > 8) 10.sp else 14.sp,
                                modifier = Modifier.align(Alignment.Center)
                            )
                        }
                    }
                    // Fill empty spaces in the row
                    repeat(3 - rowItems.size) {
                        Spacer(modifier = Modifier.weight(1f))
                    }
                }
            }
        }
    }
}

@Composable
fun DietTypeCard(
    selectedDietTypes: Set<String>,
    onDietTypesChange: (Set<String>) -> Unit
) {
    OrangeLabel("Diet Type")
    BlueContainerWithTopRadius {
        val dietTypes = listOf(
            "Vegetarian", "High Fat", "Low Fat", "Sugar Free", "Lactose Free", "Gluten Free"
        )

        Column(
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            // Split items into rows of 3
            dietTypes.chunked(3).forEach { rowItems ->
                Row(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    rowItems.forEach { type ->
                        val isSelected = type in selectedDietTypes
                        Box(
                            modifier = Modifier
                                .weight(1f)
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
                                    val newSelection = selectedDietTypes.toMutableSet()
                                    if (isSelected) {
                                        newSelection.remove(type)
                                    } else {
                                        newSelection.add(type)
                                    }
                                    onDietTypesChange(newSelection)
                                }
                                .padding(8.dp)
                        ) {
                            Text(
                                text = type,
                                color = if (isSelected) Color.Black else Color.White,
                                fontSize = if (type.length > 10) 8.sp else 12.sp,
                                modifier = Modifier.align(Alignment.Center)
                            )
                        }
                    }
                    // Fill empty spaces in the row
                    repeat(3 - rowItems.size) {
                        Spacer(modifier = Modifier.weight(1f))
                    }
                }
            }
        }
    }
}

@Preview(showBackground = true, showSystemUi = true)
@Composable
fun AddNewRecipeStepperPreview() {
    AddNewRecipeStepper()
}

