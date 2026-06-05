package com.busapp.app

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.busapp.app.api.ApiResponse
import com.busapp.app.api.AuthData
import com.busapp.app.api.RegisterRequest
import com.busapp.app.api.RetrofitClient
import com.busapp.app.databinding.ActivityRegisterBinding
import com.busapp.app.utils.SessionManager
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class RegisterActivity : AppCompatActivity() {

    private lateinit var binding: ActivityRegisterBinding
    private lateinit var sessionManager: SessionManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityRegisterBinding.inflate(layoutInflater)
        setContentView(binding.root)

        sessionManager = SessionManager(this)

        binding.btnRegister.setOnClickListener {
            val fullName = binding.etFullName.text.toString()
            val email = binding.etEmail.text.toString()
            val phone = binding.etPhone.text.toString()
            val password = binding.etPassword.text.toString()

            if (fullName.isNotEmpty() && email.isNotEmpty() && phone.isNotEmpty() && password.isNotEmpty()) {
                register(fullName, email, phone, password)
            } else {
                Toast.makeText(this, "Vui lòng nhập đầy đủ thông tin", Toast.LENGTH_SHORT).show()
            }
        }

        binding.tvLoginLink.setOnClickListener {
            finish()
        }
    }

    private fun register(fullName: String, email: String, phone: String, password: String) {
        binding.progressBar.visibility = View.VISIBLE
        binding.btnRegister.isEnabled = false

        val request = RegisterRequest(email, password, fullName, phone)
        RetrofitClient.instance.register(request).enqueue(object : Callback<ApiResponse<AuthData>> {
            override fun onResponse(call: Call<ApiResponse<AuthData>>, response: Response<ApiResponse<AuthData>>) {
                binding.progressBar.visibility = View.GONE
                binding.btnRegister.isEnabled = true

                if (response.isSuccessful && response.body()?.success == true) {
                    val authData = response.body()?.data
                    if (authData != null) {
                        sessionManager.saveAuthToken(authData.token)
                        sessionManager.saveUserData(authData.fullName, authData.role)
                        
                        Toast.makeText(this@RegisterActivity, "Đăng ký thành công!", Toast.LENGTH_SHORT).show()
                        startActivity(Intent(this@RegisterActivity, UserDashboardActivity::class.java))
                        finishAffinity()
                    }
                } else {
                    Toast.makeText(this@RegisterActivity, response.body()?.message ?: "Đăng ký thất bại", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onFailure(call: Call<ApiResponse<AuthData>>, t: Throwable) {
                binding.progressBar.visibility = View.GONE
                binding.btnRegister.isEnabled = true
                Toast.makeText(this@RegisterActivity, "Lỗi: \${t.message}", Toast.LENGTH_SHORT).show()
            }
        })
    }
}
