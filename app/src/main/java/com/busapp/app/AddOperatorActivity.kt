package com.busapp.app

import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.busapp.app.api.*
import com.busapp.app.databinding.ActivityAddOperatorBinding
import com.busapp.app.utils.SessionManager
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class AddOperatorActivity : AppCompatActivity() {

    private lateinit var binding: ActivityAddOperatorBinding
    private lateinit var sessionManager: SessionManager
    private var isEditMode = false
    private var operatorId: Int = -1

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAddOperatorBinding.inflate(layoutInflater)
        setContentView(binding.root)

        sessionManager = SessionManager(this)

        // Check if we are in Edit Mode
        val operator = intent.getSerializableExtra("OPERATOR") as? OperatorResponse
        if (operator != null) {
            isEditMode = true
            operatorId = operator.id
            fillData(operator)
        }

        binding.btnSaveOperator.setOnClickListener {
            saveOperator()
        }

        binding.btnBack.setOnClickListener { finish() }
    }

    private fun fillData(op: OperatorResponse) {
        binding.apply {
            etName.setText(op.name)
            etPhone.setText(op.phone)
            etEmail.setText(op.email)
            etDescription.setText(op.description)
            etAmenities.setText(op.amenities)
            etCancellationPolicy.setText(op.cancellationPolicy)
            btnSaveOperator.text = "CẬP NHẬT NHÀ XE"
        }
    }

    private fun saveOperator() {
        val name = binding.etName.text.toString().trim()
        val phone = binding.etPhone.text.toString().trim()
        val email = binding.etEmail.text.toString().trim()
        val description = binding.etDescription.text.toString().trim()
        val amenities = binding.etAmenities.text.toString().trim()
        val cancellationPolicy = binding.etCancellationPolicy.text.toString().trim()

        if (name.isEmpty() || phone.isEmpty() || email.isEmpty()) {
            Toast.makeText(this, "Vui lòng nhập tên, số điện thoại và email", Toast.LENGTH_SHORT).show()
            return
        }

        val token = sessionManager.fetchAuthToken() ?: return
        binding.progressBar.visibility = View.VISIBLE
        binding.btnSaveOperator.isEnabled = false

        val request = OperatorRequest(
            name = name,
            phone = phone,
            email = email,
            description = description,
            amenities = amenities,
            cancellationPolicy = cancellationPolicy,
            isActive = true
        )

        val call = if (isEditMode) {
            RetrofitClient.instance.updateOperator("Bearer $token", operatorId, request)
        } else {
            RetrofitClient.instance.createOperator("Bearer $token", request)
        }

        call.enqueue(object : Callback<ApiResponse<OperatorResponse>> {
            override fun onResponse(call: Call<ApiResponse<OperatorResponse>>, response: Response<ApiResponse<OperatorResponse>>) {
                binding.progressBar.visibility = View.GONE
                if (response.isSuccessful && response.body()?.success == true) {
                    val msg = if (isEditMode) "Cập nhật thành công!" else "Thêm nhà xe thành công!"
                    Toast.makeText(this@AddOperatorActivity, msg, Toast.LENGTH_SHORT).show()
                    finish()
                } else {
                    binding.btnSaveOperator.isEnabled = true
                    Toast.makeText(this@AddOperatorActivity, response.body()?.message ?: "Lỗi hệ thống", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onFailure(call: Call<ApiResponse<OperatorResponse>>, t: Throwable) {
                binding.progressBar.visibility = View.GONE
                binding.btnSaveOperator.isEnabled = true
                Toast.makeText(this@AddOperatorActivity, "Lỗi kết nối", Toast.LENGTH_SHORT).show()
            }
        })
    }
}
