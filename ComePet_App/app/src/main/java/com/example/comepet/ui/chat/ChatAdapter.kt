package com.example.comepet.ui.chat

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.example.comepet.R

class ChatAdapter(
    private val senderId: String
) : ListAdapter<ChatMessage, ChatAdapter.ChatViewHolder>(ChatDiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ChatViewHolder {
        // Inflate layout tanpa menggunakan View Binding
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_chat_peronal, parent, false)
        return ChatViewHolder(view)
    }

    override fun onBindViewHolder(holder: ChatViewHolder, position: Int) {
        val message = getItem(position)
        holder.bind(message)
    }

    // ViewHolder menggunakan findViewById
    inner class ChatViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val senderChatMessage: TextView = itemView.findViewById(R.id.senderChatMessage)
        private val receiverChatMessage: TextView = itemView.findViewById(R.id.receiverChatMessage)

        fun bind(message: ChatMessage) {
            if (message.senderId == senderId) {
                senderChatMessage.text = message.message
                senderChatMessage.visibility = View.VISIBLE
                receiverChatMessage.visibility = View.GONE
            } else {
                receiverChatMessage.text = message.message
                receiverChatMessage.visibility = View.VISIBLE
                senderChatMessage.visibility = View.GONE
            }
        }
    }

    // DiffUtil untuk efisiensi RecyclerView
    class ChatDiffCallback : DiffUtil.ItemCallback<ChatMessage>() {
        override fun areItemsTheSame(oldItem: ChatMessage, newItem: ChatMessage): Boolean {
            return oldItem.idMessage == newItem.idMessage
        }

        override fun areContentsTheSame(oldItem: ChatMessage, newItem: ChatMessage): Boolean {
            return oldItem == newItem
        }
    }
}
