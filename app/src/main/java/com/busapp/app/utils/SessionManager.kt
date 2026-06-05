package com.busapp.app.utils

import android.content.Context
import android.content.SharedPreferences

class SessionManager(context: Context) {
    private val prefs: SharedPreferences = context.getSharedPreferences("bus_app_prefs", Context.MODE_PRIVATE)

    companion object {
        const val USER_TOKEN = "user_token"
        const val USER_NAME = "user_name"
        const val USER_ROLE = "user_role"
    }

    fun saveAuthToken(token: String) {
        val editor = prefs.edit()
        editor.putString(USER_TOKEN, token)
        editor.apply()
    }

    fun fetchAuthToken(): String? {
        return prefs.getString(USER_TOKEN, null)
    }

    fun saveUserData(name: String, role: String) {
        val editor = prefs.edit()
        editor.putString(USER_NAME, name)
        editor.putString(USER_ROLE, role)
        editor.apply()
    }

    fun getUserName(): String? = prefs.getString(USER_NAME, "Admin")
    fun getUserRole(): String? = prefs.getString(USER_ROLE, null)

    fun clearSession() {
        prefs.edit().clear().apply()
    }
}
