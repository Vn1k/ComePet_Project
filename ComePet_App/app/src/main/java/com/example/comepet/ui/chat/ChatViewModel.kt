package com.example.comepet.ui.chat

import android.os.Message
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.firebase.database.ChildEventListener
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

data class ChatMessage(
    val senderId: String = "",
    val receiverId: String = "",
    val message: String = "",
    val date: String = "",
    val idMessage: String = "",
    val timestamp: Long = System.currentTimeMillis()
)

class ChatViewModel : ViewModel() {

    private val database = FirebaseDatabase.getInstance("https://comepet-e8840-default-rtdb.firebaseio.com/").reference
    private val _messages = MutableLiveData<List<ChatMessage>>(emptyList())
    val messages: LiveData<List<ChatMessage>> get() = _messages

    fun loadMessages(chatId: String) {
        val messagesRef = database.child("chats").child(chatId).child("messages")

        messagesRef.orderByChild("timestamp").addChildEventListener(object : ChildEventListener {
            override fun onChildAdded(snapshot: DataSnapshot, previousChildName: String?) {
                val chatMessage = snapshot.getValue(ChatMessage::class.java)
                chatMessage?.let {
                    val currentList = _messages.value.orEmpty().toMutableList()
                    if (!currentList.contains(it)) {
                        currentList.add(it)
                        _messages.value = currentList.sortedBy { it.timestamp }
                    }
                }
            }

            override fun onChildChanged(snapshot: DataSnapshot, previousChildName: String?) {}
            override fun onChildRemoved(snapshot: DataSnapshot) {}
            override fun onChildMoved(snapshot: DataSnapshot, previousChildName: String?) {}
            override fun onCancelled(error: DatabaseError) {
                Log.w("Firebase", "loadMessages:onCancelled", error.toException())
            }
        })
    }

    fun sendMessage(senderId: String, receiverId: String, message: String) {
        val chatId = if (senderId < receiverId) {
            "${senderId}_$receiverId"
        } else {
            "${receiverId}_$senderId"
        }

        val messageId = database.child("chats").child(chatId).child("messages").push().key
        val date = getCurrentDate()
        val timestamp = System.currentTimeMillis()

        val chatMessage = ChatMessage(
            senderId = senderId,
            receiverId = receiverId,
            message = message,
            date = date,
            idMessage = messageId ?: "",
            timestamp = timestamp
        )

        messageId?.let {
            database.child("chats").child(chatId).child("messages").child(it).setValue(chatMessage)
                .addOnSuccessListener {
                    Log.d("Firebase", "Message sent successfully.")
                }
                .addOnFailureListener { e ->
                    Log.w("Firebase", "Error sending message", e)
                }
        }
    }

    private fun getCurrentDate(): String {
        val dateFormat = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault())
        return dateFormat.format(Date())
    }
}
