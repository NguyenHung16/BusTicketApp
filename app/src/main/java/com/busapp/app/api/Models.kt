package com.busapp.app.api

import com.google.gson.annotations.SerializedName
import java.io.Serializable

// General Response Wrapper
data class ApiResponse<T>(
    val success: Boolean,
    val message: String?,
    val data: T?,
    val errors: List<ApiError>? = null
) : Serializable

data class ApiError(
    val field: String,
    val message: String
) : Serializable

// Point Models
data class PickupDropoffPointResponse(
    val id: Int,
    val name: String,
    val address: String,
    val pointType: String, // pickup, dropoff
    val pickupTimeNote: String?
) : Serializable

data class UserResponse(val id: Int, val email: String, val fullName: String, val phone: String?, @SerializedName("roleName") val role: String, @SerializedName("isActive") val active: Boolean) : Serializable
data class UserPageResponse(val content: List<UserResponse>, val totalElements: Int, val totalPages: Int) : Serializable
data class LoginRequest(val email: String, val password: String)
data class RegisterRequest(val email: String, val password: String, val fullName: String, val phone: String)
data class UpdateProfileRequest(val fullName: String, val phone: String, val email: String)
data class AuthData(val token: String, val userId: Int, val email: String, val fullName: String, val role: String) : Serializable
data class Province(val id: Int, val name: String, val slug: String) : Serializable
data class OperatorRequest(val name: String, val phone: String, val email: String, val description: String, val amenities: String, val cancellationPolicy: String, val isActive: Boolean) : Serializable
data class OperatorResponse(val id: Int, val name: String, val phone: String, val email: String, val description: String? = null, val rating: Double? = null, val amenities: String? = null, val cancellationPolicy: String? = null) : Serializable
data class RouteRequest(val departureProvinceId: Int, val destinationProvinceId: Int, val distanceKm: Double, val durationHours: Double, val isPopular: Boolean) : Serializable
data class RouteResponse(@SerializedName("id") val id: Int, @SerializedName("departureProvinceId") val departureProvinceId: Int, @SerializedName("departureProvinceName") val departureProvince: String, @SerializedName("departureProvinceSlug") val departureProvinceSlug: String, @SerializedName("destinationProvinceId") val destinationProvinceId: Int, @SerializedName("destinationProvinceName") val destinationProvince: String, @SerializedName("destinationProvinceSlug") val destinationProvinceSlug: String, @SerializedName("distanceKm") val distanceKm: Double, @SerializedName("durationHours") val durationHours: Double, @SerializedName("isPopular") val isPopular: Boolean) : Serializable
data class TripRequest(val operatorId: Int, val routeId: Int, val vehicleTypeId: Int, val departureDate: String, val departureTime: String, val arrivalTime: String? = null, val price: String, val availableSeats: Int, val status: String = "active") : Serializable
data class TripResponse(@SerializedName("id") val id: Int, @SerializedName("operatorName") val operatorName: String, @SerializedName("departureProvince") val departureProvince: String, @SerializedName("destinationProvince") val destinationProvince: String, @SerializedName("departureDate") val departureDate: String, @SerializedName("departureTime") val departureTime: String, @SerializedName("arrivalTime") val arrivalTime: String?, @SerializedName("price") val price: Double, @SerializedName("availableSeats") val availableSeats: Int, @SerializedName("totalSeats") val totalSeats: Int, @SerializedName("vehicleTypeName") val vehicleTypeName: String?, @SerializedName("amenities") val amenities: String?, @SerializedName("operatorId") val operatorId: Int? = null, @SerializedName("routeId") val routeId: Int? = null, @SerializedName("vehicleTypeId") val vehicleTypeId: Int? = null, @SerializedName("status") val status: String? = "active") : Serializable
data class TripPageResponse(val content: List<TripResponse>, val totalElements: Int, val totalPages: Int) : Serializable
data class SeatResponse(val id: Int, val seatCode: String, val status: String, val lockedBy: Int?) : Serializable
data class SeatMapResponse(val tripId: Int, val vehicleTypeName: String, val seatLayout: String, val totalSeats: Int, val availableSeats: Int, val seats: List<SeatResponse>) : Serializable
data class BookingRequest(val tripId: Int, val seatId: Int?, val seatCode: String?, val pickupPointId: Int, val dropoffPointId: Int, val passengerName: String, val passengerPhone: String, val passengerEmail: String, val paymentMethod: String, val ticketType: String) : Serializable

data class BookingResponse(
    val id: Int,
    val bookingCode: String,
    val bookingStatus: String,
    val paymentStatus: String,
    val ticketPrice: Double,
    val departureProvince: String?,
    val destinationProvince: String?,
    val departureDate: String?,
    val departureTime: String?,
    val operatorName: String?,
    val seatCode: String?,
    val passengerName: String?,
    val passengerPhone: String?
) : Serializable

data class BookingPageResponse(
    val content: List<BookingResponse>,
    val totalElements: Int,
    val totalPages: Int
) : Serializable

data class VehicleTypeResponse(val id: Int, @SerializedName("name") val typeName: String, val seatCount: Int) : Serializable
