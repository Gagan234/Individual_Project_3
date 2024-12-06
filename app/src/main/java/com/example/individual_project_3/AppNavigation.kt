package com.example.individual_project_3

import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.Modifier
import androidx.compose.foundation.layout.*
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.ui.Alignment
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.example.mazegame.ParentDashboard

@Composable
fun AppNavigation(navController: NavHostController) {
    NavHost(navController = navController, startDestination = "mainMenu") {
        composable(route = "mainMenu") {
            MainMenuScreen(navController = navController)
        }
        composable(route = "login") {
            LoginScreen(navController = navController, context = LocalContext.current)
        }
        composable(route = "register") {
            RegisterScreen(navController = navController, context = LocalContext.current)
        }
        composable(route = "game") {
            MazeGame(context = LocalContext.current)
        }
        composable(route = "parentDashboard/{username}") { backStackEntry ->
            ParentDashboard(
                username = backStackEntry.arguments?.getString("username") ?: "",
                progressData = listOf(10, 20, 30, 40, 50)
            )
        }
    }
}

@Composable
fun MainMenuScreen(navController: NavHostController) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Button(
                onClick = { navController.navigate("login") }
            ) {
                Text(text = "Login")
            }
            Button(
                onClick = { navController.navigate("register") }
            ) {
                Text(text = "Sign Up")
            }
        }
    }
}
