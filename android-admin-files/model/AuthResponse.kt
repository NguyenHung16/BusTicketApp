package com.example.busadmin.model

data class AuthResponse(
    val token: String,
    val tokenType: String = "Bearer",
    val userId: Int? = null,
    val fullName: String? = null,
    val email: String? = null,
    val role: String? = null
)
