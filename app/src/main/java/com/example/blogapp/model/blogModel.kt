package com.example.blogapp.model

data class BlogModel(
    var blogId: String = "",
    var title: String = "",
    var author: String = "",
    var authorId: String = "",
    var authorImage: String = "",
    var desc: String = "",
    var likes: Int = 0,
    var likedBy: MutableList<String> = mutableListOf(),
    var createdAt: Long = 0L
)
