package com.starry.greenstash

import android.annotation.SuppressLint
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.os.Looper

@SuppressLint("CustomSplashScreen")
class SplashActivity : AppCompatActivity() {
    private val LAUNCH_DELAY: Long = 250

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        Handler(Looper.getMainLooper()).postDelayed({
            intent = Intent(applicationContext, MainActivity::class.java)
            launch(intent)
        }, LAUNCH_DELAY)
    }

    private fun launch(intent: Intent) {
        startActivity(intent)
        finish()
    }
}