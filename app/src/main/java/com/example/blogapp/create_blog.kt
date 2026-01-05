package com.example.blogapp

import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.ImageButton
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class create_blog : AppCompatActivity() {

    private lateinit var etTitle: EditText
    private lateinit var etDesc: EditText
    private lateinit var btnAddBlog: Button
    private lateinit var btnBack: ImageButton

    private lateinit var firestore: FirebaseFirestore
    private lateinit var auth: FirebaseAuth
    private var isEditMode = false
    private var editBlogId: String? = null


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_create_blog)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
        val mode = intent.getStringExtra("MODE")

        if (mode == "EDIT") {
            isEditMode = true
            editBlogId = intent.getStringExtra("blogId")

            etTitle.setText(intent.getStringExtra("title"))
            etDesc.setText(intent.getStringExtra("desc"))

            btnAddBlog.text = "Update Blog"
        }

        firestore = FirebaseFirestore.getInstance()
        auth = FirebaseAuth.getInstance()

        etTitle = findViewById(R.id.etTitle)
        etDesc = findViewById(R.id.etDesc)
        btnAddBlog = findViewById(R.id.addblogbutton)
        btnBack = findViewById(R.id.backbutton)

        btnAddBlog.setOnClickListener {
            if (isEditMode) {
                updateBlog()
            } else {
                createBlog()
            }
        }


        btnBack.setOnClickListener {
            finish()
        }


    }

    private fun createBlog() {
        val title = etTitle.text.toString().trim()
        val desc = etDesc.text.toString().trim()
        val user = auth.currentUser
        if (title.isEmpty() || desc.isEmpty()) {
            Toast.makeText(this, "Please fill all fields", Toast.LENGTH_SHORT).show()
            return
        }

        if (user != null) {
            Toast.makeText(this, "Blog Created", Toast.LENGTH_SHORT).show()

        }
        val userId = user?.uid ?: return
        val blogId = firestore.collection("blogs").document().id

        firestore.collection("users")
            .document(userId)
            .get()
            .addOnSuccessListener { doc ->

                val authorName = doc.getString("name") ?: "Anonymous"
                val authorImage = doc.getString("profileImageUrl") ?: ""

                val blog = hashMapOf(
                    "blogId" to blogId,
                    "title" to title,
                    "desc" to desc,
                    "author" to authorName,
                    "authorId" to userId,
                    "authorImage" to authorImage,
                    "likes" to 0,
                    "likedBy" to emptyList<String>(),
                    "createdAt" to com.google.firebase.Timestamp.now()

                )

                firestore.collection("blogs")
                    .document(blogId)
                    .set(blog)
                    .addOnSuccessListener {
                        Toast.makeText(this, "Blog added successfully", Toast.LENGTH_SHORT).show()
                        finish()
                    }
            }


    }

    private fun updateBlog() {
        val title = etTitle.text.toString().trim()
        val desc = etDesc.text.toString().trim()

        if (title.isEmpty() || desc.isEmpty()) {
            Toast.makeText(this, "Please fill all fields", Toast.LENGTH_SHORT).show()
            return
        }

        val blogId = editBlogId ?: return

        firestore.collection("blogs")
            .document(blogId)
            .update(
                mapOf(
                    "title" to title,
                    "desc" to desc,
                    "updatedAt" to com.google.firebase.Timestamp.now()
                )
            )
            .addOnSuccessListener {
                Toast.makeText(this, "Blog updated", Toast.LENGTH_SHORT).show()
                finish()
            }
    }


}