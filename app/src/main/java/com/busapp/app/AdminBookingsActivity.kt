package com.busapp.app

import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.busapp.app.adapters.BookingAdapter
import com.busapp.app.api.*
import com.busapp.app.databinding.ActivityAdminBookingsBinding
import com.busapp.app.utils.SessionManager
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class AdminBookingsActivity : AppCompatActivity() {

    private lateinit var binding: ActivityAdminBookingsBinding
    private lateinit var adapter: BookingAdapter
    private lateinit var sessionManager: SessionManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAdminBookingsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        sessionManager = SessionManager(this)
        setupRecyclerView()
        fetchAllBookings()
    }

    private fun setupRecyclerView() {
        adapter = BookingAdapter(emptyList()) { booking ->
            Toast.makeText(this, "Vé của: ${booking.passengerName}", Toast.LENGTH_SHORT).show()
        }
        binding.rvAdminBookings.layoutManager = LinearLayoutManager(this)
        binding.rvAdminBookings.adapter = adapter
    }

    private fun fetchAllBookings() {
        val token = sessionManager.fetchAuthToken() ?: return
        binding.progressBar.visibility = View.VISIBLE
        
        RetrofitClient.instance.getAllBookingsAdmin("Bearer $token", 0, 50).enqueue(object : Callback<ApiResponse<BookingPageResponse>> {
            override fun onResponse(call: Call<ApiResponse<BookingPageResponse>>, response: Response<ApiResponse<BookingPageResponse>>) {
                binding.progressBar.visibility = View.GONE
                if (response.isSuccessful && response.body()?.success == true) {
                    val bookings = response.body()?.data?.content ?: emptyList()
                    adapter.updateData(bookings)
                } else {
                    Toast.makeText(this@AdminBookingsActivity, "Không thể tải danh sách đặt vé", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onFailure(call: Call<ApiResponse<BookingPageResponse>>, t: Throwable) {
                binding.progressBar.visibility = View.GONE
                Toast.makeText(this@AdminBookingsActivity, "Lỗi kết nối", Toast.LENGTH_SHORT).show()
            }
        })
    }
}
