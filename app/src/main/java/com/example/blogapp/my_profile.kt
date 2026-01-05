package com.example.blogapp

import android.content.Intent
import android.os.Bundle
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import com.example.blogapp.register.Signinandregister_activity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class my_profile : AppCompatActivity() {
    private lateinit var ivProfile: ImageView
    private lateinit var tvUsername: TextView

    private lateinit var menuAdd: LinearLayout
    private lateinit var menuMyArticles: LinearLayout
    private lateinit var menuLogout: LinearLayout

    private lateinit var auth: FirebaseAuth
    private lateinit var firestore: FirebaseFirestore

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_my_profile)
        auth = FirebaseAuth.getInstance()
        firestore = FirebaseFirestore.getInstance()

        ivProfile = findViewById(R.id.ivProfileImage)
        tvUsername = findViewById(R.id.tvUsername)

        menuAdd = findViewById(R.id.menuAdd)
        menuMyArticles = findViewById(R.id.menuMyArticles)
        menuLogout = findViewById(R.id.menuLogout)



        loadUserData()
        setupMenu()
    }

    private fun loadUserData() {
        val userId = auth.currentUser?.uid ?: return

        firestore.collection("users")
            .document(userId)
            .get()
            .addOnSuccessListener { doc ->
                if (!doc.exists()) return@addOnSuccessListener

                val name = doc.getString("name") ?: "User"
                val imageUrl = doc.getString("profileImageUrl")

                tvUsername.text = name

                Glide.with(this)
                    .load(imageUrl)
                    .placeholder(R.drawable.person2)
                    .error(R.drawable.person2)
                    .circleCrop()
                    .into(ivProfile)
            }

    }

    fun setupMenu() {
        setupItem(R.id.menuAdd, R.drawable.plus_icon_outline, "Add new article")
        setupItem(R.id.menuMyArticles, R.drawable.blog_outline_white, "Your articles")
        setupItem(R.id.menuLogout, R.drawable.logout_outline_white, "Log out")
        setupMenuClicks()
    }

    private fun setupItem(id: Int, icon: Int, title: String) {
        val item = findViewById<LinearLayout>(id)
        item.findViewById<ImageView>(R.id.ivIconforuse).setImageResource(icon)
        item.findViewById<TextView>(R.id.tvTitleforuse).text = title
    }

    private fun setupMenuClicks() {

        // ➕ Add new article
        menuAdd.setOnClickListener {
            startActivity(Intent(this, create_blog::class.java))
        }

        // 📄 Your articles
        menuMyArticles.setOnClickListener {
            startActivity(Intent(this, MyBlogsActivity::class.java))
        }

        // 🚪 Logout
        menuLogout.setOnClickListener {
            auth.signOut()
            Toast.makeText(this, "Logged out", Toast.LENGTH_SHORT).show()

            val intent = Intent(this, Signinandregister_activity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            startActivity(intent)
        }
    }
}