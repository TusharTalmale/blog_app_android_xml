package com.example.blogapp.home

import android.content.Intent
import android.os.Bundle
import android.widget.ImageView
import android.widget.SearchView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.blogapp.R
import com.example.blogapp.adapter.BlogAdapter
import com.example.blogapp.create_blog
import com.example.blogapp.model.BlogModel
import com.example.blogapp.my_profile
import com.google.android.material.button.MaterialButton
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ListenerRegistration
import java.util.Locale

class HomeActivity : AppCompatActivity() {

    private lateinit var rvBlogs: RecyclerView
    private lateinit var blogAdapter: BlogAdapter
    private val blogList = mutableListOf<BlogModel>()

    private lateinit var firestore: FirebaseFirestore
    private lateinit var auth: FirebaseAuth
    private var blogListener: ListenerRegistration? = null
    private lateinit var ivProfile: ImageView

    private lateinit var addblogbutton: MaterialButton

    private val filteredList = mutableListOf<BlogModel>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_home)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
        firestore = FirebaseFirestore.getInstance()
        auth = FirebaseAuth.getInstance()

        val userId = auth.currentUser?.uid ?: return


        ivProfile = findViewById(R.id.profile_image)
        val searchView = findViewById<SearchView>(R.id.searchView)
        searchView.post {
            val searchEditText =
                searchView.findViewById<android.widget.EditText>(
                    androidx.appcompat.R.id.search_src_text
                )

            searchEditText?.apply {
                setTextColor(android.graphics.Color.BLACK)
                setHintTextColor(android.graphics.Color.GRAY)
                background = null
            }
        }

        searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {

            override fun onQueryTextSubmit(query: String?): Boolean {
                return false
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                filterBlogs(newText)
                return true
            }
        })


        rvBlogs = findViewById(R.id.recycleviewlist)
        rvBlogs.layoutManager = LinearLayoutManager(this)
        loadUserProfileImage()

        blogAdapter = BlogAdapter(filteredList, userId)
        rvBlogs.adapter = blogAdapter

        fetchBlogsFromFirestore()

        addblogbutton = findViewById(R.id.add_blog_button_in_home)
        addblogbutton.setOnClickListener {
            val intent = Intent(this, create_blog::class.java)
            startActivity(intent)
            
        }
        ivProfile.setOnClickListener {
            val intent = Intent(this, my_profile::class.java)
            startActivity(intent)
        }

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
//            .orderBy("createdAt", Query.Direction.DESCENDING)
            .addSnapshotListener { snapshot, exception ->

                if (exception != null || snapshot == null) return@addSnapshotListener

                blogList.clear()

                for (document in snapshot.documents) {
                    val blog = document.toObject(BlogModel::class.java)
                    blog?.blogId = document.id
                    blog?.let { blogList.add(it) }
                }

                // Initially show all blogs
                filteredList.clear()
                filteredList.addAll(blogList)
                blogAdapter.notifyDataSetChanged()
            }
    }


    override fun onDestroy() {
        super.onDestroy()
        blogListener?.remove()
    }

    private fun filterBlogs(query: String?) {
        filteredList.clear()

        if (query.isNullOrBlank()) {
            filteredList.addAll(blogList)
        } else {
            val searchText = query.lowercase(Locale.getDefault())

            for (blog in blogList) {
                if (
                    blog.title.lowercase().contains(searchText) ||
                    blog.author.lowercase().contains(searchText) ||
                    blog.desc.lowercase().contains(searchText)
                ) {
                    filteredList.add(blog)
                }
            }
        }

        blogAdapter.notifyDataSetChanged()
    }

}
