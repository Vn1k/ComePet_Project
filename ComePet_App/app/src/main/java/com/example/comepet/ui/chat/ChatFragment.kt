package com.example.comepet.ui.chat

import androidx.fragment.app.viewModels
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.EditText
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.RecyclerView
import com.example.comepet.R
import com.example.comepet.databinding.FragmentChatBinding
import com.google.firebase.auth.FirebaseAuth

class ChatFragment : Fragment() {
    private lateinit var chatViewModel: ChatViewModel
    private lateinit var chatAdapter: ChatAdapter
    private lateinit var recyclerView: RecyclerView
    private lateinit var sendButton: ImageButton
    private lateinit var messageInput: EditText

    private var senderId: String = FirebaseAuth.getInstance().currentUser?.uid ?: ""
    private var receiverId: String = arguments?.getString("receiverId") ?: "defaultReceiverId"
    private lateinit var chatId: String

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val binding = FragmentChatBinding.inflate(inflater, container, false)
        recyclerView = binding.recyclerViewChatPersonal
        sendButton = binding.buttonLogoSend
        messageInput = binding.messageHere

        chatId = if (senderId < receiverId) {
            senderId + "_" + receiverId
        } else {
            receiverId + "_" + senderId
        }

        chatViewModel = ViewModelProvider(this).get(ChatViewModel::class.java)

        chatAdapter = ChatAdapter(senderId)
        recyclerView.adapter = chatAdapter

        chatViewModel.loadMessages(chatId)

        chatViewModel.messages.observe(viewLifecycleOwner, Observer { messages: List<ChatMessage> ->
            Log.d("ChatFragment", "Messages: $messages")
            chatAdapter.submitList(messages)
        })

        sendButton.setOnClickListener {
            val message = messageInput.text.toString().trim()
            if (message.isNotEmpty()) {
                chatViewModel.sendMessage(senderId, receiverId, message)
                messageInput.text.clear()
            }
        }

        return binding.root
    }
}