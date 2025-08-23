package com.elm.recipebox.presentation.navigation

import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.Fill
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
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
import com.elm.recipebox.presentation.recipe.detail.RecipeDetailScreen
import com.elm.recipebox.presentation.search.SearchScreen
import com.elm.recipebox.presentation.splash.SplashScreen
import androidx.compose.ui.draw.alpha
import com.elm.recipebox.presentation.collection.CollectionDetailScreen


@Composable
fun NavGraph(navController: NavHostController, startDestination: String) {
    NavHost(navController = navController, startDestination = startDestination) {
        composable(Screen.Splash.route) {
            SplashScreen(onSplashFinished = {
                try {
                    navController.navigate(Screen.Onboarding.route) {
                        popUpTo(Screen.Splash.route) { inclusive = true }
                    }
                } catch (e: Exception) {
                    navController.navigate(Screen.Home.route) {
                        popUpTo(Screen.Splash.route) { inclusive = true }
                    }
                }
            })
        }
        composable(Screen.Onboarding.route) {
            OnboardingScreen(onFinish = {
                navController.navigate(Screen.Search.route) {
                    popUpTo(Screen.Onboarding.route) { inclusive = true }
                }
            })
        }
        composable(Screen.Home.route) { HomeScreen() }
        composable(Screen.Search.route) {
            SearchScreen(onRecipeClick = { recipeId ->
                navController.navigate("${Screen.RecipeDetail.route}/$recipeId")
            })
        }
        composable(Screen.Add.route) {
            AddNewRecipeStepper(onRecipeCreated = { recipeId ->
                navController.navigate("${Screen.RecipeDetail.route}/$recipeId") {
                    popUpTo(Screen.Add.route) { inclusive = true }
                }
            })
        }
        composable(Screen.Save.route) {
            CollectionScreen(
                onCollectionClick = { collectionId ->
                    navController.navigate("${Screen.CollectionDetail.route}/$collectionId")
                }
            )
        }
        composable(Screen.Profile.route) { ProfileScreen() }
        composable(route = Screen.RecipeDetail.route + "/{recipeId}",
            arguments = listOf(navArgument("recipeId") {
                type = NavType.LongType
            })) { backStackEntry ->
            val recipeId = backStackEntry.arguments?.getLong("recipeId") ?: 0L
            RecipeDetailScreen(recipeId = recipeId, onBackClick = { navController.popBackStack() })
        }
        composable(route = Screen.CollectionDetail.route + "/{collectionId}",
            arguments = listOf(navArgument("collectionId") {
                type = NavType.LongType
            })) { backStackEntry ->
            val collectionId = backStackEntry.arguments?.getLong("collectionId") ?: 0L
            CollectionDetailScreen(
                collectionId = collectionId,
                onBackClick = { navController.popBackStack() },
                onRecipeClick = { recipeId ->
                    navController.navigate("${Screen.RecipeDetail.route}/$recipeId")
                },
                onEditCollection = { collection ->
                    // Handle edit collection - could navigate to edit screen or show dialog
                },
                onDeleteCollection = { collection ->
                    // Handle delete collection and navigate back
                    navController.popBackStack()
                }
            )
        }
    }
}

private data class BottomItem(
    val screen: Screen, val label: String, val iconRes: Int
)

@Composable
fun CustomBottomBar(
    navController: NavController,
    barHeight: Dp = 88.dp,
    cutoutRadius: Dp = 34.dp,
    highlightSize: Dp = 64.dp,
    highlightInner: Dp = 54.dp
) {
    val items = listOf(
        BottomItem(Screen.Home, "Home", R.drawable.home),
        BottomItem(Screen.Search, "Search", R.drawable.search),
        BottomItem(Screen.Add, "Add New", R.drawable.soup),
        BottomItem(Screen.Save, "Save", R.drawable.recipebook),
        BottomItem(Screen.Profile, "Profile", R.drawable.user),
    )
    val routesInBar = items.map { it.screen.route }

    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route.orEmpty()

    val selectedIndexRaw = items.indexOfFirst { currentRoute.startsWith(it.screen.route) }
    val selectedIndex = if (selectedIndexRaw >= 0) selectedIndexRaw else 1

    val targetFraction = (selectedIndex + 0.5f) / items.size.toFloat()
    val animatedCenterFraction by animateFloatAsState(
        targetValue = targetFraction,
        animationSpec = tween(durationMillis = 450, easing = FastOutSlowInEasing),
        label = "cutoutFraction"
    )

    val floatOffset by animateDpAsState(
        targetValue = (-highlightSize / 2.2f),
        animationSpec = tween(durationMillis = 450, easing = FastOutSlowInEasing),
        label = "floatOffset"
    )

    val barColor = Color(0xFF4058A0)
    val iconColor = Color(0xFFD0D2D6)
    val selectedLabelColor = Color.White
    val unselectedLabelColor = Color(0xFFD0D2D6)

    BoxWithConstraints(
        modifier = Modifier
            .fillMaxWidth()
            .height(barHeight)
    ) {
        val widthPx = constraints.maxWidth.toFloat()
        val density = LocalDensity.current

        val cutoutRpx = with(density) { cutoutRadius.toPx() }

        val centerX = widthPx * animatedCenterFraction

        Canvas(modifier = Modifier.fillMaxSize()) {
            val path = Path().apply {
                moveTo(0f, 0f)

                val leftStart = (centerX - cutoutRpx * 1.8f).coerceAtLeast(0f)
                lineTo(leftStart, 0f)

                cubicTo(
                    leftStart + cutoutRpx * 0.6f,
                    0f,
                    centerX - cutoutRpx * 1.2f,
                    cutoutRpx * 0.9f,
                    centerX,
                    cutoutRpx * 1.1f
                )
                cubicTo(
                    centerX + cutoutRpx * 1.2f,
                    cutoutRpx * 0.9f,
                    centerX + cutoutRpx * 0.6f,
                    0f,
                    (centerX + cutoutRpx * 1.8f).coerceAtMost(size.width),
                    0f
                )

                lineTo(size.width, 0f)
                lineTo(size.width, size.height)
                lineTo(0f, size.height)
                close()
            }
            drawPath(path = path, color = barColor, style = Fill)
        }

        val centerXdp = with(density) { (centerX).toDp() }
        Box(
            modifier = Modifier.fillMaxSize()
        ) {
            Box(
                modifier = Modifier
                    .size(highlightSize)
                    .offset(
                        x = centerXdp - highlightSize / 2, y = floatOffset
                    )
                    .clip(CircleShape)
                    .background(Color.White),
            )
            Box(
                modifier = Modifier
                    .size(highlightInner)
                    .offset(
                        x = centerXdp - highlightInner / 2,
                        y = floatOffset + (highlightSize - highlightInner) / 2
                    )
                    .clip(CircleShape)
                    .background(Color(0xFFFF5722)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    painter = painterResource(items[selectedIndex].iconRes),
                    contentDescription = items[selectedIndex].label,
                    tint = Color.White,
                    modifier = Modifier.size(28.dp)
                )
            }
        }

        Row(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 12.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.Bottom
        ) {
            items.forEachIndexed { index, item ->
                val selected = index == selectedIndex

                Column(
                    modifier = Modifier
                        .weight(1f)
                        .padding(bottom = 10.dp)
                        .clickable {
                            if (!currentRoute.startsWith(item.screen.route)) {
                                navController.navigate(item.screen.route) {
                                    launchSingleTop = true
                                    // Keep only one instance of each item route
                                    routesInBar.forEach { route ->
                                        popUpTo(route) { inclusive = false }
                                    }
                                }
                            }
                        },
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    Icon(
                        painter = painterResource(item.iconRes),
                        contentDescription = item.label,
                        tint = iconColor,
                        modifier = Modifier
                            .size(28.dp)
                            .alpha(if (selected) 0f else 1f)
                    )
                    Spacer(modifier = Modifier.height(6.dp))
                    Text(
                        text = item.label,
                        fontSize = 16.sp,
                        color = if (selected) selectedLabelColor else unselectedLabelColor,
                        fontWeight = if (selected) FontWeight.SemiBold else FontWeight.Normal
                    )
                }
            }
        }
    }
}