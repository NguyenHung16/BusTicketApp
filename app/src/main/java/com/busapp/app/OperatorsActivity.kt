package com.busapp.app

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.busapp.app.adapters.OperatorAdapter
import com.busapp.app.api.*
import com.busapp.app.databinding.ActivityOperatorsBinding
import com.busapp.app.utils.SessionManager
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class OperatorsActivity : AppCompatActivity() {

    private lateinit var binding: ActivityOperatorsBinding
    private lateinit var adapter: OperatorAdapter
    private lateinit var sessionManager: SessionManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityOperatorsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        sessionManager = SessionManager(this)
        setupRecyclerView()
        fetchOperators()

        binding.fabAddOperator.setOnClickListener {
            startActivity(Intent(this, AddOperatorActivity::class.java))
        }
        
        binding.btnBack.setOnClickListener { finish() }
    }

    override fun onResume() {
        super.onResume()
        fetchOperators()
    }

    private fun setupRecyclerView() {
        adapter = OperatorAdapter(
            operators = emptyList(),
            onEdit = { op ->
                val intent = Intent(this, AddOperatorActivity::class.java)
                intent.putExtra("OPERATOR", op)
                startActivity(intent)
            },
            onDelete = { op ->
                showDeleteConfirmation(op)
            },
            onClick = { op ->
                Toast.makeText(this, op.name, Toast.LENGTH_SHORT).show()
            }
        )
        binding.rvOperators.layoutManager = LinearLayoutManager(this)
        binding.rvOperators.adapter = adapter
    }

    private fun showDeleteConfirmation(op: OperatorResponse) {
        AlertDialog.Builder(this)
            .setTitle("Xóa nhà xe")
            .setMessage("Bạn có chắc chắn muốn xóa nhà xe ${op.name}?")
            .setPositiveButton("Xóa") { _, _ ->
                deleteOperator(op.id)
            }
            .setNegativeButton("Hủy", null)
            .show()
    }

    private fun deleteOperator(id: Int) {
        val token = sessionManager.fetchAuthToken() ?: return
        binding.progressBar.visibility = View.VISIBLE
        
        RetrofitClient.instance.deleteOperator("Bearer $token", id).enqueue(object : Callback<ApiResponse<Void>> {
            override fun onResponse(call: Call<ApiResponse<Void>>, response: Response<ApiResponse<Void>>) {
                binding.progressBar.visibility = View.GONE
                if (response.isSuccessful) {
                    Toast.makeText(this@OperatorsActivity, "Đã xóa nhà xe", Toast.LENGTH_SHORT).show()
                    fetchOperators()
                } else {
                    Toast.makeText(this@OperatorsActivity, "Không thể xóa nhà xe", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onFailure(call: Call<ApiResponse<Void>>, t: Throwable) {
                binding.progressBar.visibility = View.GONE
                Toast.makeText(this@OperatorsActivity, "Lỗi kết nối", Toast.LENGTH_SHORT).show()
            }
        })
    }

    private fun fetchOperators() {
        binding.progressBar.visibility = View.VISIBLE
        RetrofitClient.instance.getOperators(0, 100).enqueue(object : Callback<ApiResponse<Map<String, Any>>> {
            override fun onResponse(call: Call<ApiResponse<Map<String, Any>>>, response: Response<ApiResponse<Map<String, Any>>>) {
                binding.progressBar.visibility = View.GONE
                if (response.isSuccessful && response.body()?.success == true) {
                    val data = response.body()?.data
                    val contentJson = Gson().toJson(data?.get("content"))
                    val type = object : TypeToken<List<OperatorResponse>>() {}.type
                    val opList: List<OperatorResponse> = Gson().fromJson(contentJson, type) ?: emptyList()
                    adapter.updateData(opList)
                }
            }

            override fun onFailure(call: Call<ApiResponse<Map<String, Any>>>, t: Throwable) {
                binding.progressBar.visibility = View.GONE
            }
        })
    }
}
