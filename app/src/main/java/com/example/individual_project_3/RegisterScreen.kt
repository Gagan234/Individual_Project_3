package com.example.individual_project_3

import android.content.Context
import android.widget.Toast
import androidx.compose.foundation.layout.*
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController

@Composable
fun RegisterScreen(navController: NavHostController, context: Context, accountType: String) {
    val databaseHelper = DatabaseHelper(context)
    val username = remember { mutableStateOf("") }
    val password = remember { mutableStateOf("") }

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
            Text(text = "Register as $accountType")
            TextField(
                value = username.value,
                onValueChange = { username.value = it },
                label = { Text(text = "Username") }
            )
            TextField(
                value = password.value,
                onValueChange = { password.value = it },
                label = { Text(text = "Password") },
                visualTransformation = PasswordVisualTransformation()
            )
            Button(
                onClick = {

                    val success = databaseHelper.registerUser(
                        username = username.value,
                        password = password.value,
                        userType = accountType
                    )
                    if (success) {
                        Toast.makeText(context, "Registration successful!", Toast.LENGTH_LONG).show()
                        navController.navigate("login")
                    } else {
                        Toast.makeText(context, "Registration failed. Try again!", Toast.LENGTH_LONG).show()
                    }
                }
            ) {
                Text(text = "Register")
            }
        }
    }
}
