package com.example.blogapp.register

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.blogapp.R

class Welcome_activity : AppCompatActivity() {

    lateinit var btnLogin: Button
    lateinit var btnRegister: Button
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_welcome)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
        initViews()
        setListeners()
    }

    private fun initViews() {
        btnLogin = findViewById(R.id.button)
        btnRegister = findViewById(R.id.button3)
    }

    private fun setListeners() {

        btnLogin.setOnClickListener {
            openAuthScreen("login")
        }

        btnRegister.setOnClickListener {
            openAuthScreen("register")
        }
    }
    private fun openAuthScreen(mode: String) {
        val intent = Intent(this, Signinandregister_activity::class.java)
        intent.putExtra("auth_mode", mode)
        startActivity(intent)
    }
}