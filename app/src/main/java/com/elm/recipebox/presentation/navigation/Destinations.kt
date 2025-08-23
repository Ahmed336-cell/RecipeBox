package com.elm.recipebox.presentation.navigation

sealed class Screen(val route: String) {
    data object Splash : Screen("splash")
    data object Onboarding : Screen("onboarding") 
    data object Home : Screen("home")
    data object Search : Screen("search")
    data object Add : Screen("add")
    data object Save : Screen("save")
    data object Profile : Screen("profile")
    data object RecipeDetail : Screen("recipe_detail")
    data object CollectionDetail : Screen("collection_detail")
}
