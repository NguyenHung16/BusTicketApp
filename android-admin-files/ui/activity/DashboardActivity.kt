package com.example.busadmin.ui.activity

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.busadmin.databinding.ActivityDashboardBinding
import com.example.busadmin.utils.SessionManager

class DashboardActivity : AppCompatActivity() {

    private lateinit var binding: ActivityDashboardBinding
    private lateinit var sessionManager: SessionManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityDashboardBinding.inflate(layoutInflater)
        setContentView(binding.root)

        sessionManager = SessionManager(this)

        // Hiển thị tên user
        val fullName = sessionManager.getUserFullName() ?: "Admin"
        val role = sessionManager.getUserRole() ?: "Admin"
        binding.tvWelcome.text = "Xin chào, $fullName ($role)"

        setupMenuButtons()
    }

    private fun setupMenuButtons() {
        binding.btnTrips.setOnClickListener {
            startActivity(Intent(this, TripsActivity::class.java))
        }

        binding.btnRoutes.setOnClickListener {
            startActivity(Intent(this, RoutesActivity::class.java))
        }

        binding.btnOperators.setOnClickListener {
            startActivity(Intent(this, OperatorsActivity::class.java))
        }

        binding.btnLogout.setOnClickListener {
            sessionManager.logout()
            val intent = Intent(this, LoginActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            startActivity(intent)
            finish()
        }
    }
}
