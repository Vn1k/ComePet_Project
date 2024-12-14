package com.example.comepet.ui.chat

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

data class ChatUser(
    val userId: String = "",
    val profilePicture: String = "",
    val username: String = "",
    val lastMessage: String = "",
    val timestamp: Long = 0
)

class ChatSearchAdapter(
    private val onItemClick: (ChatUser) -> Unit
) : ListAdapter<ChatUser, ChatSearchAdapter.ChatSearchViewHolder>(ChatSearchDiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ChatSearchViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_chat_search, parent, false)
        return ChatSearchViewHolder(view, onItemClick)
    }

    override fun onBindViewHolder(holder: ChatSearchViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    inner class ChatSearchViewHolder(
        itemView: View,
        private val onItemClick: (ChatUser) -> Unit
    ) : RecyclerView.ViewHolder(itemView) {
        private val usernameTextView: TextView = itemView.findViewById(R.id.username_chat_search)
        private val lastMessageTextView: TextView = itemView.findViewById(R.id.last_message_chat_search)
        private val profilePicture: ImageView = itemView.findViewById(R.id.imageViewProfile)

        fun bind(chatUser: ChatUser) {
            usernameTextView.text = chatUser.username
            lastMessageTextView.text = chatUser.lastMessage

            if (chatUser.profilePicture.isNotEmpty()) {
                Glide.with(itemView.context)
                    .load(chatUser.profilePicture)
                    .placeholder(R.drawable.defaultprofilepicture)
                    .error(R.drawable.defaultprofilepicture)
                    .circleCrop()
                    .into(profilePicture)
            } else {
                profilePicture.setImageResource(R.drawable.defaultprofilepicture)
            }

            itemView.setOnClickListener {
                onItemClick(chatUser)
            }
        }
    }

    class ChatSearchDiffCallback : DiffUtil.ItemCallback<ChatUser>() {
        override fun areItemsTheSame(oldItem: ChatUser, newItem: ChatUser): Boolean {
            return oldItem.userId == newItem.userId
        }

        override fun areContentsTheSame(oldItem: ChatUser, newItem: ChatUser): Boolean {
            return oldItem == newItem
        }
    }
}