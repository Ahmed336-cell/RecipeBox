package com.elm.recipebox.presentation.recipe.add

import com.elm.recipebox.R
import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.systemBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
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
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.zIndex
import coil.compose.rememberAsyncImagePainter
import coil.request.ImageRequest

private val BottomBarHeight = 88.dp // Height of your CustomBottomBar

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
    modifier: Modifier = Modifier, content: @Composable () -> Unit
) {
    Box(
        modifier = modifier
            .fillMaxWidth()
            .background(Color(0xFF4058A0), shape = RoundedCornerShape(12.dp))
            .padding(16.dp)
    ) { content() }
}

@Composable
fun BlueContainerWithTopRadius(
    modifier: Modifier = Modifier, content: @Composable () -> Unit
) {
    Box(
        modifier = modifier
            .fillMaxWidth()
            .background(
                Color(0xFF4058A0),
                shape = RoundedCornerShape(topStart = 40.dp, topEnd = 12.dp, bottomEnd = 12.dp, bottomStart = 12.dp)
            )
            .padding(16.dp)
    ) { content() }
}

@OptIn(ExperimentalAnimationApi::class)
@Composable
fun AddNewRecipeStepper(
    viewModel: AddRecipeViewModel = hiltViewModel(),
    onRecipeCreated: (Long) -> Unit = {}
) {
    val recipeData by viewModel.recipeData.collectAsState()
    val currentStep by viewModel.currentStep.collectAsState()
    val uiState by viewModel.uiState.collectAsState()

    LaunchedEffect(uiState.isSuccess) {
        if (uiState.isSuccess) uiState.createdRecipeId?.let(onRecipeCreated)
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .systemBarsPadding()
                .padding(horizontal = 16.dp, vertical = 12.dp)
                .padding(bottom = 104.dp)
        ) {
            Stepper(
                totalSteps = 4,
                currentStep = currentStep,
                onStepClick = { step -> if (step <= currentStep) viewModel.goToStep(step) }
            )

            Spacer(modifier = Modifier.height(16.dp))

            uiState.error?.let { error ->
                ErrorBanner(message = error, onDismiss = viewModel::clearError)
                Spacer(modifier = Modifier.height(8.dp))
            }

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f)
            ) {
                AnimatedContent(
                    targetState = currentStep,
                    transitionSpec = { fadeIn() togetherWith fadeOut() },
                    label = "step-content"
                ) { step ->
                    when (step) {
                        0 -> DetailsSection(
                            recipeData = recipeData,
                            viewModel = viewModel,
                            onImageUriChange = viewModel::updateImageUri
                        )
                        1 -> IngredientsSection(
                            ingredients = recipeData.ingredients,
                            onChange = viewModel::updateIngredients
                        )
                        2 -> StepsSection(
                            steps = recipeData.steps,
                            onChange = viewModel::updateSteps
                        )
                        3 -> ConfirmScreen(recipeData)
                    }
                }

                if (uiState.isLoading) {
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .background(Color.Black.copy(alpha = 0.25f)),
                        contentAlignment = Alignment.Center
                    ) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            CircularProgressIndicator(color = Color.White)
                            Spacer(Modifier.height(12.dp))
                            Text("Saving...", color = Color.White, fontWeight = FontWeight.SemiBold)
                        }
                    }
                }
            }
        }

        FloatingStepperButtons(
            currentStep = currentStep,
            canGoBack = currentStep > 0,
            canGoNext = true,  // Changed
            isSaving = uiState.isLoading,
            onBack = viewModel::previousStep,
            onNext = {
                if (currentStep < 3) {
                    if (viewModel.canProceedToNextStep()) {
                        viewModel.nextStep()
                    } else {
                        viewModel.setError("Please fill in the required fields for this step.")
                    }
                } else {
                    viewModel.saveRecipe()
                }
            }
        )
    }
}

@Composable
private fun BoxScope.FloatingStepperButtons(
    currentStep: Int,
    canGoBack: Boolean,
    canGoNext: Boolean,
    isSaving: Boolean,
    onBack: () -> Unit,
    onNext: () -> Unit
) {
    // Floating, always on top, visibly ABOVE the custom bottom bar and keyboard
    Row(
        modifier = Modifier
            .zIndex(100f)                // ensure above screen content
            .align(Alignment.BottomCenter)
            .navigationBarsPadding()     // respect system nav
            .imePadding()                // respect keyboard
            // Place the buttons above your app bottom bar: its height + extra gap
            .padding(bottom = BottomBarHeight + 16.dp)
            .background(
                Color.White.copy(alpha = 0.98f),
                shape = RoundedCornerShape(32.dp)
            )
            .padding(horizontal = 20.dp, vertical = 8.dp),
        horizontalArrangement = Arrangement.spacedBy(12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Button(
            onClick = onBack,
            enabled = canGoBack && !isSaving,
            colors = ButtonDefaults.buttonColors(
                containerColor = Color(0xFF4058A0),
                contentColor = Color.White,
                disabledContainerColor = Color(0xFF4058A0).copy(alpha = 0.35f),
                disabledContentColor = Color.White.copy(alpha = 0.85f)
            )
        ) { Text("Back") }

        Button(
            onClick = onNext,
            enabled = canGoNext && !isSaving,
            colors = ButtonDefaults.buttonColors(
                containerColor = Color(0xFFFF6339),
                contentColor = Color.White,
                disabledContainerColor = Color(0xFFFF6339).copy(alpha = 0.35f),
                disabledContentColor = Color.White.copy(alpha = 0.85f)
            )
        ) { Text(if (currentStep < 3) "Next" else "Finish") }
    }
}

@Composable
private fun ErrorBanner(message: String, onDismiss: () -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(Color(0xFFFFEAE6), RoundedCornerShape(8.dp))
            .border(1.dp, Color(0xFFFF6339), RoundedCornerShape(8.dp))
            .padding(12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(text = message, color = Color(0xFF9B2C19), modifier = Modifier.weight(1f))
        Text(
            text = "Dismiss",
            color = Color(0xFF9B2C19),
            modifier = Modifier
                .clip(RoundedCornerShape(6.dp))
                .clickable(
                    indication = null,
                    interactionSource = androidx.compose.runtime.remember { MutableInteractionSource() }
                ) { onDismiss() }
                .padding(horizontal = 8.dp, vertical = 4.dp)
        )
    }
}

@Composable
fun Stepper(
    totalSteps: Int, currentStep: Int, onStepClick: (Int) -> Unit
) {
    val labels = listOf("Details", "Ingredients", "Steps", "Confirm")

    Column(modifier = Modifier.fillMaxWidth()) {
        Box(modifier = Modifier.fillMaxWidth()) {
            HorizontalDivider(
                color = Color.LightGray,
                thickness = 2.dp,
                modifier = Modifier
                    .align(Alignment.Center)
                    .fillMaxWidth()
                    .padding(horizontal = 28.dp)
            )
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                (0 until totalSteps).forEach { i ->
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        val isCompleted = i < currentStep
                        val isActive = i == currentStep
                        Box(
                            modifier = Modifier
                                .size(32.dp)
                                .clip(CircleShape)
                                .background(
                                    when {
                                        isActive -> MaterialTheme.colorScheme.primary
                                        isCompleted -> Color(0xFF4CAF50)
                                        else -> Color.Gray
                                    }
                                )
                                .border(1.dp, Color.White.copy(alpha = 0.6f), CircleShape)
                                .clickable { onStepClick(i) },
                            contentAlignment = Alignment.Center
                        ) {
                            if (isCompleted) {
                                Icon(
                                    Icons.Default.Check,
                                    contentDescription = "Completed",
                                    tint = Color.White,
                                    modifier = Modifier.size(18.dp)
                                )
                            } else {
                                Text(
                                    text = "${i + 1}",
                                    color = Color.White,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 12.sp
                                )
                            }
                        }
                        Spacer(modifier = Modifier.height(6.dp))
                        Text(
                            text = labels[i],
                            color = if (isActive) MaterialTheme.colorScheme.primary else Color.Gray,
                            fontSize = 12.sp
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun DetailsSection(
    recipeData: RecipeFormData, viewModel: AddRecipeViewModel, onImageUriChange: (Uri?) -> Unit
) {
    val galleryLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? -> onImageUriChange(uri) }

    LazyColumn(
        modifier = Modifier.fillMaxWidth(), verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        item {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(200.dp),
                shape = RoundedCornerShape(12.dp),
                colors = CardDefaults.cardColors(containerColor = Color(0xFFF5F5F5))
            ) {
                Box(
                    modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center
                ) {
                    if (recipeData.imageUri != null) {
                        Image(
                            painter = rememberAsyncImagePainter(
                                ImageRequest.Builder(LocalContext.current).data(recipeData.imageUri)
                                    .crossfade(true).build()
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
                            Text("No Image Selected", color = Color.Gray, fontSize = 14.sp)
                        }
                    }

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
        }

        item {
            CustomTextField(
                label = "Recipe Name",
                labelOrange = "Name",
                value = recipeData.title,
                onValueChange = viewModel::updateTitle
            )
        }

        item {
            CustomTextField(
                label = "Recipe Description",
                labelOrange = "Description",
                value = recipeData.description,
                onValueChange = viewModel::updateDescription
            )
        }

        item {
            ServingsSection(
                labelOrange = "Number",
                servings = recipeData.servings,
                onServingsChange = viewModel::updateServings
            )
        }

        item {
            CookTimeSection(
                labelOrange = "Cook Time",
                hours = recipeData.cookTimeHours,
                minutes = recipeData.cookTimeMinutes,
                onTimeChange = viewModel::updateCookTime
            )
        }

        item {
            DifficultyCard(
                selectedDifficulty = recipeData.difficulty,
                onDifficultyChange = viewModel::updateDifficulty
            )
        }

        item {
            DishTypeCard(
                selectedDishTypes = recipeData.dishTypes,
                onDishTypesChange = viewModel::updateDishTypes
            )
        }

        item {
            DietTypeCard(
                selectedDietTypes = recipeData.dietTypes,
                onDietTypesChange = viewModel::updateDietTypes
            )
        }

        item {
            CustomTextField(
                label = "#eat #food",
                labelOrange = "HashTags",
                value = recipeData.hashtags,
                onValueChange = viewModel::updateHashtags
            )
        }

        item { Spacer(modifier = Modifier.height(8.dp)) }
    }
}

@Composable
fun CustomTextField(
    label: String, labelOrange: String, value: String, onValueChange: (String) -> Unit
) {
    Column {
        OrangeLabel(labelOrange)
        Spacer(modifier = Modifier.height(6.dp))
        BlueContainer {
            TextField(
                value = value,
                onValueChange = onValueChange,
                placeholder = { Text(label, style = TextStyle(color = Color.Gray)) },
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(20.dp)),
                shape = RoundedCornerShape(20.dp),
                singleLine = true,
                colors = TextFieldDefaults.colors(
                    focusedContainerColor = Color.White,
                    unfocusedContainerColor = Color.White,
                    cursorColor = Color(0xFFFF6339),
                    focusedTextColor = Color.Black,
                    unfocusedTextColor = Color.Black,
                    focusedIndicatorColor = Color.Transparent,
                    unfocusedIndicatorColor = Color.Transparent,
                    disabledIndicatorColor = Color.Transparent
                )
            )
        }
    }
}

@Composable
fun ServingsSection(
    labelOrange: String, servings: Int, onServingsChange: (Int) -> Unit
) {
    Column {
        OrangeLabel(labelOrange)
        Spacer(modifier = Modifier.height(6.dp))
        BlueContainer {
            Row(
                verticalAlignment = Alignment.CenterVertically, modifier = Modifier.fillMaxWidth()
            ) {
                Text(
                    "Serving For",
                    style = TextStyle(color = Color.White, fontSize = 16.sp, fontWeight = FontWeight.Bold),
                    modifier = Modifier.padding(end = 8.dp)
                )
                Icon(
                    painter = painterResource(R.drawable.minus),
                    contentDescription = "Decrement",
                    tint = Color.White,
                    modifier = Modifier
                        .size(30.dp)
                        .padding(end = 8.dp)
                        .clickable { if (servings > 1) onServingsChange(servings - 1) }
                )
                Text(
                    text = servings.toString(),
                    style = TextStyle(color = Color.White, fontSize = 16.sp, fontWeight = FontWeight.Bold),
                    modifier = Modifier.padding(horizontal = 16.dp)
                )
                Icon(
                    painter = painterResource(R.drawable.add),
                    contentDescription = "Increment",
                    tint = Color.White,
                    modifier = Modifier
                        .size(30.dp)
                        .padding(start = 8.dp)
                        .clickable { onServingsChange(servings + 1) }
                )
                Text(
                    text = "people",
                    style = TextStyle(color = Color.White, fontSize = 16.sp, fontWeight = FontWeight.Bold),
                    modifier = Modifier.padding(start = 8.dp)
                )
            }
        }
    }
}

@Composable
fun CookTimeSection(
    labelOrange: String, hours: String, minutes: String, onTimeChange: (String, String) -> Unit
) {
    Column {
        OrangeLabel(labelOrange)
        Spacer(modifier = Modifier.height(6.dp))
        BlueContainer {
            Row(
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.fillMaxWidth()
            ) {
                TextField(
                    value = hours,
                    onValueChange = { onTimeChange(it.filter(Char::isDigit).take(2), minutes) },
                    placeholder = { Text("Hours", color = Color.LightGray) },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    modifier = Modifier
                        .width(168.dp)
                        .clip(RoundedCornerShape(20.dp)),
                    shape = RoundedCornerShape(20.dp),
                    singleLine = true,
                    suffix = {
                        Text("h", style = TextStyle(color = Color.White, fontSize = 16.sp, fontWeight = FontWeight.Bold))
                    },
                    colors = TextFieldDefaults.colors(
                        focusedContainerColor = Color(0xFF4058A0),
                        unfocusedContainerColor = Color(0xFF4058A0),
                        focusedTextColor = Color.White,
                        unfocusedTextColor = Color.White,
                        cursorColor = Color(0xFFFF6339),
                        focusedIndicatorColor = Color.Transparent,
                        unfocusedIndicatorColor = Color.Transparent,
                        disabledIndicatorColor = Color.Transparent
                    )
                )

                TextField(
                    value = minutes,
                    onValueChange = { onTimeChange(hours, it.filter(Char::isDigit).take(2)) },
                    placeholder = { Text("Minutes", color = Color.LightGray) },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    modifier = Modifier.clip(RoundedCornerShape(20.dp)),
                    shape = RoundedCornerShape(20.dp),
                    singleLine = true,
                    suffix = {
                        Text("m", style = TextStyle(color = Color.White, fontSize = 16.sp, fontWeight = FontWeight.Bold))
                    },
                    colors = TextFieldDefaults.colors(
                        focusedContainerColor = Color(0xFF4058A0),
                        unfocusedContainerColor = Color(0xFF4058A0),
                        focusedTextColor = Color.White,
                        unfocusedTextColor = Color.White,
                        cursorColor = Color(0xFFFF6339),
                        focusedIndicatorColor = Color.Transparent,
                        unfocusedIndicatorColor = Color.Transparent,
                        disabledIndicatorColor = Color.Transparent
                    )
                )
            }
        }
    }
}

@Composable
fun DifficultyCard(
    selectedDifficulty: String?, onDifficultyChange: (String?) -> Unit
) {
    Column {
        OrangeLabel("Difficulty")
        Spacer(modifier = Modifier.height(6.dp))
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
                            .border(1.dp, Color.White, RoundedCornerShape(8.dp))
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
}

@Composable
fun DishTypeCard(
    selectedDishTypes: Set<String>, onDishTypesChange: (Set<String>) -> Unit
) {
    Column {
        OrangeLabel("Dish Type")
        Spacer(modifier = Modifier.height(6.dp))
        BlueContainerWithTopRadius {
            val dishTypes = listOf("BreakFast", "Launch", "Snack", "Brunch", "Dessert", "Dinner", "Appetizer")
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
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
                                    .border(1.dp, Color.White, RoundedCornerShape(8.dp))
                                    .clickable {
                                        val newSelection = selectedDishTypes.toMutableSet()
                                        if (isSelected) newSelection.remove(type) else newSelection.add(type)
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
                        repeat(3 - rowItems.size) { Spacer(modifier = Modifier.weight(1f)) }
                    }
                }
            }
        }
    }
}

@Composable
fun DietTypeCard(
    selectedDietTypes: Set<String>, onDietTypesChange: (Set<String>) -> Unit
) {
    Column {
        OrangeLabel("Diet Type")
        Spacer(modifier = Modifier.height(6.dp))
        BlueContainerWithTopRadius {
            val dietTypes = listOf("Vegetarian", "High Fat", "Low Fat", "Sugar Free", "Lactose Free", "Gluten Free")
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
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
                                    .border(1.dp, Color.White, RoundedCornerShape(8.dp))
                                    .clickable {
                                        val newSelection = selectedDietTypes.toMutableSet()
                                        if (isSelected) newSelection.remove(type) else newSelection.add(type)
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
                        repeat(3 - rowItems.size) { Spacer(modifier = Modifier.weight(1f)) }
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