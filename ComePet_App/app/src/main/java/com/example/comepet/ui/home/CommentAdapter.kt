package com.example.comepet.ui.home

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.comepet.R

class CommentAdapter : RecyclerView.Adapter<CommentAdapter.CommentViewHolder>() {

    private val commentList = mutableListOf<Comment>()

    fun submitList(newComments: List<Comment>) {
        commentList.clear()
        commentList.addAll(newComments)
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CommentViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_comment, parent, false)
        return CommentViewHolder(view)
    }

    override fun onBindViewHolder(holder: CommentViewHolder, position: Int) {
        val comment = commentList[position]
        holder.bind(comment)
    }

    override fun getItemCount() = commentList.size

    inner class CommentViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val username: TextView = itemView.findViewById(R.id.commenterName)
        private val commentText: TextView = itemView.findViewById(R.id.commentText)
        private val profilePicutre: ImageView = itemView.findViewById(R.id.imageViewProfileComment)

        fun bind(comment: Comment) {
            username.text = comment.username ?: "Unknown"
            commentText.text = comment.commentText
            if (comment.profilePicture.isNullOrEmpty()) {
                profilePicutre.setImageResource(R.drawable.defaultprofilepicture)
            } else {
                Glide.with(itemView.context)
                    .load(comment.profilePicture)
                    .into(profilePicutre)
            }
        }
    }
}