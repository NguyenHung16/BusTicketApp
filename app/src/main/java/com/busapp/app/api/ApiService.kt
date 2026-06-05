package com.busapp.app.api

import retrofit2.Call
import retrofit2.http.*

interface ApiService {

    // --- Authentication & Profile ---
    @POST("auth/login")
    fun login(@Body request: LoginRequest): Call<ApiResponse<AuthData>>

    @POST("auth/register")
    fun register(@Body request: RegisterRequest): Call<ApiResponse<AuthData>>

    @GET("auth/me")
    fun getMe(@Header("Authorization") token: String): Call<ApiResponse<UserResponse>>

    @PUT("auth/me")
    fun updateProfile(@Header("Authorization") token: String, @Body request: UpdateProfileRequest): Call<ApiResponse<UserResponse>>

    // --- Provinces ---
    @GET("provinces")
    fun getProvinces(): Call<ApiResponse<List<Province>>>

    // --- Operators ---
    @GET("operators")
    fun getOperators(@Query("page") page: Int, @Query("size") size: Int): Call<ApiResponse<Map<String, Any>>>

    @POST("operators")
    fun createOperator(@Header("Authorization") token: String, @Body request: OperatorRequest): Call<ApiResponse<OperatorResponse>>

    @PUT("operators/{id}")
    fun updateOperator(@Header("Authorization") token: String, @Path("id") id: Int, @Body request: OperatorRequest): Call<ApiResponse<OperatorResponse>>

    @DELETE("operators/{id}")
    fun deleteOperator(@Header("Authorization") token: String, @Path("id") id: Int): Call<ApiResponse<Void>>

    // --- Routes ---
    @GET("routes")
    fun getRoutes(): Call<ApiResponse<List<RouteResponse>>>

    @POST("routes")
    fun createRoute(@Header("Authorization") token: String, @Body request: RouteRequest): Call<ApiResponse<RouteResponse>>

    @PUT("routes/{id}")
    fun updateRoute(@Header("Authorization") token: String, @Path("id") id: Int, @Body request: RouteRequest): Call<ApiResponse<RouteResponse>>

    @DELETE("routes/{id}")
    fun deleteRoute(@Header("Authorization") token: String, @Path("id") id: Int): Call<ApiResponse<Void>>

    // --- Trips ---
    @GET("trips")
    fun getAllTrips(): Call<ApiResponse<List<TripResponse>>>

    @GET("trips/{id}")
    fun getTripDetails(@Path("id") id: Int): Call<ApiResponse<TripResponse>>

    @POST("trips")
    fun createTrip(@Header("Authorization") token: String, @Body request: TripRequest): Call<ApiResponse<TripResponse>>

    @PUT("trips/{id}")
    fun updateTrip(@Header("Authorization") token: String, @Path("id") id: Int, @Body request: TripRequest): Call<ApiResponse<TripResponse>>

    @DELETE("trips/{id}")
    fun deleteTrip(@Header("Authorization") token: String, @Path("id") id: Int): Call<ApiResponse<Void>>

    @GET("trips/{id}/pickup-points")
    fun getPickupPoints(@Path("id") id: Int): Call<ApiResponse<List<PickupDropoffPointResponse>>>

    @GET("trips/{id}/dropoff-points")
    fun getDropoffPoints(@Path("id") id: Int): Call<ApiResponse<List<PickupDropoffPointResponse>>>

    @GET("trips/search")
    fun searchTrips(
        @Query("departure") departure: String,
        @Query("destination") destination: String,
        @Query("departureDate") date: String
    ): Call<ApiResponse<TripPageResponse>>

    // --- Bookings ---
    @POST("bookings")
    fun createBooking(@Header("Authorization") token: String, @Body request: BookingRequest): Call<ApiResponse<BookingResponse>>

    @GET("bookings/my")
    fun getMyBookings(@Header("Authorization") token: String): Call<ApiResponse<BookingPageResponse>>

    @GET("admin/bookings")
    fun getAllBookingsAdmin(@Header("Authorization") token: String, @Query("page") page: Int, @Query("size") size: Int): Call<ApiResponse<BookingPageResponse>>

    @PATCH("bookings/{code}/payment")
    fun updatePaymentStatus(
        @Header("Authorization") token: String,
        @Path("code") code: String,
        @Query("status") status: String
    ): Call<ApiResponse<Any>>

    // --- Seats ---
    @GET("seats/trip/{tripId}")
    fun getSeats(@Path("tripId") tripId: Int): Call<ApiResponse<SeatMapResponse>>

    @POST("seats/lock/{tripId}/{seatCode}")
    fun lockSeat(@Header("Authorization") token: String, @Path("tripId") tripId: Int, @Path("seatCode") seatCode: String): Call<ApiResponse<Any>>

    // --- Vehicle Types ---
    @GET("vehicle-types")
    fun getVehicleTypes(): Call<ApiResponse<List<VehicleTypeResponse>>>
    
    // --- Admin Dashboard / Reports ---
    @GET("admin/dashboard")
    fun getAdminDashboard(@Header("Authorization") token: String): Call<ApiResponse<Map<String, Any>>>

    // --- Users (Admin) ---
    @GET("admin/users")
    fun getAllUsers(@Header("Authorization") token: String): Call<ApiResponse<UserPageResponse>>

    @PUT("api/users/{id}")
    fun updateUser(@Header("Authorization") token: String, @Path("id") id: Int, @Body request: Map<String, Any>): Call<ApiResponse<UserResponse>>

    @DELETE("api/users/{id}")
    fun deleteUser(@Header("Authorization") token: String, @Path("id") id: Int): Call<ApiResponse<Void>>
}
