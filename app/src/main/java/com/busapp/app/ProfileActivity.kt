package com.busapp.app

import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.busapp.app.api.*
import com.busapp.app.databinding.ActivityProfileBinding
import com.busapp.app.utils.SessionManager
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class ProfileActivity : AppCompatActivity() {

    private lateinit var binding: ActivityProfileBinding
    private lateinit var sessionManager: SessionManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityProfileBinding.inflate(layoutInflater)
        setContentView(binding.root)

        sessionManager = SessionManager(this)
        
        fetchUserProfile()

        binding.btnUpdateProfile.setOnClickListener {
            updateProfile()
        }
    }

    private fun fetchUserProfile() {
        val token = sessionManager.fetchAuthToken() ?: return
        binding.progressBar.visibility = View.VISIBLE
        
        RetrofitClient.instance.getMe("Bearer $token").enqueue(object : Callback<ApiResponse<UserResponse>> {
            override fun onResponse(call: Call<ApiResponse<UserResponse>>, response: Response<ApiResponse<UserResponse>>) {
                binding.progressBar.visibility = View.GONE
                if (response.isSuccessful && response.body()?.success == true) {
                    val user = response.body()?.data
                    user?.let {
                        binding.etFullName.setText(it.fullName)
                        binding.etEmail.setText(it.email)
                        binding.etPhone.setText(it.phone ?: "")
                    }
                }
            }
            override fun onFailure(call: Call<ApiResponse<UserResponse>>, t: Throwable) {
                binding.progressBar.visibility = View.GONE
                Toast.makeText(this@ProfileActivity, "Lỗi tải thông tin", Toast.LENGTH_SHORT).show()
            }
        })
    }

    private fun updateProfile() {
        val fullName = binding.etFullName.text.toString().trim()
        val phone = binding.etPhone.text.toString().trim()
        val email = binding.etEmail.text.toString().trim()

        if (fullName.isEmpty() || phone.isEmpty()) {
            Toast.makeText(this, "Vui lòng nhập đầy đủ thông tin", Toast.LENGTH_SHORT).show()
            return
        }

        val token = sessionManager.fetchAuthToken() ?: return
        val request = UpdateProfileRequest(fullName, phone, email)

        binding.progressBar.visibility = View.VISIBLE
        RetrofitClient.instance.updateProfile("Bearer $token", request).enqueue(object : Callback<ApiResponse<UserResponse>> {
            override fun onResponse(call: Call<ApiResponse<UserResponse>>, response: Response<ApiResponse<UserResponse>>) {
                binding.progressBar.visibility = View.GONE
                if (response.isSuccessful && response.body()?.success == true) {
                    Toast.makeText(this@ProfileActivity, "Cập nhật thành công!", Toast.LENGTH_SHORT).show()
                    // Cập nhật lại tên trong session nếu cần
                    sessionManager.saveAuthToken(token) // Giả sử hàm này lưu cả profile hoặc chỉ cần reload dashboard
                    finish()
                } else {
                    Toast.makeText(this@ProfileActivity, "Cập nhật thất bại", Toast.LENGTH_SHORT).show()
                }
            }
            override fun onFailure(call: Call<ApiResponse<UserResponse>>, t: Throwable) {
                binding.progressBar.visibility = View.GONE
                Toast.makeText(this@ProfileActivity, "Lỗi kết nối", Toast.LENGTH_SHORT).show()
            }
        })
    }
}
