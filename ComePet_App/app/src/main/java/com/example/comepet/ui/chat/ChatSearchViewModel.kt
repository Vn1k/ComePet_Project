package com.example.comepet.ui.chat

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.QuerySnapshot
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

data class Chat(
    val userId: String,
    var name: String,
    val userNameKedua: String,
    val profileImageUserId2: String = "",
    val chatMessage: String,
    val idChat: String = "",
    val date: String = ""
)

class ChatSearchViewModel : ViewModel() {

    private val _chats = MutableLiveData<List<Chat>>()
    val chats: LiveData<List<Chat>> get() = _chats

    private val db: FirebaseFirestore = FirebaseFirestore.getInstance()

    init {
        fetchChats()
    }

    private fun fetchChats() {
        db.collection("users")
            .get()
            .addOnSuccessListener { userSnapshot: QuerySnapshot ->
                val chatList = mutableListOf<Chat>()

                for (userDoc in userSnapshot.documents) {
                    val userId = userDoc.id
                    val name = userDoc.getString("name") ?: ""

                    db.collection("users").document(userId).collection("chats")
                        .get()
                        .addOnSuccessListener { chatSnapshot: QuerySnapshot ->
                            for (chatDoc in chatSnapshot.documents) {
                                val chatId = chatDoc.id
                                val userNameKedua = chatDoc.getString("userNameKedua") ?: ""
                                val chatMessage = chatDoc.getString("chatMessage") ?: ""
                                val profileImageUserId2 = chatDoc.getString("profileImageUserId2") ?: ""
                                val date = chatDoc.getString("date") ?: getCurrentDate()

                                Log.d("ChatSearchViewModel", "chatMessage: $chatMessage")
                                Log.d("ChatSearchViewModel", "profileImageUserId2: $profileImageUserId2")
                                Log.d("ChatSearchViewModel", "name: $name")
                                Log.d("ChatSearchViewModel", "userNameKedua: $userNameKedua")

                                val chat = Chat(
                                    userId = userId,
                                    name = name,
                                    userNameKedua = userNameKedua,
                                    profileImageUserId2 = profileImageUserId2,
                                    chatMessage = chatDoc.getString("chatMessage") ?: "",
                                    date = date,
                                    idChat = chatId
                                )
                                chatList.add(chat)
                            }

                            val sortedChats = chatList.sortedByDescending { chat ->
                                val dateFormat = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault())
                                dateFormat.parse(chat.date)
                            }
                            _chats.value = sortedChats
                        }
                        .addOnFailureListener { error: Exception ->
                            Log.e("ChatSearchViewModel", "Failed to fetch chats: ${error.message}")
                        }
                }
            }
            .addOnFailureListener { error: Exception ->
                Log.e("ChatSearchViewModel", "Failed to fetch users: ${error.message}")
            }
    }

    private fun getCurrentDate(): String {
        val dateFormat = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault())
        return dateFormat.format(Date())
    }
}