package com.example.individual_project_3

import androidx.compose.foundation.layout.*
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController

@Composable
fun DifficultySelectionScreen(navController: NavHostController) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(text = "Select Difficulty Level")
        Spacer(modifier = Modifier.height(24.dp))

        Button(onClick = {
            navController.navigate("game?difficulty=Easy")
        }) {
            Text(text = "Easy")
        }

        Spacer(modifier = Modifier.height(16.dp))

        Button(onClick = {
            navController.navigate("game?difficulty=Hard")
        }) {
            Text(text = "Hard")
        }
    }
}
