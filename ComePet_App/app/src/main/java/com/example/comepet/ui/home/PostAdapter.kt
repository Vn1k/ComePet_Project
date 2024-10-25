package com.example.comepet.ui.home

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.comepet.R

class PostAdapter(private val postList: List<Post>) : RecyclerView.Adapter<PostViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PostViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_post, parent, false)
        return PostViewHolder(view)
    }

    override fun onBindViewHolder(holder: PostViewHolder, position: Int) {
        holder.bind(postList[position])
    }

    override fun getItemCount() = postList.size
}

class PostViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
    private val imageViewProfile = itemView.findViewById<ImageView>(R.id.imageViewProfile)
    private val postUsernameTop = itemView.findViewById<TextView>(R.id.postUsernameTop)
    private val postUsernameBottom = itemView.findViewById<TextView>(R.id.postUsernameBottom)
    private val postLocation = itemView.findViewById<TextView>(R.id.postLocation)
    private val postImageView = itemView.findViewById<ImageView>(R.id.postImageView)
    private val postCaption = itemView.findViewById<TextView>(R.id.postCaption)
    private val postDate = itemView.findViewById<TextView>(R.id.postDate)
    private val postLikeCount = itemView.findViewById<TextView>(R.id.postLikeCount)
    private val commentCount = itemView.findViewById<TextView>(R.id.commentCount)
    private val postLike: ImageView = itemView.findViewById(R.id.postLike)

    fun bind(post: Post) {
        postUsernameTop.text = post.username
        postUsernameBottom.text = post.username
        postLocation.text = post.location
        postCaption.text = post.caption
        postDate.text = post.date
        postLikeCount.text = post.likeCount.toString()
        commentCount.text = post.commentCount.toString()
        imageViewProfile.setImageResource(post.profileImage)
        postImageView.setImageResource(post.imageUrl)

        setLikeColor(post.isLiked)
        updateLikeDrawable(post.isLiked)

        postLike.setOnClickListener {
            post.isLiked = !post.isLiked
            setLikeColor(post.isLiked)
            updateLikeDrawable(post.isLiked)

            post.likeCount += if (post.isLiked) 1 else -1
            postLikeCount.text = post.likeCount.toString()
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

    private fun updateLikeDrawable(isLiked: Boolean) {
        val drawableRes = if (isLiked) {
            R.drawable.heart
        } else {
            R.drawable.like
        }
        postLike.setImageResource(drawableRes)
    }
}
