package com.example.individual_project_3

import android.content.Context
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController

@Composable
fun LoginScreen(navController: NavHostController, context: Context) {
    val databaseHelper = DatabaseHelper(context)
    var username by remember { mutableStateOf(TextFieldValue("")) }
    var password by remember { mutableStateOf(TextFieldValue("")) }
    var showErrorDialog by remember { mutableStateOf(false) }
    var showDifficultyDialog by remember { mutableStateOf(false) }

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
            Spacer(modifier = Modifier.height(50.dp))
            Column(horizontalAlignment = Alignment.Start) {
                Text(text = "Username")
                Spacer(modifier = Modifier.height(8.dp))
                TextField(
                    value = username,
                    onValueChange = { username = it },
                    modifier = Modifier.fillMaxWidth()
                )
            }
            Column(horizontalAlignment = Alignment.Start) {
                Text(text = "Password")
                Spacer(modifier = Modifier.height(8.dp))
                TextField(
                    value = password,
                    onValueChange = { password = it },
                    modifier = Modifier.fillMaxWidth(),
                    visualTransformation = PasswordVisualTransformation()
                )
            }
            Button(
                onClick = {
                    val success = databaseHelper.loginUser(username.text, password.text)
                    if (success) {
                        showDifficultyDialog = true
                    } else {
                        showErrorDialog = true
                    }
                },
                modifier = Modifier.padding(top = 40.dp)
            ) {
                Text(text = "Login")
            }
        }
    }

    if (showErrorDialog) {
        AlertDialog(
            onDismissRequest = { showErrorDialog = false },
            title = { Text("Login Error") },
            text = { Text("The username or password is incorrect. Please try again.") },
            confirmButton = {
                Button(onClick = { showErrorDialog = false }) {
                    Text("OK")
                }
            }
        )
    }

    if (showDifficultyDialog) {
        DifficultySelectionDialog(
            onSelectDifficulty = { difficulty ->
                showDifficultyDialog = false
                navController.navigate("game?difficulty=$difficulty")
            },
            onDismiss = {
                showDifficultyDialog = false
            }
        )
    }
}

@Composable
fun DifficultySelectionDialog(onSelectDifficulty: (String) -> Unit, onDismiss: () -> Unit) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Select Difficulty") },
        text = {
            Column {
                Text("Please select the difficulty level:")
            }
        },
        confirmButton = {
            Row(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                Button(onClick = { onSelectDifficulty("Easy") }) {
                    Text("Easy")
                }
                Button(onClick = { onSelectDifficulty("Hard") }) {
                    Text("Hard")
                }
            }
        },
        dismissButton = {
            Button(onClick = onDismiss) {
                Text("Cancel")
            }
        }
    )
}
