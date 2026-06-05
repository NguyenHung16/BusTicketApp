package com.busapp.app

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.busapp.app.adapters.TripAdapter
import com.busapp.app.api.*
import com.busapp.app.databinding.ActivitySearchTripsBinding
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class SearchTripsActivity : AppCompatActivity() {

    private lateinit var binding: ActivitySearchTripsBinding
    private lateinit var adapter: TripAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySearchTripsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupRecyclerView()

        binding.btnSearch.setOnClickListener {
            val dep = binding.etDeparture.text.toString()
            val dest = binding.etDestination.text.toString()
            val date = binding.etDate.text.toString()

            if (dep.isNotEmpty() && dest.isNotEmpty() && date.isNotEmpty()) {
                searchTrips(dep, dest, date)
            } else {
                Toast.makeText(this, "Vui lòng nhập đầy đủ thông tin", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun setupRecyclerView() {
        // CẬP NHẬT: SearchTripsActivity là dành cho người dùng, không cho phép Sửa/Xóa
        adapter = TripAdapter(
            trips = emptyList(),
            onEdit = { /* Không làm gì */ },
            onDelete = { /* Không làm gì */ },
            onTripClick = { trip ->
                val intent = Intent(this, SeatSelectionActivity::class.java)
                intent.putExtra("TRIP_ID", trip.id)
                intent.putExtra("OPERATOR_NAME", trip.operatorName)
                startActivity(intent)
            }
        )
        binding.rvTrips.layoutManager = LinearLayoutManager(this)
        binding.rvTrips.adapter = adapter
    }

    private fun searchTrips(dep: String, dest: String, date: String) {
        binding.progressBar.visibility = View.VISIBLE
        binding.btnSearch.isEnabled = false

        RetrofitClient.instance.searchTrips(dep, dest, date).enqueue(object : Callback<ApiResponse<TripPageResponse>> {
            override fun onResponse(call: Call<ApiResponse<TripPageResponse>>, response: Response<ApiResponse<TripPageResponse>>) {
                binding.progressBar.visibility = View.GONE
                binding.btnSearch.isEnabled = true

                if (response.isSuccessful && response.body()?.success == true) {
                    val trips = response.body()?.data?.content ?: emptyList()
                    adapter.updateData(trips)
                    if (trips.isEmpty()) {
                        Toast.makeText(this@SearchTripsActivity, "Không tìm thấy chuyến xe nào", Toast.LENGTH_SHORT).show()
                    }
                } else {
                    Toast.makeText(this@SearchTripsActivity, response.body()?.message ?: "Lỗi tìm kiếm", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onFailure(call: Call<ApiResponse<TripPageResponse>>, t: Throwable) {
                binding.progressBar.visibility = View.GONE
                binding.btnSearch.isEnabled = true
                Toast.makeText(this@SearchTripsActivity, "Lỗi kết nối: ${t.message}", Toast.LENGTH_SHORT).show()
            }
        })
    }
}
