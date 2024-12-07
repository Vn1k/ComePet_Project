package com.example.comepet.ui.chat

import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.comepet.R
import com.example.comepet.ui.home.Post
import com.google.firebase.firestore.FirebaseFirestore

class ChatSearchAdapter(private var chatList: MutableList<Chat>) : RecyclerView.Adapter<ChatSearchAdapter.ChatSearchViewHolder>() {

    private val db: FirebaseFirestore = FirebaseFirestore.getInstance()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ChatSearchViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_chat_contact, parent, false)
        return ChatSearchViewHolder(view)
    }

    override fun onBindViewHolder(holder: ChatSearchViewHolder, position: Int) {
        val chat = chatList[position]
        holder.getChatName().text = chat.name

        holder.bind(chat)
    }

    fun updateChats(newChats: List<Chat>) {
        chatList = newChats.toMutableList()
        notifyDataSetChanged()
    }

    override fun getItemCount() = chatList.size

    inner class ChatSearchViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val profileImageUserKedua = itemView.findViewById<ImageView>(R.id.imageViewProfile)
        private val chatMessage = itemView.findViewById<TextView>(R.id.chatMessage)
        private val chatName = itemView.findViewById<TextView>(R.id.chatName)

        fun getChatProfilePicture() = profileImageUserKedua
        fun getChatMessage() = chatMessage
        fun getChatName() = chatName

        fun bind(chat: Chat) {
            chatMessage.text = chat.chatMessage
            chatName.text = chat.name

            db.collection("users").document(chat.userId).collection("chats")
                .get()
                .addOnSuccessListener { querySnapshot ->
                    if (!querySnapshot.isEmpty) {
                        for (chatDoc in querySnapshot.documents) {
                            val profileImageUserId2 = chatDoc.getString("profileImageUserId2")

                            if (profileImageUserId2 != null) {
                                Glide.with(itemView.context)
                                    .load(profileImageUserId2)
                                    .into(profileImageUserKedua)
                            } else {
                                Log.d("ChatSearchAdapter", "profileImageUserId2 is null for chatId: ${chat.idChat}")
                            }
                        }
                    } else {
                        Log.d("ChatSearchAdapter", "chatSnapshot is empty for userId: ${chat.userId}")
                    }
                }
        }
    }

}