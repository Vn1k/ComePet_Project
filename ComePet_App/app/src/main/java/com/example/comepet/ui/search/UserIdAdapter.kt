package com.example.comepet.ui.search

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.comepet.R
import com.example.comepet.ui.auth.register.model.User

class UserIdAdapter : ListAdapter<User, UserIdAdapter.UserViewHolder>(UserIdDiffCallback()) {

    var onItemClick: ((User) -> Unit)? = null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): UserViewHolder {
        val binding = LayoutInflater.from(parent.context).inflate(R.layout.item_user_id, parent, false)
        return UserViewHolder(binding)
    }

    override fun onBindViewHolder(holder: UserViewHolder, position: Int) {
        val user = getItem(position)
        holder.bind(user)
        holder.itemView.setOnClickListener {
            onItemClick?.invoke(user)
        }
    }

    class UserViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        private val userImage: ImageView = view.findViewById(R.id.userImage)
        private val usernameText: TextView = view.findViewById(R.id.usernameText)
        private val locationText: TextView = view.findViewById(R.id.locationText)

        fun bind(user: User) {
            usernameText.text = user.username
            locationText.text = user.location

            // Set the image using Glide
            Glide.with(userImage.context)
                .load(user.profilePicture)
                .into(userImage)
        }
    }

    class UserIdDiffCallback : DiffUtil.ItemCallback<User>() {
        override fun areItemsTheSame(oldItem: User, newItem: User): Boolean = oldItem.username == newItem.username
        override fun areContentsTheSame(oldItem: User, newItem: User): Boolean = oldItem == newItem
    }
}