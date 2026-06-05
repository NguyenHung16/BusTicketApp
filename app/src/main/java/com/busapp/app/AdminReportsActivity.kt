package com.busapp.app

import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.busapp.app.api.ApiResponse
import com.busapp.app.api.RetrofitClient
import com.busapp.app.databinding.ActivityAdminReportsBinding
import com.busapp.app.utils.SessionManager
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.text.NumberFormat
import java.util.*

class AdminReportsActivity : AppCompatActivity() {

    private lateinit var binding: ActivityAdminReportsBinding
    private lateinit var sessionManager: SessionManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAdminReportsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        sessionManager = SessionManager(this)
        
        fetchDashboardStats()

        binding.btnRefresh.setOnClickListener {
            fetchDashboardStats()
        }
    }

    private fun fetchDashboardStats() {
        val token = sessionManager.fetchAuthToken() ?: return
        binding.progressBar.visibility = View.VISIBLE

        RetrofitClient.instance.getAdminDashboard("Bearer $token").enqueue(object : Callback<ApiResponse<Map<String, Any>>> {
            override fun onResponse(call: Call<ApiResponse<Map<String, Any>>>, response: Response<ApiResponse<Map<String, Any>>>) {
                binding.progressBar.visibility = View.GONE
                if (response.isSuccessful && response.body()?.success == true) {
                    val data = response.body()?.data
                    data?.let {
                        // Trích xuất dữ liệu từ Map trả về của Backend
                        val revenue = (it["totalRevenue"] as? Double) ?: 0.0
                        val totalBookings = (it["totalBookings"] as? Double)?.toInt() ?: 0
                        val paidBookings = (it["paidBookings"] as? Double)?.toInt() ?: 0

                        val currencyFormat = NumberFormat.getCurrencyInstance(Locale("vi", "VN"))
                        binding.tvTotalRevenue.text = currencyFormat.format(revenue)
                        binding.tvTotalBookings.text = totalBookings.toString()
                        binding.tvPaidBookings.text = paidBookings.toString()
                    }
                } else {
                    Toast.makeText(this@AdminReportsActivity, "Không thể tải báo cáo", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onFailure(call: Call<ApiResponse<Map<String, Any>>>, t: Throwable) {
                binding.progressBar.visibility = View.GONE
                Toast.makeText(this@AdminReportsActivity, "Lỗi kết nối", Toast.LENGTH_SHORT).show()
            }
        })
    }
}
