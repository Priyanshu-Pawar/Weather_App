package com.example.weatherapp

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.os.Looper

class SplashActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash)

        Handler(Looper.getMainLooper()).postDelayed({
            // Create an intent to start the MainActivity
            val intent = Intent(this, MainActivity::class.java)

            // Start the new activity
            startActivity(intent)
            // Finish the current activity
            finish()
        }, 3000)
    }
}