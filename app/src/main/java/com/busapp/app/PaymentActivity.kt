package com.busapp.app

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.busapp.app.api.ApiResponse
import com.busapp.app.api.RetrofitClient
import com.busapp.app.databinding.ActivityPaymentBinding
import com.busapp.app.utils.SessionManager
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class PaymentActivity : AppCompatActivity() {

    private lateinit var binding: ActivityPaymentBinding
    private lateinit var sessionManager: SessionManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityPaymentBinding.inflate(layoutInflater)
        setContentView(binding.root)

        sessionManager = SessionManager(this)
        val bookingCode = intent.getStringExtra("BOOKING_CODE") ?: ""
        val amount = intent.getDoubleExtra("AMOUNT", 0.0)

        binding.tvPaymentInfo.text = "Thanh toán cho mã vé: $bookingCode\nSố tiền: $amount VNĐ"

        binding.btnPaySuccess.setOnClickListener {
            // Sửa từ "completed" thành "paid" để khớp với Enum của Backend
            updatePayment(bookingCode, "paid")
        }

        binding.btnPayFail.setOnClickListener {
            Toast.makeText(this, "Giao dịch đã bị hủy", Toast.LENGTH_SHORT).show()
            finish()
        }
    }

    private fun updatePayment(code: String, status: String) {
        val token = sessionManager.fetchAuthToken() ?: return
        
        RetrofitClient.instance.updatePaymentStatus("Bearer $token", code, status).enqueue(object : Callback<ApiResponse<Any>> {
            override fun onResponse(call: Call<ApiResponse<Any>>, response: Response<ApiResponse<Any>>) {
                if (response.isSuccessful) {
                    Toast.makeText(this@PaymentActivity, "Thanh toán thành công!", Toast.LENGTH_SHORT).show()
                    startActivity(Intent(this@PaymentActivity, MyBookingsActivity::class.java))
                    finishAffinity()
                } else {
                    Toast.makeText(this@PaymentActivity, "Lỗi cập nhật trạng thái thanh toán", Toast.LENGTH_SHORT).show()
                }
            }
            override fun onFailure(call: Call<ApiResponse<Any>>, t: Throwable) {
                Toast.makeText(this@PaymentActivity, "Lỗi kết nối", Toast.LENGTH_SHORT).show()
            }
        })
    }
}
