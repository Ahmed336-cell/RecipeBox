package com.elm.recipebox.presentation.navigation

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.navArgument
import com.elm.recipebox.R
import com.elm.recipebox.presentation.collection.CollectionScreen
import com.elm.recipebox.presentation.home.HomeScreen
import com.elm.recipebox.presentation.onboarding.OnboardingScreen
import com.elm.recipebox.presentation.profile.ProfileScreen
import com.elm.recipebox.presentation.recipe.add.AddNewRecipeStepper
import com.elm.recipebox.presentation.recipe.detail.RecipeDetail
import com.elm.recipebox.presentation.recipe.detail.RecipeDetailScreen
import com.elm.recipebox.presentation.search.SearchScreen
import com.elm.recipebox.presentation.splash.SplashScreen

@Composable
fun NavGraph(navController: NavHostController, startDestination: String) {
    NavHost(navController = navController, startDestination = startDestination) {
        composable(Screen.Splash.route) {
            SplashScreen(
                onSplashFinished = {
                    try {
                        navController.navigate(Screen.Onboarding.route) {
                            popUpTo(Screen.Splash.route) { inclusive = true }
                        }
                    } catch (e: Exception) {
                        navController.navigate(Screen.Home.route) {
                            popUpTo(Screen.Splash.route) { inclusive = true }
                        }
                    }
                }
            )
        }
        composable(Screen.Onboarding.route) {
            OnboardingScreen(
                onFinish = {
                    navController.navigate(Screen.Search.route) {
                        popUpTo(Screen.Onboarding.route) { inclusive = true }
                    }
                }
            )
        }
        composable(Screen.Home.route) { HomeScreen() }
        composable(Screen.Search.route) { 
            SearchScreen(
                onRecipeClick = { recipeId ->
                    navController.navigate("${Screen.RecipeDetail.route}/$recipeId")
                }
            )
        }
        composable(Screen.Add.route) { 
            AddNewRecipeStepper(
                onRecipeCreated = { recipeId ->
                    navController.navigate("${Screen.RecipeDetail.route}/$recipeId") {
                        popUpTo(Screen.Add.route) { inclusive = true }
                    }
                }
            )
        }
        composable(Screen.Save.route) { CollectionScreen() }
        composable(Screen.Profile.route) { ProfileScreen() }
        composable(
            route = Screen.RecipeDetail.route + "/{recipeId}",
            arguments = listOf(
                navArgument("recipeId") { type = NavType.LongType }
            )
        ) { backStackEntry ->
            val recipeId = backStackEntry.arguments?.getLong("recipeId") ?: 0L
            RecipeDetailScreen(
                recipeId = recipeId,
                onBackClick = { navController.popBackStack() }
            )
        }
    }
}

@Composable
fun CustomBottomBar(navController: NavController) {
    val items = listOf(
        Screen.Home,
        Screen.Search,
        Screen.Add,
        Screen.Save,
        Screen.Profile
    )

    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .height(70.dp),
        horizontalArrangement = Arrangement.SpaceAround,
        verticalAlignment = Alignment.CenterVertically
    ) {
        items.forEach { screen ->
            val selected = currentRoute == screen.route

            Box(

                modifier = Modifier
                    .weight(1f)
                    .fillMaxHeight()
                    .background( Color(0xFF4058A0) ),

                contentAlignment = Alignment.Center
            ) {
                if (selected) {
                    Box(
                        modifier = Modifier
                            .offset(y = (-20).dp)
                            .size(60.dp)
                            .background(Color(0xFFFF5722), CircleShape)
                            .clickable {
                                navController.navigate(screen.route) {
                                    launchSingleTop = true
                                }
                            },
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            painter = when (screen) {
                                Screen.Home -> painterResource(R.drawable.home )
                                Screen.Search -> painterResource(R.drawable.search)
                                Screen.Add -> painterResource(R.drawable.soup)
                                Screen.Save -> painterResource(R.drawable.recipebook)
                                Screen.Profile -> painterResource(R.drawable.user)
                                Screen.Onboarding -> painterResource(R.drawable.home)
                                Screen.Splash -> painterResource(R.drawable.home)
                                Screen.RecipeDetail -> painterResource(R.drawable.home)
                            },
                            contentDescription = screen.route,
                            tint = Color.White,

                            modifier = Modifier.size(20.dp)
                        )

                    }
                } else {
                    IconButton(onClick = {
                        navController.navigate(screen.route) {
                            launchSingleTop = true
                        }
                    }) {
                        Icon(
                            painter = when (screen) {
                                Screen.Home -> painterResource(R.drawable.home)
                                Screen.Search -> painterResource(R.drawable.search)
                                Screen.Add -> painterResource(R.drawable.soup)
                                Screen.Save -> painterResource(R.drawable.recipebook)
                                Screen.Profile -> painterResource(R.drawable.user)
                                Screen.Onboarding -> painterResource(R.drawable.home)
                                Screen.Splash -> painterResource(R.drawable.home)
                                Screen.RecipeDetail -> painterResource(R.drawable.home)
                            },
                            contentDescription = screen.route,
                            tint = Color.LightGray
                        )
                    }
                }
            }
        }
    }
}
