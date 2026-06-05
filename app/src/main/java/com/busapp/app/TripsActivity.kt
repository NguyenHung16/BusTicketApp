package com.busapp.app

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.busapp.app.adapters.TripAdapter
import com.busapp.app.api.*
import com.busapp.app.databinding.ActivityTripsBinding
import com.busapp.app.utils.SessionManager
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class TripsActivity : AppCompatActivity() {

    private lateinit var binding: ActivityTripsBinding
    private lateinit var adapter: TripAdapter
    private lateinit var sessionManager: SessionManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityTripsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        sessionManager = SessionManager(this)
        setupRecyclerView()
        fetchTrips()

        binding.fabAddTrip.setOnClickListener {
            startActivity(Intent(this, AddTripActivity::class.java))
        }
        
        binding.btnBack.setOnClickListener { finish() }
    }

    override fun onResume() {
        super.onResume()
        fetchTrips()
    }

    private fun setupRecyclerView() {
        adapter = TripAdapter(
            trips = emptyList(),
            onEdit = { trip ->
                val intent = Intent(this, AddTripActivity::class.java)
                intent.putExtra("TRIP", trip)
                startActivity(intent)
            },
            onDelete = { trip ->
                showDeleteConfirmation(trip)
            },
            onTripClick = { trip ->
                Toast.makeText(this, trip.operatorName, Toast.LENGTH_SHORT).show()
            }
        )
        binding.rvTrips.layoutManager = LinearLayoutManager(this)
        binding.rvTrips.adapter = adapter
    }

    private fun showDeleteConfirmation(trip: TripResponse) {
        AlertDialog.Builder(this)
            .setTitle("Hủy chuyến xe")
            .setMessage("Bạn có chắc chắn muốn hủy chuyến xe của ${trip.operatorName}?")
            .setPositiveButton("Hủy chuyến") { _, _ ->
                deleteTrip(trip.id)
            }
            .setNegativeButton("Quay lại", null)
            .show()
    }

    private fun deleteTrip(id: Int) {
        val token = sessionManager.fetchAuthToken() ?: return
        binding.progressBar.visibility = View.VISIBLE
        
        RetrofitClient.instance.deleteTrip("Bearer $token", id).enqueue(object : Callback<ApiResponse<Void>> {
            override fun onResponse(call: Call<ApiResponse<Void>>, response: Response<ApiResponse<Void>>) {
                binding.progressBar.visibility = View.GONE
                if (response.isSuccessful) {
                    Toast.makeText(this@TripsActivity, "Đã hủy chuyến xe", Toast.LENGTH_SHORT).show()
                    fetchTrips()
                } else {
                    Toast.makeText(this@TripsActivity, "Không thể hủy chuyến xe", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onFailure(call: Call<ApiResponse<Void>>, t: Throwable) {
                binding.progressBar.visibility = View.GONE
                Toast.makeText(this@TripsActivity, "Lỗi kết nối", Toast.LENGTH_SHORT).show()
            }
        })
    }

    private fun fetchTrips() {
        binding.progressBar.visibility = View.VISIBLE
        RetrofitClient.instance.getAllTrips().enqueue(object : Callback<ApiResponse<List<TripResponse>>> {
            override fun onResponse(call: Call<ApiResponse<List<TripResponse>>>, response: Response<ApiResponse<List<TripResponse>>>) {
                binding.progressBar.visibility = View.GONE
                if (response.isSuccessful && response.body()?.success == true) {
                    val trips = response.body()?.data ?: emptyList()
                    adapter.updateData(trips)
                }
            }

            override fun onFailure(call: Call<ApiResponse<List<TripResponse>>>, t: Throwable) {
                binding.progressBar.visibility = View.GONE
            }
        })
    }
}
