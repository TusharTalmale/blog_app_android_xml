package com.example.blogapp.adapter

import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.blogapp.R
import com.example.blogapp.create_blog
import com.example.blogapp.model.BlogModel
import com.example.blogapp.readmoreblog
import com.google.firebase.firestore.FirebaseFirestore

// The adapter no longer needs a Context parameter if it's only used for Glide
class MyBlogAdapter(
    private val items: MutableList<BlogModel>
) : RecyclerView.Adapter<MyBlogAdapter.MyBlogViewHolder>() {

    inner class MyBlogViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val blogTitle: TextView = itemView.findViewById(R.id.blogTitle)
        private val blogDescription: TextView = itemView.findViewById(R.id.blogDescription)
        private val blogUserName: TextView = itemView.findViewById(R.id.blogUserName)
        private val blogDate: TextView = itemView.findViewById(R.id.blogDate)
        private val blogUserImage: ImageView = itemView.findViewById(R.id.blogUserImage)
        private val editButton: Button = itemView.findViewById(R.id.editButton)
        private val deleteButton: Button = itemView.findViewById(R.id.deleteButton)
        private val readMoreButton: Button = itemView.findViewById(R.id.readMoreButton)

        fun bind(blogItem: BlogModel) {
            blogTitle.text = blogItem.title
            blogDescription.text = blogItem.desc
            blogUserName.text = blogItem.author

//            val date =
            blogDate.text = blogItem.createdAt.toString()
//            val format = SimpleDateFormat("MMM dd, yyyy", Locale.getDefault())
//            blogDate.text = format.format(date)

            Glide.with(itemView.context)
                .load(blogItem.authorImage)
                .into(blogUserImage)

            // Set OnClickListeners
            editButton.setOnClickListener {
                val intent = Intent(
                    itemView.context,
                    create_blog::class.java
                )
                intent.putExtra("MODE", "EDIT")
                intent.putExtra("blogId", blogItem.blogId)
                intent.putExtra("title", blogItem.title)
                intent.putExtra("desc", blogItem.desc)
                itemView.context.startActivity(intent)
            }
            deleteButton.setOnClickListener {
                val pos = bindingAdapterPosition
                if (pos != RecyclerView.NO_POSITION) {
                    deleteBlog(blogItem.blogId, pos)
                }

            }
            readMoreButton.setOnClickListener {
                val intent = Intent(
                    itemView.context,
                    readmoreblog::class.java
                )
                intent.putExtra("blogId", blogItem.blogId)
                itemView.context.startActivity(intent)
            }
        }
    }

    private fun deleteBlog(blogId: String, position: Int) {
        FirebaseFirestore.getInstance()
            .collection("blogs")
            .document(blogId)
            .delete()
            .addOnSuccessListener {
                items.removeAt(position)
                notifyItemRemoved(position)
                notifyItemRangeChanged(position, items.size)
            }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyBlogViewHolder {
        // Inflate the layout XML file to create a View object
        val view = LayoutInflater.from(parent.context).inflate(R.layout.my_blog_item, parent, false)
        return MyBlogViewHolder(view)
    }

    override fun onBindViewHolder(holder: MyBlogViewHolder, position: Int) {
        val blogItem = items[position]
        holder.bind(blogItem)
    }

    override fun getItemCount(): Int = items.size
}
