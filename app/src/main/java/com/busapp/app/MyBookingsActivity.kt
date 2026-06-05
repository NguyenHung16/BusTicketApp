package com.busapp.app

import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.busapp.app.adapters.BookingAdapter
import com.busapp.app.api.ApiResponse
import com.busapp.app.api.BookingPageResponse
import com.busapp.app.api.BookingResponse
import com.busapp.app.api.RetrofitClient
import com.busapp.app.databinding.ActivityMyBookingsBinding
import com.busapp.app.utils.SessionManager
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class MyBookingsActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMyBookingsBinding
    private lateinit var adapter: BookingAdapter
    private lateinit var sessionManager: SessionManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMyBookingsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        sessionManager = SessionManager(this)
        setupRecyclerView()
        fetchMyBookings()
    }

    private fun setupRecyclerView() {
        adapter = BookingAdapter(emptyList()) { booking ->
            // Khi click vào vé, có thể xem chi tiết hoặc hủy vé
            Toast.makeText(this, "Mã vé: ${booking.bookingCode}", Toast.LENGTH_SHORT).show()
        }
        binding.rvBookings.layoutManager = LinearLayoutManager(this)
        binding.rvBookings.adapter = adapter
    }

    private fun fetchMyBookings() {
        val token = sessionManager.fetchAuthToken()
        if (token == null) {
            Toast.makeText(this, "Vui lòng đăng nhập lại", Toast.LENGTH_SHORT).show()
            return
        }

        binding.progressBar.visibility = View.VISIBLE
        // Cập nhật để nhận BookingPageResponse thay vì List
        RetrofitClient.instance.getMyBookings("Bearer $token").enqueue(object : Callback<ApiResponse<BookingPageResponse>> {
            override fun onResponse(
                call: Call<ApiResponse<BookingPageResponse>>,
                response: Response<ApiResponse<BookingPageResponse>>
            ) {
                binding.progressBar.visibility = View.GONE
                if (response.isSuccessful && response.body()?.success == true) {
                    // Lấy danh sách từ trường content của PageResponse
                    val bookings = response.body()?.data?.content ?: emptyList()
                    adapter.updateData(bookings)
                    
                    if (bookings.isEmpty()) {
                        binding.tvNoData.visibility = View.VISIBLE
                        binding.tvNoData.text = "Bạn chưa có vé nào"
                    } else {
                        binding.tvNoData.visibility = View.GONE
                    }
                } else {
                    Toast.makeText(this@MyBookingsActivity, "Không thể tải danh sách vé", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onFailure(call: Call<ApiResponse<BookingPageResponse>>, t: Throwable) {
                binding.progressBar.visibility = View.GONE
                Toast.makeText(this@MyBookingsActivity, "Lỗi kết nối: ${t.message}", Toast.LENGTH_SHORT).show()
            }
        })
    }
}
