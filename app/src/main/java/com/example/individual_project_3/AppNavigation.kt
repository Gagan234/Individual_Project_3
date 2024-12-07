package com.example.individual_project_3

import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.example.mazegame.ParentDashboard

@Composable
fun AppNavigation(navController: NavHostController) {
    NavHost(navController = navController, startDestination = "mainMenu") {
        composable(route = "mainMenu") {
            MainMenu(navController = navController)
        }
        composable(route = "login") {
            LoginScreen(navController = navController, context = LocalContext.current)
        }
        composable(route = "register?accountType={accountType}") { backStackEntry ->
            val accountType = backStackEntry.arguments?.getString("accountType") ?: "parent"
            RegisterScreen(navController = navController, context = LocalContext.current, accountType = accountType)
        }
        composable(route = "difficultySelection") {
            DifficultySelectionScreen(navController = navController)
        }
        composable(route = "game?difficulty={difficulty}") { backStackEntry ->
            val difficulty = backStackEntry.arguments?.getString("difficulty") ?: "Easy"
            MazeGame(context = LocalContext.current, difficulty = difficulty)
        }
        composable(route = "parentDashboard/{username}") { backStackEntry ->
            ParentDashboard(
                username = backStackEntry.arguments?.getString("username") ?: "",
                progressData = listOf(10, 20, 30, 40, 50)
            )
        }
    }
}
