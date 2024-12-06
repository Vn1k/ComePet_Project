package com.example.comepet.ui.chat

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.example.comepet.databinding.ItemChatPeronalBinding

class ChatAdapter(
    private val senderId: String
) : ListAdapter<ChatMessage, ChatAdapter.ChatViewHolder>(ChatDiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ChatViewHolder {
        val binding = ItemChatPeronalBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ChatViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ChatViewHolder, position: Int) {
        val message = getItem(position)
        holder.bind(message)
    }

    inner class ChatViewHolder(private val binding: ItemChatPeronalBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(message: ChatMessage) {
            if (message.senderId == senderId) {
                // If the message is from the sender, set it to senderChatMessage
                binding.senderChatMessage.text = message.message
                binding.senderChatMessage.visibility = View.VISIBLE
                binding.receiverChatMessage.visibility = View.GONE
            } else {
                // If the message is from the receiver, set it to receiverChatMessage
                binding.receiverChatMessage.text = message.message
                binding.receiverChatMessage.visibility = View.VISIBLE
                binding.senderChatMessage.visibility = View.GONE
            }
        }
    }

    class ChatDiffCallback : DiffUtil.ItemCallback<ChatMessage>() {
        override fun areItemsTheSame(oldItem: ChatMessage, newItem: ChatMessage): Boolean {
            return oldItem.idMessage == newItem.idMessage
        }

        override fun areContentsTheSame(oldItem: ChatMessage, newItem: ChatMessage): Boolean {
            return oldItem == newItem
        }
    }
}
