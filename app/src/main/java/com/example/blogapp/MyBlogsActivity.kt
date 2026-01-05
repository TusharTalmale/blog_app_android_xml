package com.example.blogapp

import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.blogapp.adapter.MyBlogAdapter
import com.example.blogapp.databinding.ActivityMyBlogsBinding
import com.example.blogapp.model.BlogModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query

class MyBlogsActivity : AppCompatActivity() {

    private val binding: ActivityMyBlogsBinding by lazy {
        ActivityMyBlogsBinding.inflate(layoutInflater)
    }

    private lateinit var firestore: FirebaseFirestore
    private lateinit var auth: FirebaseAuth
    private lateinit var adapter: MyBlogAdapter

    private val blogItems = mutableListOf<BlogModel>()


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        // Initialize Firebase
        auth = FirebaseAuth.getInstance()
        firestore = FirebaseFirestore.getInstance()

        // Setup the back button
        binding.backButton.setOnClickListener {
            finish()
        }

        // Get current user's ID
        val currentUserId = auth.currentUser?.uid

        // Setup RecyclerView
        adapter = MyBlogAdapter(blogItems)
        binding.myBlogsRecyclerView.layoutManager = LinearLayoutManager(this)
        binding.myBlogsRecyclerView.adapter = adapter

        if (currentUserId != null) {
            fetchUserBlogs(currentUserId)
        }
    }

    private fun fetchUserBlogs(currentUserId: String) {

        firestore.collection("blogs")
            .whereEqualTo("authorId", currentUserId)
            .orderBy("createdAt", Query.Direction.DESCENDING)
            .get()
            .addOnSuccessListener { documents ->
                blogItems.clear()
                for (document in documents) {
                    val blogItem = document.toObject(BlogModel::class.java).apply {
                        blogId = document.id
                    }
                    blogItems.add(blogItem)
                }
                adapter.notifyDataSetChanged()
            }
            .addOnFailureListener { exception ->
                // Log the error or show a toast message to the user
                // For example:
                Log.e("MyBlogsActivity", "Error fetching blogs", exception)
                Toast.makeText(this, "Error fetching blogs", Toast.LENGTH_SHORT).show()
            }
    }
}
