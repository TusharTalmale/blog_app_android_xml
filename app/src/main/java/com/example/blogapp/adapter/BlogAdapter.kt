package com.example.blogapp.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.blogapp.R
import com.example.blogapp.model.BlogModel
import com.google.firebase.firestore.FirebaseFirestore

class BlogAdapter(
    private val blogList: MutableList<BlogModel>,
    private val currentUserId: String

) : RecyclerView.Adapter<BlogAdapter.BlogViewHolder>() {

    class BlogViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val tvTitle: TextView = itemView.findViewById(R.id.tvTitle)
        val tvAuthor: TextView = itemView.findViewById(R.id.tvAuthor)
        val tvDate: TextView = itemView.findViewById(R.id.tvDate)
        val tvDesc: TextView = itemView.findViewById(R.id.eddesc)
        val tvLikes: TextView = itemView.findViewById(R.id.tvLikes)
        val ivLike: ImageView = itemView.findViewById(R.id.ivLike)
        val ivSave: ImageView = itemView.findViewById(R.id.ivSave)
        val btnReadMore: Button = itemView.findViewById(R.id.btnReadMore)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BlogViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_blog, parent, false)
        return BlogViewHolder(view)
    }

    override fun onBindViewHolder(holder: BlogViewHolder, position: Int) {
        val vlog = blogList[position]
        val blogRef = FirebaseFirestore.getInstance()
            .collection("blogs")
            .document(vlog.blogId)
        val isLiked = vlog.likedBy.contains(currentUserId)


        holder.tvTitle.text = vlog.title
        holder.tvAuthor.text = vlog.author
        holder.tvDate.text = vlog.createdAt.toString()
        holder.tvDesc.text = vlog.desc
        holder.tvLikes.text = vlog.likes.toString()

        holder.ivLike.setImageResource(
            if (isLiked) R.drawable.like_filled_red
            else R.drawable.like_black_outline
        )

//        holder.ivSave.setImageResource(
//            if (vlog.isSaved) R.drawable.save_red_fill
//            else R.drawable.save_black_outline
//        )

        holder.ivLike.setOnClickListener {

            if (isLiked) {
                blogRef.update("likedBy", vlog.likedBy.apply { remove(currentUserId) })
                vlog.likes--
                notifyItemChanged(holder.adapterPosition)
            } else {
                blogRef.update("likedBy", vlog.likedBy.apply { add(currentUserId) })

                vlog.likes++
                notifyItemChanged(holder.adapterPosition)
            }

            blogRef.update(
                mapOf(
                    "likes" to vlog.likes,
                    "likedBy" to vlog.likedBy
                )
            )

        }

//        holder.ivSave.setOnClickListener {
//            vlog.isSaved = !vlog.isSaved
//            notifyItemChanged(holder.adapterPosition)
//        }

        holder.btnReadMore.setOnClickListener {
            // next step: open detail page
        }
    }

    override fun getItemCount(): Int = blogList.size
}