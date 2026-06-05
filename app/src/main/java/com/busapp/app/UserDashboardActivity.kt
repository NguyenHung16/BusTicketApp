package com.busapp.app

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.busapp.app.adapters.TripAdapter
import com.busapp.app.api.*
import com.busapp.app.databinding.ActivityUserDashboardBinding
import com.busapp.app.utils.SessionManager
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class UserDashboardActivity : AppCompatActivity() {

    private lateinit var binding: ActivityUserDashboardBinding
    private lateinit var sessionManager: SessionManager
    private lateinit var adapter: TripAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityUserDashboardBinding.inflate(layoutInflater)
        setContentView(binding.root)

        sessionManager = SessionManager(this)
        
        val userName = sessionManager.getUserName()
        binding.tvWelcomeUser.text = "Xin chào, $userName"

        setupRecyclerView()
        fetchAvailableTrips()

        binding.btnMyBookings.setOnClickListener {
            startActivity(Intent(this, MyBookingsActivity::class.java))
        }

        binding.btnProfile.setOnClickListener {
            startActivity(Intent(this, ProfileActivity::class.java))
        }

        binding.btnLogout.setOnClickListener {
            sessionManager.clearSession()
            startActivity(Intent(this, LoginActivity::class.java))
            finishAffinity()
        }
    }

    override fun onResume() {
        super.onResume()
        val userName = sessionManager.getUserName()
        binding.tvWelcomeUser.text = "Xin chào, $userName"
    }

    private fun setupRecyclerView() {
        // Truyền null cho onEdit và onDelete để ẩn 2 icon phía sau
        adapter = TripAdapter(
            trips = emptyList(),
            onEdit = null,
            onDelete = null,
            onTripClick = { trip ->
                val intent = Intent(this, SeatSelectionActivity::class.java)
                intent.putExtra("TRIP_ID", trip.id)
                intent.putExtra("OPERATOR_NAME", trip.operatorName)
                startActivity(intent)
            }
        )
        binding.rvUserTrips.layoutManager = LinearLayoutManager(this)
        binding.rvUserTrips.adapter = adapter
    }

    private fun fetchAvailableTrips() {
        binding.progressBar.visibility = View.VISIBLE
        RetrofitClient.instance.getAllTrips().enqueue(object : Callback<ApiResponse<List<TripResponse>>> {
            override fun onResponse(call: Call<ApiResponse<List<TripResponse>>>, response: Response<ApiResponse<List<TripResponse>>>) {
                binding.progressBar.visibility = View.GONE
                if (response.isSuccessful && response.body()?.success == true) {
                    val trips = response.body()?.data ?: emptyList()
                    adapter.updateData(trips)
                    if (trips.isEmpty()) {
                        Toast.makeText(this@UserDashboardActivity, "Hiện không có chuyến xe nào khả dụng", Toast.LENGTH_SHORT).show()
                    }
                } else {
                    Toast.makeText(this@UserDashboardActivity, "Không thể tải danh sách chuyến xe", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onFailure(call: Call<ApiResponse<List<TripResponse>>>, t: Throwable) {
                binding.progressBar.visibility = View.GONE
                Toast.makeText(this@UserDashboardActivity, "Lỗi kết nối", Toast.LENGTH_SHORT).show()
            }
        })
    }
}
