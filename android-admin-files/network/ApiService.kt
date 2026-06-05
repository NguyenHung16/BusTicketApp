package com.example.busadmin.network

import com.example.busadmin.model.ApiResponse
import com.example.busadmin.model.AuthResponse
import com.example.busadmin.model.OperatorRequest
import com.example.busadmin.model.RouteRequest
import com.example.busadmin.model.TripRequest
import retrofit2.http.*

interface ApiService {

    @POST("api/auth/login")
    suspend fun login(@Body body: Map<String, String>): ApiResponse<AuthResponse>

    // Trips
    @POST("api/trips")
    suspend fun createTrip(
        @Header("Authorization") bearer: String,
        @Body request: TripRequest
    ): ApiResponse<Any>

    @PUT("api/trips/{id}")
    suspend fun updateTrip(
        @Header("Authorization") bearer: String,
        @Path("id") id: Int,
        @Body request: TripRequest
    ): ApiResponse<Any>

    // Routes
    @POST("api/routes")
    suspend fun createRoute(
        @Header("Authorization") bearer: String,
        @Body request: RouteRequest
    ): ApiResponse<Any>

    @PUT("api/routes/{id}")
    suspend fun updateRoute(
        @Header("Authorization") bearer: String,
        @Path("id") id: Int,
        @Body request: RouteRequest
    ): ApiResponse<Any>

    // Operators
    @POST("api/operators")
    suspend fun createOperator(
        @Header("Authorization") bearer: String,
        @Body request: OperatorRequest
    ): ApiResponse<Any>

    @PUT("api/operators/{id}")
    suspend fun updateOperator(
        @Header("Authorization") bearer: String,
        @Path("id") id: Int,
        @Body request: OperatorRequest
    ): ApiResponse<Any>

    @DELETE("api/operators/{id}")
    suspend fun deleteOperator(
        @Header("Authorization") bearer: String,
        @Path("id") id: Int
    ): ApiResponse<Any>
}
