package com.example.comepet.ui.chat

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.firestore.FirebaseFirestore

class ChatSearchViewModel : ViewModel() {
    private val currentUserId = FirebaseAuth.getInstance().currentUser?.uid ?: ""
    private val database = FirebaseDatabase.getInstance("https://comepet-e8840-default-rtdb.firebaseio.com/").reference
    private val firestore = FirebaseFirestore.getInstance()

    private val _chatUsers = MutableLiveData<List<ChatUser>>()
    val chatUsers: LiveData<List<ChatUser>> = _chatUsers

    fun loadChatUsers() {
        database.child("chats")
            .orderByKey()
            .addValueEventListener(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    val userChats = mutableListOf<ChatUser>()

                    for (chatSnapshot in snapshot.children) {
                        val chatId = chatSnapshot.key ?: continue

                        if (!chatId.contains(currentUserId)) continue

                        val otherUserId = chatId.replace("${currentUserId}_", "")
                            .replace("_$currentUserId", "")

                        val lastMessageSnapshot = chatSnapshot.child("messages")
                            .children.lastOrNull()

                        val lastMessage = lastMessageSnapshot
                            ?.child("message")
                            ?.getValue(String::class.java) ?: ""

                        val timestamp = lastMessageSnapshot
                            ?.child("timestamp")
                            ?.getValue(Long::class.java) ?: 0

                        fetchUserDetails(otherUserId, lastMessage, timestamp, userChats)
                    }
                }

                override fun onCancelled(error: DatabaseError) {
                    Log.e("ChatSearchViewModel", "Error loading chats", error.toException())
                }
            })
    }

    private fun fetchUserDetails(
        userId: String,
        lastMessage: String,
        timestamp: Long,
        userChats: MutableList<ChatUser>
    ) {
        firestore.collection("users")
            .document(userId)
            .get()
            .addOnSuccessListener { document ->
                val username = document.getString("username") ?: "Unknown User"
                val profilePicture = document.getString("profilePicture") ?: ""

                val chatUser = ChatUser(
                    userId = userId,
                    username = username,
                    profilePicture = profilePicture,
                    lastMessage = lastMessage,
                    timestamp = timestamp
                )
                userChats.add(chatUser)

                val sortedChats = userChats.sortedByDescending { it.timestamp }
                _chatUsers.value = sortedChats
            }
            .addOnFailureListener { e ->
                Log.e("ChatSearchViewModel", "Error fetching user details", e)
            }
    }
}