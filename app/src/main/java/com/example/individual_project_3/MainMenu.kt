package com.example.individual_project_3

import androidx.compose.foundation.layout.*
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController

@Composable
fun MainMenu(navController: NavHostController) {
    var showAccountTypeDialog by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier.fillMaxSize().padding(16.dp),
        verticalArrangement = Arrangement.Center
    ) {
        Button(
            onClick = { navController.navigate("login") },
            modifier = Modifier.fillMaxWidth().padding(8.dp)
        ) {
            Text("Login")
        }
        Button(
            onClick = { showAccountTypeDialog = true },
            modifier = Modifier.fillMaxWidth().padding(8.dp)
        ) {
            Text("Sign Up")
        }
    }

    if (showAccountTypeDialog) {
        AlertDialog(
            onDismissRequest = { showAccountTypeDialog = false },
            title = { Text("Choose Account Type") },
            text = {
                Column {
                    Text("Please select your account type:")
                    Spacer(modifier = Modifier.height(16.dp))
                }
            },
            confirmButton = {
                Column {
                    Button(onClick = {
                        showAccountTypeDialog = false
                        navController.navigate("register?accountType=parent")
                    }) {
                        Text("Sign Up as Parent")
                    }
                    Spacer(modifier = Modifier.height(8.dp))
                    Button(onClick = {
                        showAccountTypeDialog = false
                        navController.navigate("register?accountType=student")
                    }) {
                        Text("Sign Up as Student")
                    }
                }
            },
            dismissButton = {
                Button(onClick = { showAccountTypeDialog = false }) {
                    Text("Cancel")
                }
            }
        )
    }
}
