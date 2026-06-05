package com.example.busadmin.ui.activity

import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.busadmin.databinding.ActivityTripsBinding
import com.example.busadmin.model.TripRequest
import com.example.busadmin.network.RetrofitClient
import com.example.busadmin.utils.SessionManager
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class TripsActivity : AppCompatActivity() {

    private lateinit var binding: ActivityTripsBinding
    private lateinit var sessionManager: SessionManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityTripsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        sessionManager = SessionManager(this)

        binding.tvTitle.text = "Quản lý Trips"

        binding.btnBack.setOnClickListener { finish() }

        binding.btnAddTrip.setOnClickListener {
            createTrip()
        }
    }

    private fun createTrip() {
        val operatorId = binding.etOperatorId.text.toString().toIntOrNull()
        val routeId = binding.etRouteId.text.toString().toIntOrNull()
        val vehicleTypeId = binding.etVehicleTypeId.text.toString().toIntOrNull()
        val departureDate = binding.etDepartureDate.text.toString().trim()
        val departureTime = binding.etDepartureTime.text.toString().trim()
        val price = binding.etPrice.text.toString().trim()
        val availableSeats = binding.etAvailableSeats.text.toString().toIntOrNull()

        if (operatorId == null || routeId == null || vehicleTypeId == null 
            || departureDate.isEmpty() || departureTime.isEmpty()) {
            Toast.makeText(this, "Vui lòng điền đầy đủ thông tin bắt buộc", Toast.LENGTH_SHORT).show()
            return
        }

        showLoading(true)

        val tripRequest = TripRequest(
            operatorId = operatorId,
            routeId = routeId,
            vehicleTypeId = vehicleTypeId,
            departureDate = departureDate,
            departureTime = departureTime,
            price = price,
            availableSeats = availableSeats,
            status = "ACTIVE"
        )

        CoroutineScope(Dispatchers.IO).launch {
            try {
                val token = sessionManager.getToken() ?: ""
                val bearer = "Bearer $token"
                val response = RetrofitClient.apiService.createTrip(bearer, tripRequest)

                withContext(Dispatchers.Main) {
                    showLoading(false)
                    if (response.success) {
                        Toast.makeText(this@TripsActivity, "Tạo chuyến thành công", Toast.LENGTH_SHORT).show()
                        clearForm()
                    } else {
                        Toast.makeText(this@TripsActivity, response.message ?: "Tạo thất bại", Toast.LENGTH_SHORT).show()
                    }
                }
            } catch (e: Exception) {
                withContext(Dispatchers.Main) {
                    showLoading(false)
                    Toast.makeText(this@TripsActivity, "Lỗi: ${e.localizedMessage}", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    private fun clearForm() {
        binding.etOperatorId.text?.clear()
        binding.etRouteId.text?.clear()
        binding.etVehicleTypeId.text?.clear()
        binding.etDepartureDate.text?.clear()
        binding.etDepartureTime.text?.clear()
        binding.etPrice.text?.clear()
        binding.etAvailableSeats.text?.clear()
    }

    private fun showLoading(isLoading: Boolean) {
        binding.progressBar.visibility = if (isLoading) View.VISIBLE else View.GONE
        binding.btnAddTrip.isEnabled = !isLoading
    }
}
