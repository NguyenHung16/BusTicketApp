package com.busapp.app

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.GridLayoutManager
import com.busapp.app.adapters.SeatAdapter
import com.busapp.app.api.*
import com.busapp.app.databinding.ActivitySeatSelectionBinding
import com.busapp.app.utils.SessionManager
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class SeatSelectionActivity : AppCompatActivity() {

    private lateinit var binding: ActivitySeatSelectionBinding
    private lateinit var adapter: SeatAdapter
    private lateinit var sessionManager: SessionManager
    private var tripId: Int = -1

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySeatSelectionBinding.inflate(layoutInflater)
        setContentView(binding.root)

        sessionManager = SessionManager(this)
        tripId = intent.getIntExtra("TRIP_ID", -1)
        val operatorName = intent.getStringExtra("OPERATOR_NAME") ?: "Chuyến xe"

        binding.tvTripTitle.text = "Chọn ghế: $operatorName"

        setupRecyclerView()
        fetchSeats()

        binding.btnContinue.setOnClickListener {
            val selectedSeat = adapter.getSelectedSeat()
            if (selectedSeat != null) {
                lockAndProceed(selectedSeat)
            } else {
                Toast.makeText(this, "Vui lòng chọn một ghế", Toast.LENGTH_SHORT).show()
            }
        }
        
        binding.btnBack.setOnClickListener { finish() }
    }

    private fun setupRecyclerView() {
        adapter = SeatAdapter(emptyList()) { seat ->
            binding.btnContinue.isEnabled = true
        }
        binding.rvSeats.layoutManager = GridLayoutManager(this, 4)
        binding.rvSeats.adapter = adapter
    }

    private fun fetchSeats() {
        binding.progressBar.visibility = View.VISIBLE
        RetrofitClient.instance.getSeats(tripId).enqueue(object : Callback<ApiResponse<SeatMapResponse>> {
            override fun onResponse(call: Call<ApiResponse<SeatMapResponse>>, response: Response<ApiResponse<SeatMapResponse>>) {
                binding.progressBar.visibility = View.GONE
                if (response.isSuccessful && response.body()?.success == true) {
                    val seats = response.body()?.data?.seats ?: emptyList()
                    adapter.updateData(seats)
                }
            }
            override fun onFailure(call: Call<ApiResponse<SeatMapResponse>>, t: Throwable) {
                binding.progressBar.visibility = View.GONE
                Toast.makeText(this@SeatSelectionActivity, "Lỗi tải sơ đồ ghế", Toast.LENGTH_SHORT).show()
            }
        })
    }

    private fun lockAndProceed(seat: SeatResponse) {
        val token = sessionManager.fetchAuthToken()
        if (token == null) {
            Toast.makeText(this, "Vui lòng đăng nhập", Toast.LENGTH_SHORT).show()
            return
        }

        binding.progressBar.visibility = View.VISIBLE
        binding.btnContinue.isEnabled = false

        RetrofitClient.instance.lockSeat("Bearer $token", tripId, seat.seatCode).enqueue(object : Callback<ApiResponse<Any>> {
            override fun onResponse(call: Call<ApiResponse<Any>>, response: Response<ApiResponse<Any>>) {
                binding.progressBar.visibility = View.GONE
                if (response.isSuccessful && response.body()?.success == true) {
                    val intent = Intent(this@SeatSelectionActivity, BookingConfirmActivity::class.java)
                    intent.putExtra("TRIP_ID", tripId)
                    intent.putExtra("SEAT_ID", seat.id) // TRUYỀN THÊM SEAT_ID
                    intent.putExtra("SEAT_CODE", seat.seatCode)
                    startActivity(intent)
                } else {
                    binding.btnContinue.isEnabled = true
                    Toast.makeText(this@SeatSelectionActivity, "Ghế đã bị người khác chọn hoặc khóa", Toast.LENGTH_SHORT).show()
                    fetchSeats()
                }
            }
            override fun onFailure(call: Call<ApiResponse<Any>>, t: Throwable) {
                binding.progressBar.visibility = View.GONE
                binding.btnContinue.isEnabled = true
                Toast.makeText(this@SeatSelectionActivity, "Lỗi kết nối", Toast.LENGTH_SHORT).show()
            }
        })
    }
}
