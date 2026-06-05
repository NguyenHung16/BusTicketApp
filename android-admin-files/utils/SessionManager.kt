package com.example.busadmin.utils

import android.content.Context
import android.content.SharedPreferences
import com.example.busadmin.model.AuthResponse

class SessionManager(context: Context) {

    private val prefs: SharedPreferences =
        context.getSharedPreferences("bus_admin_prefs", Context.MODE_PRIVATE)

    companion object {
        private const val KEY_TOKEN = "key_token"
        private const val KEY_USER_ID = "key_user_id"
        private const val KEY_USER_EMAIL = "key_user_email"
        private const val KEY_USER_FULLNAME = "key_user_fullname"
        private const val KEY_USER_ROLE = "key_user_role"
    }

    fun saveToken(token: String) {
        prefs.edit().putString(KEY_TOKEN, token).apply()
    }

    fun getToken(): String? = prefs.getString(KEY_TOKEN, null)

    fun saveUserInfo(authResponse: AuthResponse) {
        prefs.edit().apply {
            putInt(KEY_USER_ID, authResponse.userId ?: -1)
            putString(KEY_USER_EMAIL, authResponse.email)
            putString(KEY_USER_FULLNAME, authResponse.fullName)
            putString(KEY_USER_ROLE, authResponse.role)
            apply()
        }
    }

    fun getUserId(): Int = prefs.getInt(KEY_USER_ID, -1)
    fun getUserEmail(): String? = prefs.getString(KEY_USER_EMAIL, null)
    fun getUserFullName(): String? = prefs.getString(KEY_USER_FULLNAME, null)
    fun getUserRole(): String? = prefs.getString(KEY_USER_ROLE, null)

    fun logout() {
        prefs.edit().clear().apply()
    }

    fun isLoggedIn(): Boolean = getToken() != null
}
