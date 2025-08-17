package com.elm.recipebox

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.elm.recipebox.presentation.navigation.CustomBottomBar
import com.elm.recipebox.presentation.navigation.NavGraph
import com.elm.recipebox.presentation.navigation.Screen
import com.elm.recipebox.presentation.onboarding.OnboardingScreen
import com.elm.recipebox.presentation.splash.SplashScreen
import com.elm.recipebox.ui.theme.RecipeBoxTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            val navController = rememberNavController()
            val navBackStackEntry by navController.currentBackStackEntryAsState()
            val currentRoute = navBackStackEntry?.destination?.route

            Scaffold(
                bottomBar = {
                    if (currentRoute != Screen.Splash.route && currentRoute != Screen.Onboarding.route) {
                        CustomBottomBar(navController)
                    }
                }
            ) { innerPadding ->
                NavGraph(
                    navController = navController,
                    startDestination = Screen.Splash.route
                )
            }
        }
    }
}
