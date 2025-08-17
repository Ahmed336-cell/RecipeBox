package com.elm.recipebox.presentation.search

import android.provider.CalendarContract
import androidx.compose.foundation.background
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
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.elm.recipebox.R

@Composable
fun SearchScreen() {
        Column(modifier = Modifier.fillMaxWidth().systemBarsPadding()) {
            SearchBar()

            SearchResults()
        }

}
@Composable
fun SearchBar(){
    Box(
        modifier = androidx.compose.ui.Modifier
            .fillMaxWidth()
            .height(100.dp)
            .background(Color(0xFF4058A0))
            .clip(RoundedCornerShape(bottomEnd = 16.dp , bottomStart = 16.dp))
    ){
        Row {
            OutlinedTextField(
                leadingIcon = {
                    Icon(
                        modifier = Modifier.size(30.dp),
                        painter = painterResource(R.drawable.search),
                        contentDescription = "Email Icon"
                    )
                },
                value = "",
                onValueChange = {},
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
                    .clickable { /* Handle filter click */ } // Add click action if needed

            ) {
                Icon(
                    painter = painterResource(R.drawable.filter),
                    contentDescription = "Search Icon",
                    modifier = Modifier.padding(8.dp).size(45.dp)


                )
            }
        }
    }
}


@Composable
fun  SearchResults() {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(8.dp)
    ) {
        Text(
            text = "Recipes",
            style = MaterialTheme.typography.titleMedium,
            modifier = Modifier.padding(8.dp)
        )

        Spacer(modifier = Modifier.height(8.dp))

        LazyVerticalGrid(
            columns = GridCells.Fixed(2),
            modifier = Modifier.fillMaxSize().padding(bottom = 70.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            items(recipeList) { recipe ->
                RecipeCard(recipe)
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

val recipeList = listOf(
    Recipe("chocolate cake with buttercream frosting",
        "https://picsum.photos/200/300?1", 4.8),
    Recipe("chocolate cake with buttercream frosting",
        "https://picsum.photos/200/300?2", 4.8),
    Recipe("chocolate cake with buttercream frosting",
        "https://picsum.photos/200/300?3", 4.8),
    Recipe("chocolate cake with buttercream frosting",
        "https://picsum.photos/200/300?4", 4.8),
    Recipe("chocolate cake with buttercream frosting",
        "https://picsum.photos/200/300?5", 4.8),
    Recipe("chocolate cake with buttercream frosting",
        "https://picsum.photos/200/300?6", 4.8),
)


@Preview(showBackground = true , name = "SearchScreenPreview")
@Composable
fun PreviewSearchScreen() {
    SearchScreen()
}