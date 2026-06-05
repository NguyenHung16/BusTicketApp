package com.busapp.app

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        
        // Chuyển hướng đến màn hình Login làm màn hình khởi đầu
        startActivity(Intent(this, LoginActivity::class.java))
        finish()
    }
}
