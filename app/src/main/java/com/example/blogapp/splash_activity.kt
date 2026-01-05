package com.example.blogapp

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import com.example.blogapp.home.HomeActivity
import com.example.blogapp.register.Signinandregister_activity
import com.google.firebase.auth.FirebaseAuth

class splash_activity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_splash)
        val firebaseAuth = FirebaseAuth.getInstance()

        Handler(Looper.getMainLooper()).postDelayed({

            if (firebaseAuth.currentUser != null) {
                // User already logged in
                startActivity(Intent(this, HomeActivity::class.java))
            } else {
                // User not logged in
                startActivity(Intent(this, Signinandregister_activity::class.java))
            }

            finish()

        }, 1500)
    }
}