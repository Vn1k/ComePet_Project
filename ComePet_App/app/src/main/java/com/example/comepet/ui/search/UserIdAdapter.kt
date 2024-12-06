package com.example.comepet.ui.search

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.comepet.R

class UserIdAdapter : ListAdapter<String, UserIdAdapter.UserIdViewHolder>(UserIdDiffCallback()) {

    var onItemClick: ((String) -> Unit)? = null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): UserIdViewHolder {
        val binding = LayoutInflater.from(parent.context).inflate(R.layout.item_user_id, parent, false)
        return UserIdViewHolder(binding)
    }

    override fun onBindViewHolder(holder: UserIdViewHolder, position: Int) {
        val userId = getItem(position)
        holder.bind(userId)
        holder.itemView.setOnClickListener {
            onItemClick?.invoke(userId)
        }
    }

    class UserIdViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val userIdTextView: TextView = itemView.findViewById(R.id.userIdTextView)

        fun bind(userId: String) {
            userIdTextView.text = userId
        }
    }

    class UserIdDiffCallback : DiffUtil.ItemCallback<String>() {
        override fun areItemsTheSame(oldItem: String, newItem: String): Boolean = oldItem == newItem
        override fun areContentsTheSame(oldItem: String, newItem: String): Boolean = oldItem == newItem
    }


}