package com.elm.recipebox.presentation.navigation

sealed class Screen(val route: String) {
    object Splash : Screen("splash")
    object Onboarding : Screen("onboarding")
    object Home : Screen("home")
    object Search : Screen("search")
    object Add : Screen("add")
    object Save : Screen("save")
    object Profile : Screen("profile")
}
