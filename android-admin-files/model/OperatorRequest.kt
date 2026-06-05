package com.example.busadmin.model

data class OperatorRequest(
    val name: String,
    val phone: String,
    val email: String,
    val description: String? = null,
    val amenities: String? = null,
    val cancellationPolicy: String? = null,
    val isActive: Boolean? = null
)
