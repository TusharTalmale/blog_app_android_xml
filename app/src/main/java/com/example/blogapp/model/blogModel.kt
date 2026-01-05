package com.example.blogapp.model

import com.google.firebase.Timestamp

data class BlogModel(
    var blogId: String = "",
    var title: String = "",
    var author: String = "",
    var authorId: String = "",
    var authorImage: String = "",
    var desc: String = "",
    var likes: Int = 0,
    var likedBy: MutableList<String> = mutableListOf(),
    var createdAt: Timestamp? = null
)
