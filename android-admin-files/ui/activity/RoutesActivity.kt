package com.example.busadmin.ui.activity

import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.busadmin.databinding.ActivityRoutesBinding
import com.example.busadmin.model.RouteRequest
import com.example.busadmin.network.RetrofitClient
import com.example.busadmin.utils.SessionManager
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class RoutesActivity : AppCompatActivity() {

    private lateinit var binding: ActivityRoutesBinding
    private lateinit var sessionManager: SessionManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityRoutesBinding.inflate(layoutInflater)
        setContentView(binding.root)

        sessionManager = SessionManager(this)

        binding.tvTitle.text = "Quản lý Routes"
        binding.btnBack.setOnClickListener { finish() }
        binding.btnAddRoute.setOnClickListener { createRoute() }
    }

    private fun createRoute() {
        val depProvinceId = binding.etDepProvinceId.text.toString().toIntOrNull()
        val destProvinceId = binding.etDestProvinceId.text.toString().toIntOrNull()

        if (depProvinceId == null || destProvinceId == null) {
            Toast.makeText(this, "Vui lòng điền Province ID", Toast.LENGTH_SHORT).show()
            return
        }

        showLoading(true)

        val routeRequest = RouteRequest(
            departureProvinceId = depProvinceId,
            destinationProvinceId = destProvinceId,
            distanceKm = binding.etDistanceKm.text.toString().toIntOrNull(),
            durationHours = binding.etDurationHours.text.toString().toFloatOrNull(),
            isPopular = binding.cbIsPopular.isChecked
        )

        CoroutineScope(Dispatchers.IO).launch {
            try {
                val token = sessionManager.getToken() ?: ""
                val bearer = "Bearer $token"
                val response = RetrofitClient.apiService.createRoute(bearer, routeRequest)

                withContext(Dispatchers.Main) {
                    showLoading(false)
                    if (response.success) {
                        Toast.makeText(this@RoutesActivity, "Tạo tuyến thành công", Toast.LENGTH_SHORT).show()
                        clearForm()
                    } else {
                        Toast.makeText(this@RoutesActivity, response.message ?: "Tạo thất bại", Toast.LENGTH_SHORT).show()
                    }
                }
            } catch (e: Exception) {
                withContext(Dispatchers.Main) {
                    showLoading(false)
                    Toast.makeText(this@RoutesActivity, "Lỗi: ${e.localizedMessage}", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    private fun clearForm() {
        binding.etDepProvinceId.text?.clear()
        binding.etDestProvinceId.text?.clear()
        binding.etDistanceKm.text?.clear()
        binding.etDurationHours.text?.clear()
        binding.cbIsPopular.isChecked = false
    }

    private fun showLoading(isLoading: Boolean) {
        binding.progressBar.visibility = if (isLoading) View.VISIBLE else View.GONE
        binding.btnAddRoute.isEnabled = !isLoading
    }
}
