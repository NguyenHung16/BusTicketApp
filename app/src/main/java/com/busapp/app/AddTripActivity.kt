package com.busapp.app

import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.os.Bundle
import android.view.View
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.busapp.app.api.*
import com.busapp.app.databinding.ActivityAddTripBinding
import com.busapp.app.utils.SessionManager
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.util.*
import java.text.SimpleDateFormat

class AddTripActivity : AppCompatActivity() {

    private lateinit var binding: ActivityAddTripBinding
    private lateinit var sessionManager: SessionManager

    private var operators: List<OperatorResponse> = emptyList()
    private var routes: List<RouteResponse> = emptyList()
    private var vehicleTypes: List<VehicleTypeResponse> = emptyList()

    private val calendar = Calendar.getInstance()
    private var isEditMode = false
    private var tripId: Int = -1

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAddTripBinding.inflate(layoutInflater)
        setContentView(binding.root)

        sessionManager = SessionManager(this)

        val trip = intent.getSerializableExtra("TRIP") as? TripResponse
        if (trip != null) {
            isEditMode = true
            tripId = trip.id
        }

        setupPickers()
        fetchFormData(trip)

        binding.btnSaveTrip.setOnClickListener {
            saveTrip()
        }
        
        binding.btnBack.setOnClickListener { finish() }
    }

    private fun setupPickers() {
        binding.etDepartureDate.setOnClickListener {
            val dateSetListener = DatePickerDialog.OnDateSetListener { _, year, month, dayOfMonth ->
                calendar.set(Calendar.YEAR, year)
                calendar.set(Calendar.MONTH, month)
                calendar.set(Calendar.DAY_OF_MONTH, dayOfMonth)
                val format = SimpleDateFormat("yyyy-MM-dd", Locale.US)
                binding.etDepartureDate.setText(format.format(calendar.time))
            }

            DatePickerDialog(
                this, dateSetListener,
                calendar.get(Calendar.YEAR),
                calendar.get(Calendar.MONTH),
                calendar.get(Calendar.DAY_OF_MONTH)
            ).show()
        }

        binding.etDepartureTime.setOnClickListener {
            val timeSetListener = TimePickerDialog.OnTimeSetListener { _, hourOfDay, minute ->
                val timeDisplay = String.format("%02d:%02d", hourOfDay, minute)
                binding.etDepartureTime.setText(timeDisplay)
            }

            TimePickerDialog(
                this, timeSetListener,
                calendar.get(Calendar.HOUR_OF_DAY),
                calendar.get(Calendar.MINUTE),
                true
            ).show()
        }
    }

    private fun fetchFormData(editingTrip: TripResponse?) {
        binding.progressBar.visibility = View.VISIBLE
        val token = sessionManager.fetchAuthToken() ?: return

        // Fetching all data and then filling if in edit mode
        RetrofitClient.instance.getOperators(0, 100).enqueue(object : Callback<ApiResponse<Map<String, Any>>> {
            override fun onResponse(call: Call<ApiResponse<Map<String, Any>>>, response: Response<ApiResponse<Map<String, Any>>>) {
                if (response.isSuccessful) {
                    // Simplified parsing for brevity, should use Gson as in OperatorsActivity for robustness
                    val data = response.body()?.data
                    val content = data?.get("content") as? List<Map<String, Any>>
                    operators = content?.map {
                        OperatorResponse(
                            id = (it["id"] as? Double)?.toInt() ?: 0,
                            name = it["name"] as? String ?: "",
                            phone = it["phone"] as? String ?: "",
                            email = it["email"] as? String ?: ""
                        )
                    } ?: emptyList()
                    binding.spinnerOperator.adapter = ArrayAdapter(this@AddTripActivity, android.R.layout.simple_spinner_dropdown_item, operators.map { it.name })
                    
                    if (isEditMode && editingTrip != null) {
                        val opPos = operators.indexOfFirst { it.name == editingTrip.operatorName }
                        if (opPos != -1) binding.spinnerOperator.setSelection(opPos)
                    }
                }
            }
            override fun onFailure(call: Call<ApiResponse<Map<String, Any>>>, t: Throwable) {}
        })

        RetrofitClient.instance.getRoutes().enqueue(object : Callback<ApiResponse<List<RouteResponse>>> {
            override fun onResponse(call: Call<ApiResponse<List<RouteResponse>>>, response: Response<ApiResponse<List<RouteResponse>>>) {
                if (response.isSuccessful) {
                    routes = response.body()?.data ?: emptyList()
                    binding.spinnerRoute.adapter = ArrayAdapter(this@AddTripActivity, android.R.layout.simple_spinner_dropdown_item, routes.map { "${it.departureProvince} -> ${it.destinationProvince}" })
                    
                    if (isEditMode && editingTrip != null) {
                        val routePos = routes.indexOfFirst { "${it.departureProvince} -> ${it.destinationProvince}" == "${editingTrip.departureProvince} -> ${editingTrip.destinationProvince}" }
                        if (routePos != -1) binding.spinnerRoute.setSelection(routePos)
                    }
                }
            }
            override fun onFailure(call: Call<ApiResponse<List<RouteResponse>>>, t: Throwable) {}
        })

        RetrofitClient.instance.getVehicleTypes().enqueue(object : Callback<ApiResponse<List<VehicleTypeResponse>>> {
            override fun onResponse(call: Call<ApiResponse<List<VehicleTypeResponse>>>, response: Response<ApiResponse<List<VehicleTypeResponse>>>) {
                binding.progressBar.visibility = View.GONE
                if (response.isSuccessful) {
                    vehicleTypes = response.body()?.data ?: emptyList()
                    binding.spinnerVehicleType.adapter = ArrayAdapter(this@AddTripActivity, android.R.layout.simple_spinner_dropdown_item, vehicleTypes.map { it.typeName })
                    
                    if (isEditMode && editingTrip != null) {
                        val vtPos = vehicleTypes.indexOfFirst { it.typeName == editingTrip.vehicleTypeName }
                        if (vtPos != -1) binding.spinnerVehicleType.setSelection(vtPos)
                        
                        // Fill other fields
                        binding.etDepartureDate.setText(editingTrip.departureDate)
                        binding.etDepartureTime.setText(editingTrip.departureTime.substring(0, 5))
                        binding.etPrice.setText(editingTrip.price.toLong().toString())
                        binding.btnSaveTrip.text = "CẬP NHẬT CHUYẾN XE"
                    }
                }
            }
            override fun onFailure(call: Call<ApiResponse<List<VehicleTypeResponse>>>, t: Throwable) {
                binding.progressBar.visibility = View.GONE
            }
        })
    }

    private fun saveTrip() {
        val opIdx = binding.spinnerOperator.selectedItemPosition
        val routeIdx = binding.spinnerRoute.selectedItemPosition
        val vtIdx = binding.spinnerVehicleType.selectedItemPosition
        val date = binding.etDepartureDate.text.toString()
        var time = binding.etDepartureTime.text.toString()
        val priceInput = binding.etPrice.text.toString()

        if (opIdx < 0 || routeIdx < 0 || vtIdx < 0 || date.isEmpty() || time.isEmpty() || priceInput.isEmpty()) {
            Toast.makeText(this, "Vui lòng nhập đầy đủ thông tin", Toast.LENGTH_SHORT).show()
            return
        }

        if (time.length == 5) time = "$time:00"

        val token = sessionManager.fetchAuthToken() ?: return
        binding.progressBar.visibility = View.VISIBLE
        binding.btnSaveTrip.isEnabled = false

        val request = TripRequest(
            operatorId = operators[opIdx].id,
            routeId = routes[routeIdx].id,
            vehicleTypeId = vehicleTypes[vtIdx].id,
            departureDate = date,
            departureTime = time,
            price = priceInput,
            availableSeats = vehicleTypes[vtIdx].seatCount
        )

        val call = if (isEditMode) {
            RetrofitClient.instance.updateTrip("Bearer $token", tripId, request)
        } else {
            RetrofitClient.instance.createTrip("Bearer $token", request)
        }

        call.enqueue(object : Callback<ApiResponse<TripResponse>> {
            override fun onResponse(call: Call<ApiResponse<TripResponse>>, response: Response<ApiResponse<TripResponse>>) {
                binding.progressBar.visibility = View.GONE
                if (response.isSuccessful) {
                    Toast.makeText(this@AddTripActivity, "Thao tác thành công!", Toast.LENGTH_SHORT).show()
                    finish()
                } else {
                    binding.btnSaveTrip.isEnabled = true
                    Toast.makeText(this@AddTripActivity, "Lỗi: ${response.message()}", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onFailure(call: Call<ApiResponse<TripResponse>>, t: Throwable) {
                binding.progressBar.visibility = View.GONE
                binding.btnSaveTrip.isEnabled = true
                Toast.makeText(this@AddTripActivity, "Lỗi kết nối", Toast.LENGTH_SHORT).show()
            }
        })
    }
}
