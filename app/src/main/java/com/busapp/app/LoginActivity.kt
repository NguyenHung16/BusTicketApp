package com.busapp.app

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import com.busapp.app.api.ApiResponse
import com.busapp.app.api.AuthData
import com.busapp.app.api.LoginRequest
import com.busapp.app.api.RetrofitClient
import com.busapp.app.databinding.ActivityLoginBinding
import com.busapp.app.utils.SessionManager
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class LoginActivity : AppCompatActivity() {

    private lateinit var binding: ActivityLoginBinding
    private lateinit var sessionManager: SessionManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

        sessionManager = SessionManager(this)

        if (sessionManager.fetchAuthToken() != null) {
            navigateByRole(sessionManager.getUserRole())
        }

        binding.btnLogin.setOnClickListener {
            val email = binding.etEmail.text.toString()
            val password = binding.etPassword.text.toString()

            if (email.isNotEmpty() && password.isNotEmpty()) {
                login(email, password)
            } else {
                binding.tvStatus.text = "Vui lòng nhập đầy đủ email và mật khẩu"
            }
        }

        binding.tvRegisterLink.setOnClickListener {
            startActivity(Intent(this, RegisterActivity::class.java))
        }
    }

    private fun login(email: String, password: String) {
        binding.progressBar.visibility = View.VISIBLE
        binding.btnLogin.isEnabled = false
        binding.tvStatus.text = ""

        val request = LoginRequest(email, password)
        RetrofitClient.instance.login(request).enqueue(object : Callback<ApiResponse<AuthData>> {
            override fun onResponse(call: Call<ApiResponse<AuthData>>, response: Response<ApiResponse<AuthData>>) {
                binding.progressBar.visibility = View.GONE
                binding.btnLogin.isEnabled = true

                if (response.isSuccessful && response.body()?.success == true) {
                    val authData = response.body()?.data
                    if (authData != null) {
                        sessionManager.saveAuthToken(authData.token)
                        sessionManager.saveUserData(authData.fullName, authData.role)
                        navigateByRole(authData.role)
                    }
                } else {
                    val errorMsg = response.body()?.message ?: "Đăng nhập thất bại"
                    binding.tvStatus.text = errorMsg
                }
            }

            override fun onFailure(call: Call<ApiResponse<AuthData>>, t: Throwable) {
                binding.progressBar.visibility = View.GONE
                binding.btnLogin.isEnabled = true
                binding.tvStatus.text = "Lỗi kết nối: \${t.message}"
            }
        })
    }

    private fun navigateByRole(role: String?) {
        val intent = if (role == "ADMIN" || role == "OPERATOR") {
            Intent(this, DashboardActivity::class.java)
        } else {
            Intent(this, UserDashboardActivity::class.java)
        }
        startActivity(intent)
        finish()
    }
}
