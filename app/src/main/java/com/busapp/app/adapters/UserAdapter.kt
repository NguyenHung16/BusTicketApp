package com.busapp.app.adapters

import android.graphics.Color
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.busapp.app.api.UserResponse
import com.busapp.app.databinding.ItemUserBinding

class UserAdapter(
    private var users: List<UserResponse>,
    private val onLockUnlock: (UserResponse) -> Unit,
    private val onUserClick: (UserResponse) -> Unit
) : RecyclerView.Adapter<UserAdapter.UserViewHolder>() {

    class UserViewHolder(val binding: ItemUserBinding) : RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): UserViewHolder {
        val binding = ItemUserBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return UserViewHolder(binding)
    }

    override fun onBindViewHolder(holder: UserViewHolder, position: Int) {
        val user = users[position]
        holder.binding.apply {
            tvUserFullName.text = user.fullName
            tvUserEmail.text = user.email
            tvUserRole.text = "Role: ${user.role}"
            tvUserStatus.text = if (user.active) "Active" else "Locked"
            tvUserStatus.setTextColor(if (user.active) Color.parseColor("#4CAF50") else Color.RED)

            // Hành động khóa/mở khóa giờ được thực hiện khi nhấn vào dòng trạng thái chữ
            tvUserStatus.setOnClickListener { onLockUnlock(user) }
            root.setOnClickListener { onUserClick(user) }
        }
    }

    override fun getItemCount(): Int = users.size

    fun updateData(newUsers: List<UserResponse>) {
        users = newUsers
        notifyDataSetChanged()
    }
}
