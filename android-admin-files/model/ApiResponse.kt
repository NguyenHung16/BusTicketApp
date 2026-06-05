package com.example.busadmin.model

data class ApiResponse<T>(
    val success: Boolean,
    val message: String? = null,
    val data: T? = null,
    val errors: Map<String, String>? = null,
    val timestamp: String? = null
)
