package com.busapp.app

import android.os.Bundle
import android.view.View
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.busapp.app.api.*
import com.busapp.app.databinding.ActivityAddRouteBinding
import com.busapp.app.utils.SessionManager
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class AddRouteActivity : AppCompatActivity() {

    private lateinit var binding: ActivityAddRouteBinding
    private lateinit var sessionManager: SessionManager
    private var provinces: List<Province> = emptyList()
    private var isEditMode = false
    private var routeId: Int = -1

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAddRouteBinding.inflate(layoutInflater)
        setContentView(binding.root)

        sessionManager = SessionManager(this)

        val route = intent.getSerializableExtra("ROUTE") as? RouteResponse
        if (route != null) {
            isEditMode = true
            routeId = route.id
        }

        fetchProvinces(route)

        binding.btnSaveRoute.setOnClickListener {
            saveRoute()
        }

        binding.btnBack.setOnClickListener { finish() }
    }

    private fun fetchProvinces(editingRoute: RouteResponse?) {
        binding.progressBar.visibility = View.VISIBLE
        RetrofitClient.instance.getProvinces().enqueue(object : Callback<ApiResponse<List<Province>>> {
            override fun onResponse(call: Call<ApiResponse<List<Province>>>, response: Response<ApiResponse<List<Province>>>) {
                binding.progressBar.visibility = View.GONE
                if (response.isSuccessful && response.body()?.success == true) {
                    provinces = response.body()?.data ?: emptyList()
                    val provinceNames = provinces.map { it.name }
                    val adapter = ArrayAdapter(this@AddRouteActivity, android.R.layout.simple_spinner_dropdown_item, provinceNames)
                    binding.spinnerDeparture.adapter = adapter
                    binding.spinnerDestination.adapter = adapter

                    if (isEditMode && editingRoute != null) {
                        fillData(editingRoute)
                    }
                }
            }
            override fun onFailure(call: Call<ApiResponse<List<Province>>>, t: Throwable) {
                binding.progressBar.visibility = View.GONE
            }
        })
    }

    private fun fillData(route: RouteResponse) {
        val depPos = provinces.indexOfFirst { it.id == route.departureProvinceId }
        val destPos = provinces.indexOfFirst { it.id == route.destinationProvinceId }
        
        binding.apply {
            if (depPos != -1) spinnerDeparture.setSelection(depPos)
            if (destPos != -1) spinnerDestination.setSelection(destPos)
            etDistance.setText(route.distanceKm.toString())
            etDuration.setText(route.durationHours.toString())
            cbIsPopular.isChecked = route.isPopular
            btnSaveRoute.text = "CẬP NHẬT TUYẾN ĐƯỜNG"
        }
    }

    private fun saveRoute() {
        val depIdx = binding.spinnerDeparture.selectedItemPosition
        val destIdx = binding.spinnerDestination.selectedItemPosition
        val distance = binding.etDistance.text.toString().toDoubleOrNull()
        val duration = binding.etDuration.text.toString().toDoubleOrNull()

        if (depIdx == -1 || destIdx == -1 || distance == null || duration == null) {
            Toast.makeText(this, "Vui lòng nhập đầy đủ thông tin", Toast.LENGTH_SHORT).show()
            return
        }

        val token = sessionManager.fetchAuthToken() ?: return
        binding.progressBar.visibility = View.VISIBLE
        binding.btnSaveRoute.isEnabled = false

        val request = RouteRequest(
            departureProvinceId = provinces[depIdx].id,
            destinationProvinceId = provinces[destIdx].id,
            distanceKm = distance,
            durationHours = duration,
            isPopular = binding.cbIsPopular.isChecked
        )

        val call = if (isEditMode) {
            RetrofitClient.instance.updateRoute("Bearer $token", routeId, request)
        } else {
            RetrofitClient.instance.createRoute("Bearer $token", request)
        }

        call.enqueue(object : Callback<ApiResponse<RouteResponse>> {
            override fun onResponse(call: Call<ApiResponse<RouteResponse>>, response: Response<ApiResponse<RouteResponse>>) {
                binding.progressBar.visibility = View.GONE
                if (response.isSuccessful && response.body()?.success == true) {
                    Toast.makeText(this@AddRouteActivity, "Thao tác thành công!", Toast.LENGTH_SHORT).show()
                    finish()
                } else {
                    binding.btnSaveRoute.isEnabled = true
                    Toast.makeText(this@AddRouteActivity, "Lỗi: ${response.body()?.message}", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onFailure(call: Call<ApiResponse<RouteResponse>>, t: Throwable) {
                binding.progressBar.visibility = View.GONE
                binding.btnSaveRoute.isEnabled = true
                Toast.makeText(this@AddRouteActivity, "Lỗi kết nối", Toast.LENGTH_SHORT).show()
            }
        })
    }
}
