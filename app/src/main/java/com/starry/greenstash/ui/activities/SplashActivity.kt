package com.starry.greenstash.ui.activities

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import androidx.appcompat.app.AppCompatActivity

@SuppressLint("CustomSplashScreen")
class SplashActivity : AppCompatActivity() {
    private val LAUNCH_DELAY: Long = 250

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.S) {
            Handler(Looper.getMainLooper()).postDelayed({
                intent = Intent(applicationContext, MainActivity::class.java)
                launch(intent)
            }, LAUNCH_DELAY)
        } else {
            launch(Intent(applicationContext, MainActivity::class.java))
        }

    }

    private fun launch(intent: Intent) {
        startActivity(intent)
        finish()
    }
}