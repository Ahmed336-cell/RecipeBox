package com.elm.recipebox.presentation.collection

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import coil.compose.AsyncImage
import com.elm.recipebox.R
import com.elm.recipebox.domain.model.Recipe
import com.elm.recipebox.domain.model.RecipeCollection

@Composable
fun CollectionDetailScreen(
    collectionId: Long,
    onBackClick: () -> Unit,
    onRecipeClick: (Long) -> Unit,
    onEditCollection: (RecipeCollection) -> Unit,
    onDeleteCollection: (RecipeCollection) -> Unit
) {
    val viewModel: CollectionDetailViewModel = hiltViewModel()
    val collection by viewModel.collection.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()
    
    var showEditDialog by remember { mutableStateOf(false) }
    var showDeleteDialog by remember { mutableStateOf(false) }
    var isSelectionMode by remember { mutableStateOf(false) }
    var selectedRecipes by remember { mutableStateOf(setOf<Long>()) }
    
    LaunchedEffect(collectionId) {
        viewModel.loadCollection(collectionId)
    }
    
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFF8F9FA))
    ) {
        Column(
            modifier = Modifier.fillMaxSize()
        ) {
            if (isSelectionMode) {
                SelectionTopBar(
                    selectedCount = selectedRecipes.size,
                    onCancelSelection = { 
                        isSelectionMode = false
                        selectedRecipes = setOf()
                    },
                    onRemoveSelected = {
                        collection?.let { collectionData ->
                            selectedRecipes.forEach { recipeId ->
                                viewModel.removeRecipeFromCollection(recipeId, collectionData.id)
                            }
                        }
                        isSelectionMode = false
                        selectedRecipes = setOf()
                    }
                )
            } else {
                CollectionTopBar(
                    collection = collection,
                    onBackClick = onBackClick,
                    onEditClick = { showEditDialog = true },
                    onDeleteClick = { showDeleteDialog = true },
                    onSelectModeClick = { 
                        isSelectionMode = true
                        selectedRecipes = setOf()
                    }
                )
            }
            
            if (isLoading) {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator(color = Color(0xFF4058A0))
                }
            } else {
                collection?.let { collectionData ->
                    CollectionInfo(collection = collectionData)
                    
                    Spacer(modifier = Modifier.height(16.dp))
                    
                    if (collectionData.recipes.isEmpty()) {
                        EmptyCollectionState()
                    } else {
                        RecipesGrid(
                            recipes = collectionData.recipes,
                            onRecipeClick = onRecipeClick,
                            isSelectionMode = isSelectionMode,
                            selectedRecipes = selectedRecipes,
                            onRecipeSelected = { recipeId ->
                                selectedRecipes = if (selectedRecipes.contains(recipeId)) {
                                    selectedRecipes - recipeId
                                } else {
                                    selectedRecipes + recipeId
                                }
                            }
                        )
                    }
                }
            }
        }
        
        if (showEditDialog && collection != null) {
            EditCollectionDialog(
                collection = collection!!,
                onDismiss = { showEditDialog = false },
                onConfirm = { name, description ->
                    viewModel.updateCollection(collection!!.copy(name = name, description = description))
                    showEditDialog = false
                }
            )
        }
        
        if (showDeleteDialog && collection != null) {
            DeleteCollectionDialog(
                collection = collection!!,
                onDismiss = { showDeleteDialog = false },
                onConfirm = {
                    onDeleteCollection(collection!!)
                    showDeleteDialog = false
                }
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CollectionTopBar(
    collection: RecipeCollection?,
    onBackClick: () -> Unit,
    onEditClick: () -> Unit,
    onDeleteClick: () -> Unit,
    onSelectModeClick: () -> Unit
) {
    CenterAlignedTopAppBar(
        title = {
            Text(
                text = collection?.name ?: "Collection",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold
            )
        },
        navigationIcon = {
            IconButton(onClick = onBackClick) {
                Icon(
                    Icons.AutoMirrored.Filled.ArrowBack,
                    contentDescription = "Back",
                    tint = Color(0xFF4058A0)
                )
            }
        },
        actions = {
            IconButton(onClick = onSelectModeClick) {
                Icon(
                    Icons.Default.CheckCircle,
                    contentDescription = "Select Recipes",
                    tint = Color(0xFF4058A0)
                )
            }
            
            IconButton(onClick = onEditClick) {
                Icon(
                    Icons.Default.Edit,
                    contentDescription = "Edit Collection",
                    tint = Color(0xFF4058A0)
                )
            }
            
            IconButton(onClick = onDeleteClick) {
                Icon(
                    Icons.Default.Delete,
                    contentDescription = "Delete Collection",
                    tint = Color.Red
                )
            }
        },
        colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
            containerColor = Color.White,
            titleContentColor = Color(0xFF4058A0)
        )
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SelectionTopBar(
    selectedCount: Int,
    onCancelSelection: () -> Unit,
    onRemoveSelected: () -> Unit
) {
    CenterAlignedTopAppBar(
        title = {
            Text(
                text = "$selectedCount selected",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold
            )
        },
        navigationIcon = {
            IconButton(onClick = onCancelSelection) {
                Icon(
                    Icons.Default.Close,
                    contentDescription = "Cancel Selection",
                    tint = Color(0xFF4058A0)
                )
            }
        },
        actions = {
            if (selectedCount > 0) {
                IconButton(onClick = onRemoveSelected) {
                    Icon(
                        Icons.Default.Delete,
                        contentDescription = "Remove Selected",
                        tint = Color.Red
                    )
                }
            }
        },
        colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
            containerColor = Color(0xFFE3F2FD),
            titleContentColor = Color(0xFF4058A0)
        )
    )
}

@Composable
fun CollectionInfo(collection: RecipeCollection) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        shape = RoundedCornerShape(16.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Column(
            modifier = Modifier.padding(20.dp)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier
                        .size(60.dp)
                        .background(Color(0xFF4058A0), CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                                    Icon(
                    painter = painterResource(R.drawable.recipebook),
                    contentDescription = "Collection",
                    modifier = Modifier.size(32.dp),
                    tint = Color.White
                )
                }
                
                Spacer(modifier = Modifier.width(16.dp))
                
                Column {
                    Text(
                        text = collection.name,
                        style = MaterialTheme.typography.headlineMedium,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF4058A0)
                    )
                    
                    if (collection.description.isNotBlank()) {
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = collection.description,
                            style = MaterialTheme.typography.bodyMedium,
                            color = Color.Gray
                        )
                    }
                }
            }
            
            Spacer(modifier = Modifier.height(16.dp))
            
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = "${collection.recipeCount} recipes",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Medium,
                    color = Color(0xFF4058A0)
                )
                
                Text(
                    text = "Created ${formatDate(collection.createdAt)}",
                    style = MaterialTheme.typography.bodySmall,
                    color = Color.Gray
                )
            }
        }
    }
}

@Composable
fun RecipesGrid(
    recipes: List<Recipe>,
    onRecipeClick: (Long) -> Unit,
    isSelectionMode: Boolean = false,
    selectedRecipes: Set<Long> = setOf(),
    onRecipeSelected: (Long) -> Unit = {}
) {
    Column(
        modifier = Modifier.padding(horizontal = 16.dp)
    ) {
        Text(
            text = "Recipes in this collection",
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold,
            color = Color(0xFF4058A0),
            modifier = Modifier.padding(bottom = 12.dp)
        )
        
        LazyVerticalGrid(
            columns = GridCells.Fixed(2),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            items(recipes) { recipe ->
                RecipeCard(
                    recipe = recipe,
                    onClick = { 
                        if (isSelectionMode) {
                            onRecipeSelected(recipe.id)
                        } else {
                            onRecipeClick(recipe.id)
                        }
                    },
                    isSelectionMode = isSelectionMode,
                    isSelected = selectedRecipes.contains(recipe.id)
                )
            }
        }
    }
}

@Composable
fun RecipeCard(
    recipe: Recipe,
    onClick: () -> Unit,
    isSelectionMode: Boolean = false,
    isSelected: Boolean = false
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .aspectRatio(0.8f)
            .clickable { onClick() },
        shape = RoundedCornerShape(12.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Box {
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
            
            if (isSelectionMode) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(
                            if (isSelected) Color(0xFF4058A0).copy(alpha = 0.3f) 
                            else Color.Black.copy(alpha = 0.2f)
                        )
                )
                
                Box(
                    modifier = Modifier
                        .align(Alignment.TopEnd)
                        .padding(8.dp)
                        .size(24.dp)
                        .background(
                            if (isSelected) Color(0xFF4058A0) else Color.White,
                            CircleShape
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    if (isSelected) {
                        Icon(
                            Icons.Default.CheckCircle,
                            contentDescription = "Selected",
                            tint = Color.White,
                            modifier = Modifier.size(20.dp)
                        )
                    } else {
                        Box(
                            modifier = Modifier
                                .size(16.dp)
                                .background(Color.Transparent, CircleShape)
                        )
                    }
                }
            }
            
            Column(
                modifier = Modifier
                    .align(Alignment.BottomStart)
                    .padding(12.dp)
            ) {
                Text(
                    text = recipe.title,
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.Bold,
                    color = Color.White,
                    maxLines = 2
                )
                
                Spacer(modifier = Modifier.height(4.dp))
                
                Text(
                    text = "${recipe.cookTimeHours}h ${recipe.cookTimeMinutes}m",
                    style = MaterialTheme.typography.bodySmall,
                    color = Color.White.copy(alpha = 0.9f)
                )
            }
        }
    }
}

@Composable
fun EmptyCollectionState() {
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
                    .background(Color(0xFFF0F0F0), CircleShape),
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
                text = "Add recipes to this collection to get started!",
                style = MaterialTheme.typography.bodyLarge,
                color = Color.Gray,
                textAlign = TextAlign.Center
            )
        }
    }
}

private fun formatDate(timestamp: Long): String {
    val date = java.util.Date(timestamp)
    val formatter = java.text.SimpleDateFormat("MMM dd, yyyy", java.util.Locale.getDefault())
    return formatter.format(date)
}

@Composable
fun EditCollectionDialog(
    collection: RecipeCollection,
    onDismiss: () -> Unit,
    onConfirm: (name: String, description: String) -> Unit
) {
    var name by remember { mutableStateOf(collection.name) }
    var description by remember { mutableStateOf(collection.description) }
    
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Edit Collection") },
        text = {
            Column {
                OutlinedTextField(
                    value = name,
                    onValueChange = { name = it },
                    label = { Text("Collection Name") },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true
                )
                
                Spacer(modifier = Modifier.height(8.dp))
                
                OutlinedTextField(
                    value = description,
                    onValueChange = { description = it },
                    label = { Text("Description (Optional)") },
                    modifier = Modifier.fillMaxWidth(),
                    maxLines = 3
                )
            }
        },
        confirmButton = {
            TextButton(
                onClick = { onConfirm(name.trim(), description.trim()) },
                enabled = name.trim().isNotBlank()
            ) {
                Text("Save")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel")
            }
        }
    )
}

@Composable
fun DeleteCollectionDialog(
    collection: RecipeCollection,
    onDismiss: () -> Unit,
    onConfirm: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Delete Collection") },
        text = { 
            Text("Are you sure you want to delete '${collection.name}'? This action cannot be undone.")
        },
        confirmButton = {
            TextButton(
                onClick = onConfirm,
                colors = ButtonDefaults.textButtonColors(contentColor = Color.Red)
            ) {
                Text("Delete")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel")
            }
        }
    )
}