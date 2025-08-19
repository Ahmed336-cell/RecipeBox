package com.elm.recipebox.presentation.recipe.add
import com.elm.recipebox.R
import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
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
import com.google.accompanist.flowlayout.FlowRow
import com.google.accompanist.flowlayout.MainAxisAlignment

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddNewRecipeStepper() {
    var currentStep by remember { mutableStateOf(0) }
    var ingredients by remember { mutableStateOf(listOf("")) }
    var steps by remember { mutableStateOf(listOf("")) }
    var recipeTitle by remember { mutableStateOf("") }
    var recipeDesc by remember { mutableStateOf("") }
    var hashtage by remember { mutableStateOf("") }

    Column(
        modifier = Modifier
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
                .fillMaxSize()
                .padding(bottom = 80.dp)
                .verticalScroll(rememberScrollState())
        ){



        when (currentStep) {
            0 -> DetailsSection(title = recipeTitle , desc = recipeDesc ,hashtage)
            {newTitle , hastag->

                hashtage=hastag
                recipeTitle=newTitle

            }
            1 -> IngredientsSection(ingredients) {
                ingredients = it
            }
            2 -> StepsSection (steps){
                steps=it
            }
            3 -> ConfirmScreen(recipeTitle,recipeDesc,ingredients,steps)
        }
            Row(

                horizontalArrangement = Arrangement.SpaceBetween,
                modifier = Modifier.fillMaxWidth()
                    .align (Alignment.BottomCenter)
                    .padding(top = 30.dp)
            ) {
                if (currentStep > 0) {
                    Button(onClick = { currentStep-- }) {
                        Text("Back")
                    }
                }
                if (currentStep < 3) {
                    Button(onClick = { currentStep++ }) {
                        Text("Next")
                    }
                } else {
                    Button(onClick = { /* Submit */ }) {
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
                    Divider(
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
fun DetailsSection(title: String, desc: String , hashtage: String,     onValueChange: (String, String) -> Unit
){

    var imageUri by remember { mutableStateOf<Uri?>(null) }

    val galleryLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? -> imageUri = uri }
    Column ( modifier = Modifier

    ) {

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(200.dp)
                .background(Color.LightGray, RoundedCornerShape(12.dp)),
            contentAlignment = Alignment.Center
        ) {
            if (imageUri != null) {
                Image(
                    painter = rememberAsyncImagePainter(
                        ImageRequest.Builder(LocalContext.current)
                            .data(imageUri)
                            .build()
                    ),
                    contentDescription = "Recipe Image",
                    modifier = Modifier.fillMaxSize(),
                    contentScale = ContentScale.Crop
                )
            } else {
                Text("No Image Selected")
            }
            Button(
                onClick = { galleryLauncher.launch("image/*") },
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .padding(8.dp)
            ) {
                Text("Pick Image")
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        CustomTextFeild(
            label = "Recipe Name",
            labelOrange = "Name",
            recipeDesc = title,
           onValueChange = { onValueChange( it,hashtage ) }
        )
        Spacer(modifier = Modifier.height(4.dp))
        CustomIncremental(labelOrange = "Number")
        Spacer(modifier = Modifier.height(4.dp))
        customCookTime(labelOrange = "Cook Time")
        Spacer(modifier = Modifier.height(4.dp))
        diffcuiltyCard()
        Spacer(modifier = Modifier.height(4.dp))
        DishTypeCard()
        Spacer(modifier = Modifier.height(4.dp))
        SuggestedDietCard()
        Spacer(modifier = Modifier.height(4.dp))
        CustomTextFeildHashtags("#eat#food" , "HashTags" ,hashtage,
            onValueChange = { onValueChange(title, it ) }

        )
    }
}



@Composable
fun CustomTextFeild(label: String, labelOrange: String, recipeDesc: String , onValueChange: (String) -> Unit) {
    Text(labelOrange,
        style = MaterialTheme.typography.titleLarge,
        color = Color.White,
        modifier = Modifier
            .background(Color(0xFFFF6339), shape = RoundedCornerShape(8.dp))
            .padding(4.dp)

    )
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(bottom = 16.dp)
            .background(Color(0xFF4058A0), shape = RoundedCornerShape(topStart = 12.dp , bottomStart = 12.dp, topEnd = 12.dp, bottomEnd = 12.dp,))
            .padding(16.dp)
    ){
        OutlinedTextField(
            value = recipeDesc,
            onValueChange = onValueChange,
            label = { Text(label , style = TextStyle(color = Color.Gray)) },
            modifier = Modifier.fillMaxWidth()
                .border(
                    width = 1.dp,
                    color = Color.White,
                    shape = RoundedCornerShape(20.dp)
                ).background(Color.White)
        )
    }
}



@Composable
fun CustomIncremental(labelOrange: String, ) {
    Text(labelOrange,
        style = MaterialTheme.typography.titleLarge,
        color = Color.White,
        modifier = Modifier
            .background(Color(0xFFFF6339), shape = RoundedCornerShape(8.dp))
            .padding(4.dp)

    )
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(bottom = 16.dp)
            .background(Color(0xFF4058A0), shape = RoundedCornerShape(topStart = 12.dp , bottomStart = 12.dp, topEnd = 12.dp, bottomEnd = 12.dp,))
            .padding(16.dp)
    ){
        Row {
            Text(
               " Serving For",
                style = TextStyle(color = Color.White, fontSize = 16.sp, fontWeight = FontWeight.Bold),
                modifier = Modifier.padding(end = 8.dp)
            )
            Icon(
                painter = painterResource(R.drawable.minus),
                contentDescription = "Increment",
                tint = Color.White,
                modifier = Modifier
                    .size(30.dp)
                    .padding(end = 8.dp)
                    .clickable {
                    }
            )
         Text(
             text = "0",
                style = TextStyle(color = Color.White, fontSize = 16.sp, fontWeight = FontWeight.Bold),
         )
            Icon(
                painter = painterResource(R.drawable.add),
                contentDescription = "Increment",
                tint = Color.White,
                modifier = Modifier
                    .size(30.dp)
                    .padding(start = 8.dp)
                    .clickable {
                        // Increment logic here
                    }
            )
            Text(
                modifier = Modifier.padding(8.dp),
                text = "people",
                style = TextStyle(color = Color.White, fontSize = 16.sp, fontWeight = FontWeight.Bold),
            )
        }
    }
}

@Composable
fun customCookTime( labelOrange: String){
    Text(labelOrange,
        style = MaterialTheme.typography.titleLarge,
        color = Color.White,
        modifier = Modifier
            .background(Color(0xFFFF6339), shape = RoundedCornerShape(8.dp))
            .padding(4.dp)

    )
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(bottom = 16.dp)
            .background(Color(0xFF4058A0), shape = RoundedCornerShape(topStart = 12.dp , bottomStart = 12.dp, topEnd = 12.dp, bottomEnd = 12.dp,))
            .padding(16.dp)
    ){
        Row (
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.fillMaxWidth()
        ){
            OutlinedTextField(
                value = "00",
                onValueChange = { /* Handle time change */ },
                modifier = Modifier
                    .border(
                        width = 1.dp,
                        color = Color.White,
                        shape = RoundedCornerShape(20.dp)
                    )
                    .width(168.dp)
                    .background(Color(0xFF4058A0))
                    .padding(end=8.dp)
                ,
                suffix = {
                    Text(
                        text = "h",
                        style = TextStyle(color = Color.White, fontSize = 16.sp, fontWeight = FontWeight.Bold),
                        modifier = Modifier.padding(start = 8.dp)
                    )
                }

            )
            OutlinedTextField(
                value = "00",
                onValueChange = { /* Handle time change */ },
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
                        style = TextStyle(color = Color.White, fontSize = 16.sp, fontWeight = FontWeight.Bold),
                        modifier = Modifier.padding(start = 8.dp)
                    )
                }

            )
        }
    }

}

@Composable
fun diffcuiltyCard(){
    var selectedDifficulty by remember { mutableStateOf<String?>(null) }
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
                        .clickable { selectedDifficulty = diff }
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
fun DishTypeCard(){
    val dishTypes = listOf(
        "BreakFast", "Launch", "Snack", "Brunch", "Dessert", "Dinner", "Appetizer"
    )
    val selectedDishTypes = remember { mutableStateListOf<String>() }

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
                    topStart = 40.dp, bottomStart = 12.dp,
                    topEnd = 12.dp, bottomEnd = 12.dp
                )
            )
            .padding(16.dp)
    ) {
        FlowRow(
            mainAxisSpacing = 8.dp,
            crossAxisSpacing = 8.dp,
            mainAxisAlignment = MainAxisAlignment.Start
        ) {
            dishTypes.forEach { type ->
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
                            if (isSelected) selectedDishTypes.remove(type)
                            else selectedDishTypes.add(type)
                        }
                        .padding(8.dp)
                ) {
                    Text(
                        text = type,
                        color = if (isSelected) Color.Black else Color.White,
                        fontSize = if (type.length > 8) 10.sp else 14.sp
                    )
                }
            }
        }
    }
}
@Composable
fun SuggestedDietCard(){
    val dietType = listOf(
        "Vegetarian", "High Fat", "Low Fat", "Sugar Free", "Lactose Free", "Gluten Free",
    )
    val selectedDietType = remember { mutableStateListOf<String>() }

    Text(
        "Diet Type",
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
        FlowRow(
            mainAxisSpacing = 8.dp,
            crossAxisSpacing = 8.dp,
            mainAxisAlignment = MainAxisAlignment.Start
        ) {
            dietType.forEach { type ->
                val isSelected = type in selectedDietType
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
                            if (isSelected) selectedDietType.remove(type)
                            else selectedDietType.add(type)
                        }
                        .padding(8.dp)
                ) {
                    Text(
                        text = type,
                        color = if (isSelected) Color.Black else Color.White,
                        fontSize = if (type.length > 10) 8.sp else 12.sp
                    )
                }
            }
        }
    }
}

@Composable
fun CustomTextFeildHashtags(label: String, labelOrange: String, hashtage: String , onValueChange: (String) -> Unit) {
    Text(labelOrange,
        style = MaterialTheme.typography.titleLarge,
        color = Color.White,
        modifier = Modifier
            .background(Color(0xFFFF6339), shape = RoundedCornerShape(8.dp))
            .padding(4.dp)

    )
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(bottom = 16.dp)
            .background(Color(0xFF4058A0), shape = RoundedCornerShape(topStart = 12.dp , bottomStart = 12.dp, topEnd = 12.dp, bottomEnd = 12.dp,))
            .padding(16.dp)
    ){
        OutlinedTextField(
            value = hashtage,
            onValueChange = onValueChange,
            label = { Text(label , style = TextStyle(color = Color.Gray)) },
            modifier = Modifier.fillMaxWidth()
                .border(
                    width = 1.dp,
                    color = Color.White,
                    shape = RoundedCornerShape(20.dp)
                ).background(Color.White)
        )
    }
}
@Preview(showBackground = true, showSystemUi = true)
@Composable
fun AddNewRecipeStepperPreview() {
    AddNewRecipeStepper()
}

