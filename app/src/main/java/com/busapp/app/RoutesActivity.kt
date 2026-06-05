package com.busapp.app

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.busapp.app.adapters.RouteAdapter
import com.busapp.app.api.*
import com.busapp.app.databinding.ActivityRoutesBinding
import com.busapp.app.utils.SessionManager
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class RoutesActivity : AppCompatActivity() {

    private lateinit var binding: ActivityRoutesBinding
    private lateinit var adapter: RouteAdapter
    private lateinit var sessionManager: SessionManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityRoutesBinding.inflate(layoutInflater)
        setContentView(binding.root)

        sessionManager = SessionManager(this)
        setupRecyclerView()
        fetchRoutes()

        binding.fabAddRoute.setOnClickListener {
            startActivity(Intent(this, AddRouteActivity::class.java))
        }
        
        binding.btnBack.setOnClickListener { finish() }
    }

    override fun onResume() {
        super.onResume()
        fetchRoutes()
    }

    private fun setupRecyclerView() {
        adapter = RouteAdapter(
            routes = emptyList(),
            onEdit = { route ->
                val intent = Intent(this, AddRouteActivity::class.java)
                intent.putExtra("ROUTE", route)
                startActivity(intent)
            },
            onDelete = { route ->
                showDeleteConfirmation(route)
            },
            onClick = { route ->
                Toast.makeText(this, "${route.departureProvince} -> ${route.destinationProvince}", Toast.LENGTH_SHORT).show()
            }
        )
        binding.rvRoutes.layoutManager = LinearLayoutManager(this)
        binding.rvRoutes.adapter = adapter
    }

    private fun showDeleteConfirmation(route: RouteResponse) {
        AlertDialog.Builder(this)
            .setTitle("Xóa tuyến đường")
            .setMessage("Bạn có chắc chắn muốn xóa tuyến ${route.departureProvince} -> ${route.destinationProvince}?")
            .setPositiveButton("Xóa") { _, _ ->
                deleteRoute(route.id)
            }
            .setNegativeButton("Hủy", null)
            .show()
    }

    private fun deleteRoute(id: Int) {
        val token = sessionManager.fetchAuthToken() ?: return
        binding.progressBar.visibility = View.VISIBLE
        
        RetrofitClient.instance.deleteRoute("Bearer $token", id).enqueue(object : Callback<ApiResponse<Void>> {
            override fun onResponse(call: Call<ApiResponse<Void>>, response: Response<ApiResponse<Void>>) {
                binding.progressBar.visibility = View.GONE
                if (response.isSuccessful) {
                    Toast.makeText(this@RoutesActivity, "Đã xóa tuyến đường", Toast.LENGTH_SHORT).show()
                    fetchRoutes()
                } else {
                    Toast.makeText(this@RoutesActivity, "Không thể xóa tuyến đường", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onFailure(call: Call<ApiResponse<Void>>, t: Throwable) {
                binding.progressBar.visibility = View.GONE
                Toast.makeText(this@RoutesActivity, "Lỗi kết nối", Toast.LENGTH_SHORT).show()
            }
        })
    }

    private fun fetchRoutes() {
        binding.progressBar.visibility = View.VISIBLE
        RetrofitClient.instance.getRoutes().enqueue(object : Callback<ApiResponse<List<RouteResponse>>> {
            override fun onResponse(call: Call<ApiResponse<List<RouteResponse>>>, response: Response<ApiResponse<List<RouteResponse>>>) {
                binding.progressBar.visibility = View.GONE
                if (response.isSuccessful && response.body()?.success == true) {
                    val routes = response.body()?.data ?: emptyList()
                    adapter.updateData(routes)
                }
            }

            override fun onFailure(call: Call<ApiResponse<List<RouteResponse>>>, t: Throwable) {
                binding.progressBar.visibility = View.GONE
            }
        })
    }
}
