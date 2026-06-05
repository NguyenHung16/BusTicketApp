package com.example.busadmin.ui.activity

import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.busadmin.databinding.ActivityOperatorsBinding
import com.example.busadmin.model.OperatorRequest
import com.example.busadmin.network.RetrofitClient
import com.example.busadmin.utils.SessionManager
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class OperatorsActivity : AppCompatActivity() {

    private lateinit var binding: ActivityOperatorsBinding
    private lateinit var sessionManager: SessionManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityOperatorsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        sessionManager = SessionManager(this)

        binding.tvTitle.text = "Quản lý Operators"
        binding.btnBack.setOnClickListener { finish() }
        binding.btnAddOperator.setOnClickListener { createOperator() }
    }

    private fun createOperator() {
        val name = binding.etOperatorName.text.toString().trim()
        val phone = binding.etPhone.text.toString().trim()
        val email = binding.etEmail.text.toString().trim()

        if (name.isEmpty() || phone.isEmpty() || email.isEmpty()) {
            Toast.makeText(this, "Vui lòng điền đầy đủ thông tin bắt buộc", Toast.LENGTH_SHORT).show()
            return
        }

        showLoading(true)

        val operatorRequest = OperatorRequest(
            name = name,
            phone = phone,
            email = email,
            description = binding.etDescription.text.toString().trim(),
            amenities = binding.etAmenities.text.toString().trim(),
            cancellationPolicy = binding.etCancellationPolicy.text.toString().trim(),
            isActive = binding.cbIsActive.isChecked
        )

        CoroutineScope(Dispatchers.IO).launch {
            try {
                val token = sessionManager.getToken() ?: ""
                val bearer = "Bearer $token"
                val response = RetrofitClient.apiService.createOperator(bearer, operatorRequest)

                withContext(Dispatchers.Main) {
                    showLoading(false)
                    if (response.success) {
                        Toast.makeText(this@OperatorsActivity, "Tạo nhà xe thành công", Toast.LENGTH_SHORT).show()
                        clearForm()
                    } else {
                        Toast.makeText(this@OperatorsActivity, response.message ?: "Tạo thất bại", Toast.LENGTH_SHORT).show()
                    }
                }
            } catch (e: Exception) {
                withContext(Dispatchers.Main) {
                    showLoading(false)
                    Toast.makeText(this@OperatorsActivity, "Lỗi: ${e.localizedMessage}", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    private fun clearForm() {
        binding.etOperatorName.text?.clear()
        binding.etPhone.text?.clear()
        binding.etEmail.text?.clear()
        binding.etDescription.text?.clear()
        binding.etAmenities.text?.clear()
        binding.etCancellationPolicy.text?.clear()
        binding.cbIsActive.isChecked = false
    }

    private fun showLoading(isLoading: Boolean) {
        binding.progressBar.visibility = if (isLoading) View.VISIBLE else View.GONE
        binding.btnAddOperator.isEnabled = !isLoading
    }
}
