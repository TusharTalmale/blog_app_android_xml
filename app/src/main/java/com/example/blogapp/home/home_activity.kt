package com.example.blogapp.home

import android.os.Bundle
import android.widget.ImageView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.blogapp.R
import com.example.blogapp.adapter.BlogAdapter
import com.example.blogapp.model.BlogModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ListenerRegistration
import com.google.firebase.firestore.Query

class HomeActivity : AppCompatActivity() {

    private lateinit var rvBlogs: RecyclerView
    private lateinit var blogAdapter: BlogAdapter
    private val blogList = mutableListOf<BlogModel>()

    private lateinit var firestore: FirebaseFirestore
    private lateinit var auth: FirebaseAuth
    private var blogListener: ListenerRegistration? = null
    private lateinit var ivProfile: ImageView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_home)

        firestore = FirebaseFirestore.getInstance()
        auth = FirebaseAuth.getInstance()

        val userId = auth.currentUser?.uid ?: return


        ivProfile = findViewById(R.id.profile_image)

        rvBlogs = findViewById(R.id.recycleviewlist)
        rvBlogs.layoutManager = LinearLayoutManager(this)
        loadUserProfileImage()

        blogAdapter = BlogAdapter(blogList, userId)
        rvBlogs.adapter = blogAdapter
        fetchBlogsFromFirestore()
    }

    private fun loadUserProfileImage() {
        val userId = auth.currentUser?.uid ?: return

        firestore.collection("users")
            .document(userId)
            .get()
            .addOnSuccessListener { document ->
                if (document.exists()) {
                    val imageUrl = document.getString("profileImageUrl")

                    if (!imageUrl.isNullOrEmpty()) {
                        Glide.with(this)
                            .load(imageUrl)
                            .placeholder(R.drawable.person2)
                            .error(R.drawable.person2)
                            .circleCrop()
                            .into(ivProfile)
                    }
                }
            }
    }

    private fun fetchBlogsFromFirestore() {
        blogListener = firestore.collection("blogs")
            .orderBy("createdAt", Query.Direction.DESCENDING)
            .addSnapshotListener { snapshot, exception ->

                if (exception != null || snapshot == null) return@addSnapshotListener

                blogList.clear()
                for (document in snapshot.documents) {
                    val blog = document.toObject(BlogModel::class.java)
                    blog?.blogId = document.id
                    blog?.let { blogList.add(it) }
                }
                blogAdapter.notifyDataSetChanged()
            }
    }

    override fun onDestroy() {
        super.onDestroy()
        blogListener?.remove()
    }
}
