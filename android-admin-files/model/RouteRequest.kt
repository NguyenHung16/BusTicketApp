package com.example.busadmin.model

data class RouteRequest(
    val departureProvinceId: Int,
    val destinationProvinceId: Int,
    val distanceKm: Int? = null,
    val durationHours: Float? = null,
    val isPopular: Boolean? = null
)
