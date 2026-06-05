package com.busapp.app

import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.busapp.app.adapters.UserAdapter
import com.busapp.app.api.*
import com.busapp.app.databinding.ActivityUserManagementBinding
import com.busapp.app.utils.SessionManager
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class UserManagementActivity : AppCompatActivity() {

    private lateinit var binding: ActivityUserManagementBinding
    private lateinit var adapter: UserAdapter
    private lateinit var sessionManager: SessionManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityUserManagementBinding.inflate(layoutInflater)
        setContentView(binding.root)

        sessionManager = SessionManager(this)
        setupRecyclerView()
        fetchAllUsers()
        
        binding.btnBack.setOnClickListener { finish() }
    }

    private fun setupRecyclerView() {
        adapter = UserAdapter(
            users = emptyList(),
            onLockUnlock = { user ->
                showLockUnlockDialog(user)
            },
            onUserClick = { user ->
                Toast.makeText(this, user.fullName, Toast.LENGTH_SHORT).show()
            }
        )
        binding.rvUsers.layoutManager = LinearLayoutManager(this)
        binding.rvUsers.adapter = adapter
    }

    private fun showLockUnlockDialog(user: UserResponse) {
        val action = if (user.active) "Khóa" else "Mở khóa"
        AlertDialog.Builder(this)
            .setTitle("$action tài khoản")
            .setMessage("Bạn có chắc chắn muốn $action tài khoản của ${user.fullName}?")
            .setPositiveButton("Đồng ý") { _, _ ->
                toggleUserStatus(user)
            }
            .setNegativeButton("Hủy", null)
            .show()
    }

    private fun toggleUserStatus(user: UserResponse) {
        val token = sessionManager.fetchAuthToken() ?: return
        binding.progressBar.visibility = View.VISIBLE
        
        // Gửi yêu cầu cập nhật trạng thái
        val updateMap = mapOf("active" to !user.active)
        
        RetrofitClient.instance.updateUser("Bearer $token", user.id, updateMap).enqueue(object : Callback<ApiResponse<UserResponse>> {
            override fun onResponse(call: Call<ApiResponse<UserResponse>>, response: Response<ApiResponse<UserResponse>>) {
                binding.progressBar.visibility = View.GONE
                if (response.isSuccessful) {
                    Toast.makeText(this@UserManagementActivity, "Cập nhật thành công", Toast.LENGTH_SHORT).show()
                    fetchAllUsers()
                } else {
                    Toast.makeText(this@UserManagementActivity, "Lỗi cập nhật", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onFailure(call: Call<ApiResponse<UserResponse>>, t: Throwable) {
                binding.progressBar.visibility = View.GONE
                Toast.makeText(this@UserManagementActivity, "Lỗi kết nối", Toast.LENGTH_SHORT).show()
            }
        })
    }

    private fun fetchAllUsers() {
        val token = sessionManager.fetchAuthToken() ?: return

        binding.progressBar.visibility = View.VISIBLE
        // CẬP NHẬT: Sử dụng UserPageResponse thay vì List<UserResponse>
        RetrofitClient.instance.getAllUsers("Bearer $token").enqueue(object : Callback<ApiResponse<UserPageResponse>> {
            override fun onResponse(call: Call<ApiResponse<UserPageResponse>>, response: Response<ApiResponse<UserPageResponse>>) {
                binding.progressBar.visibility = View.GONE
                if (response.isSuccessful && response.body()?.success == true) {
                    // Lấy content từ đối tượng phân trang
                    val users = response.body()?.data?.content ?: emptyList()
                    adapter.updateData(users)
                } else {
                    Toast.makeText(this@UserManagementActivity, "Lỗi tải danh sách người dùng", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onFailure(call: Call<ApiResponse<UserPageResponse>>, t: Throwable) {
                binding.progressBar.visibility = View.GONE
                Toast.makeText(this@UserManagementActivity, "Lỗi kết nối", Toast.LENGTH_SHORT).show()
            }
        })
    }
}
