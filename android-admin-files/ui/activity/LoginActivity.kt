package com.example.busadmin.ui.activity

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import com.example.busadmin.databinding.ActivityLoginBinding
import com.example.busadmin.network.RetrofitClient
import com.example.busadmin.utils.SessionManager
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class LoginActivity : AppCompatActivity() {

    private lateinit var binding: ActivityLoginBinding
    private lateinit var sessionManager: SessionManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

        sessionManager = SessionManager(this)

        // Kiểm tra nếu đã login trước đó
        if (sessionManager.getToken() != null) {
            navigateToDashboard()
            return
        }

        setupClickListeners()
    }

    private fun setupClickListeners() {
        binding.btnLogin.setOnClickListener {
            val email = binding.etEmail.text.toString().trim()
            val password = binding.etPassword.text.toString().trim()

            if (validateInput(email, password)) {
                login(email, password)
            }
        }
    }

    private fun validateInput(email: String, password: String): Boolean {
        return when {
            email.isEmpty() -> {
                binding.tvStatus.text = "Email không được để trống"
                false
            }
            password.isEmpty() -> {
                binding.tvStatus.text = "Mật khẩu không được để trống"
                false
            }
            !android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches() -> {
                binding.tvStatus.text = "Email không hợp lệ"
                false
            }
            else -> true
        }
    }

    private fun login(email: String, password: String) {
        showLoading(true)
        binding.tvStatus.text = ""

        CoroutineScope(Dispatchers.IO).launch {
            try {
                val response = RetrofitClient.apiService.login(
                    mapOf(
                        "email" to email,
                        "password" to password
                    )
                )

                withContext(Dispatchers.Main) {
                    showLoading(false)

                    if (response.success && response.data != null) {
                        val token = response.data.token
                        sessionManager.saveToken(token)
                        sessionManager.saveUserInfo(response.data)

                        navigateToDashboard()
                    } else {
                        binding.tvStatus.text = response.message ?: "Đăng nhập thất bại"
                    }
                }
            } catch (e: Exception) {
                withContext(Dispatchers.Main) {
                    showLoading(false)
                    binding.tvStatus.text = "Lỗi: ${e.localizedMessage}"
                }
            }
        }
    }

    private fun showLoading(isLoading: Boolean) {
        if (isLoading) {
            binding.progressBar.visibility = View.VISIBLE
            binding.btnLogin.isEnabled = false
        } else {
            binding.progressBar.visibility = View.GONE
            binding.btnLogin.isEnabled = true
        }
    }

    private fun navigateToDashboard() {
        val intent = Intent(this, DashboardActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        startActivity(intent)
        finish()
    }
}
