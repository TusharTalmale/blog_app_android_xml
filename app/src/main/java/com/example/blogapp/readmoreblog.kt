package com.example.blogapp

import android.os.Bundle
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.bumptech.glide.Glide
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.firebase.Timestamp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import java.text.SimpleDateFormat
import java.util.Locale

class readmoreblog : AppCompatActivity() {

    private lateinit var firestore: FirebaseFirestore
    private lateinit var auth: FirebaseAuth

    private lateinit var blogTitle: TextView
    private lateinit var authorName: TextView
    private lateinit var publishDate: TextView
    private lateinit var blogContent: TextView
    private lateinit var authorImage: ImageView
    private lateinit var backButton: ImageButton
    private lateinit var likeButton: FloatingActionButton

    private var blogId: String = ""
    private var currentUserId: String? = null
    private var isLiked = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_readmoreblog)

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        firestore = FirebaseFirestore.getInstance()
        auth = FirebaseAuth.getInstance()
        currentUserId = auth.currentUser?.uid

        initViews()
        setupListeners()

        blogId = intent.getStringExtra("blogId") ?: ""
        if (blogId.isNotEmpty()) {
            fetchBlogData()
        }
    }

    private fun initViews() {
        blogTitle = findViewById(R.id.blogTitle)
        authorName = findViewById(R.id.authorName)
        publishDate = findViewById(R.id.publishDate)
        blogContent = findViewById(R.id.blogContent)
        authorImage = findViewById(R.id.authorImage)
        backButton = findViewById(R.id.backButton)
        likeButton = findViewById(R.id.likeButton)
    }

    private fun setupListeners() {
        backButton.setOnClickListener { finish() }

        likeButton.setOnClickListener {
            if (currentUserId == null) {
                Toast.makeText(this, "Please login to like", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            toggleLikeState()
        }
    }

    private fun fetchBlogData() {
        firestore.collection("blogs").document(blogId)
            .get()
            .addOnSuccessListener { document ->
                if (!document.exists()) return@addOnSuccessListener

                blogTitle.text = document.getString("title")
                authorName.text = document.getString("author")
                blogContent.text = document.getString("desc")

                val imageUrl = document.getString("authorImage")
                if (!imageUrl.isNullOrEmpty()) {
                    Glide.with(this).load(imageUrl).into(authorImage)
                }

                val createdAt = document.getTimestamp("createdAt")
                createdAt?.let {
                    publishDate.text = createdAt.toString()
//                        formatDate(it)
                }

                val likedBy = document.get("likedBy") as? List<String> ?: emptyList()
                currentUserId?.let { uid ->
                    isLiked = likedBy.contains(uid)
                    updateLikeButtonUI()
                }
            }
            .addOnFailureListener {
                Toast.makeText(this, "Failed to load blog", Toast.LENGTH_SHORT).show()
            }
    }

    private fun toggleLikeState() {
        val blogRef = firestore.collection("blogs").document(blogId)
        val userId = currentUserId ?: return

        firestore.runTransaction { transaction ->
            val snapshot = transaction.get(blogRef)
            val likedBy = snapshot.get("likedBy") as? List<String> ?: emptyList()

            if (likedBy.contains(userId)) {
                transaction.update(blogRef, "likes", FieldValue.increment(-1))
                transaction.update(blogRef, "likedBy", FieldValue.arrayRemove(userId))
                isLiked = false
            } else {
                transaction.update(blogRef, "likes", FieldValue.increment(1))
                transaction.update(blogRef, "likedBy", FieldValue.arrayUnion(userId))
                isLiked = true
            }
            null
        }.addOnSuccessListener {
            updateLikeButtonUI()
        }.addOnFailureListener {
            Toast.makeText(this, "Like update failed", Toast.LENGTH_SHORT).show()
        }
    }

    private fun updateLikeButtonUI() {
        val icon = if (isLiked) {
            R.drawable.like_filled_red
        } else {
            R.drawable.like_white_outline
        }
        likeButton.setImageDrawable(ContextCompat.getDrawable(this, icon))
    }

    private fun formatDate(timestamp: Timestamp): String {
        val sdf = SimpleDateFormat("MMM dd, yyyy", Locale.getDefault())
        return sdf.format(timestamp.toDate())
    }
}
