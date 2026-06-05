package com.example.busadmin.model

data class TripRequest(
    val operatorId: Int,
    val routeId: Int,
    val vehicleTypeId: Int,
    val departureDate: String,
    val departureTime: String,
    val arrivalTime: String? = null,
    val price: String? = null,
    val availableSeats: Int? = null,
    val status: String? = null
)
