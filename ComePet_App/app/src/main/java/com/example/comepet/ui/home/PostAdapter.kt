package com.example.comepet.ui.home

import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.comepet.R
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class PostAdapter(private var posts: List<Post>) : RecyclerView.Adapter<PostAdapter.PostViewHolder>() {

    inner class PostViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val imageView: ImageView = itemView.findViewById(R.id.postImageView)
        val imageViewProfile: ImageView = itemView.findViewById(R.id.imageViewProfile)
        val usernameText: TextView = itemView.findViewById(R.id.postUsername)
        val captionText: TextView = itemView.findViewById(R.id.postCaption)
        val dateText: TextView = itemView.findViewById(R.id.postDate)
        val postLike: ImageView = itemView.findViewById(R.id.postLike)

        fun bind(post: Post) {
            usernameText.text = post.username
            captionText.text = post.caption
            dateText.text = post.date

            val userId = post.userId
            FirebaseDatabase.getInstance()
                .getReference("users")
                .child(userId)
                .child("profileImage")
                .addListenerForSingleValueEvent(object : ValueEventListener {
                    override fun onDataChange(snapshot: DataSnapshot) {
                        val profileImageUrl = snapshot.getValue(String::class.java)
                        if (profileImageUrl != null) {
                            Glide.with(itemView.context)
                                .load(profileImageUrl)
                                .circleCrop()
                                .into(imageViewProfile)

                        } else {
                            Log.e("PostAdapter", "Profile image URL is null")
                        }
                    }

                    override fun onCancelled(error: DatabaseError) {
                        Log.e("PostAdapter", "Error getting profile image URL: ${error.message}")
                    }
                })

            setLikeColor(post.isLiked)

            postLike.setOnClickListener {
                post.isLiked = !post.isLiked
                setLikeColor(post.isLiked)
            }
        }

        private fun setLikeColor(isLiked: Boolean) {
            val color = if (isLiked) {
                itemView.context.getColor(android.R.color.holo_red_dark)
            } else {
                itemView.context.getColor(android.R.color.black)
            }
            postLike.setColorFilter(color, android.graphics.PorterDuff.Mode.SRC_IN)
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PostViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_post, parent, false)
        return PostViewHolder(view)
    }

    override fun onBindViewHolder(holder: PostViewHolder, position: Int) {
        val post = posts[position]
        holder.usernameText.text = post.username
        holder.captionText.text = post.caption
        holder.dateText.text = post.date

        Glide.with(holder.itemView.context)
            .load(post.imageUrl)
            .into(holder.imageView)

    }

    override fun getItemCount(): Int = posts.size

    fun updatePosts(newPosts: List<Post>) {
        posts = newPosts.toMutableList()
        notifyDataSetChanged()
    }
}