package com.busapp.app

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.busapp.app.databinding.ActivityDashboardBinding
import com.busapp.app.utils.SessionManager

class DashboardActivity : AppCompatActivity() {

    private lateinit var binding: ActivityDashboardBinding
    private lateinit var sessionManager: SessionManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityDashboardBinding.inflate(layoutInflater)
        setContentView(binding.root)

        sessionManager = SessionManager(this)
        
        val userName = sessionManager.getUserName()
        val userRole = sessionManager.getUserRole()
        binding.tvWelcome.text = "Xin chào, $userName ($userRole)"

        binding.btnReports.setOnClickListener {
            startActivity(Intent(this, AdminReportsActivity::class.java))
        }

        binding.btnAdminBookings.setOnClickListener {
            startActivity(Intent(this, AdminBookingsActivity::class.java))
        }

        binding.btnOperators.setOnClickListener {
            startActivity(Intent(this, OperatorsActivity::class.java))
        }

        binding.btnRoutes.setOnClickListener {
            startActivity(Intent(this, RoutesActivity::class.java))
        }

        binding.btnTrips.setOnClickListener {
            startActivity(Intent(this, TripsActivity::class.java))
        }

        binding.btnUsers.setOnClickListener {
            startActivity(Intent(this, UserManagementActivity::class.java))
        }

        binding.btnLogout.setOnClickListener {
            sessionManager.clearSession()
            startActivity(Intent(this, LoginActivity::class.java))
            finishAffinity()
        }
    }
}
