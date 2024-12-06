package com.example.individual_project_3

import android.content.Context

class Auth {
    companion object {
        fun saveAccount(context: Context, username: String, password: String, isParent: Boolean) {
            val sharedPreferences = context.getSharedPreferences("accounts", Context.MODE_PRIVATE)
            sharedPreferences.edit()
                .putString("$username-password", password)
                .putBoolean("$username-isParent", isParent)
                .apply()
        }

        fun isValidLogin(context: Context, username: String, password: String): Boolean {
            val sharedPreferences = context.getSharedPreferences("accounts", Context.MODE_PRIVATE)
            val storedPassword = sharedPreferences.getString("$username-password", null)
            return storedPassword == password
        }

        fun isParentAccount(context: Context, username: String): Boolean {
            val sharedPreferences = context.getSharedPreferences("accounts", Context.MODE_PRIVATE)
            return sharedPreferences.getBoolean("$username-isParent", false)
        }
    }
}
