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
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.comepet.R
import com.example.comepet.databinding.FragmentChatBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class ChatFragment : Fragment() {
    private lateinit var binding: FragmentChatBinding
    private lateinit var chatViewModel: ChatViewModel
    private lateinit var chatAdapter: ChatAdapter
    private lateinit var profilePicureTop: ImageView
    private lateinit var postUsernameTop: TextView
    private lateinit var recyclerView: RecyclerView
    private lateinit var sendButton: ImageButton
    private lateinit var messageInput: EditText
    private lateinit var backButton: ImageButton

    private lateinit var chatId: String

    private var senderId: String = FirebaseAuth.getInstance().currentUser?.uid ?: ""
    private var receiverId: String = ""  // Inisialisasi kosong

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Ambil receiverId dari argumen dengan cara yang aman

        receiverId = arguments?.getString("receiverId")
            ?: run {
                // Fallback atau error handling
                Toast.makeText(context, "No receiver selected", Toast.LENGTH_SHORT).show()
                findNavController().popBackStack()
                return null
            }

        binding = FragmentChatBinding.inflate(inflater, container, false)
        profilePicureTop = binding.imageViewProfile
        // Inisialisasi komponen UI
        recyclerView = binding.recyclerViewChatPersonal
        sendButton = binding.buttonLogoSend
        messageInput = binding.messageHere
        postUsernameTop = binding.postUsernameTop
        backButton = binding.backButton

        // Setup chat ID
        val chatId = if (senderId < receiverId) {
            "${senderId}_$receiverId"
        } else {
            "${receiverId}_$senderId"
        }

        // Setup ViewModel dan Adapter
        chatViewModel = ViewModelProvider(this).get(ChatViewModel::class.java)
        chatAdapter = ChatAdapter(senderId)
        recyclerView.adapter = chatAdapter

        // Load pesan
        chatViewModel.loadMessages(chatId)

        val layoutManager = LinearLayoutManager(context)
        layoutManager.stackFromEnd = true
        recyclerView.layoutManager = layoutManager
        recyclerView.adapter = chatAdapter

        // Observer untuk pesan
        chatViewModel.messages.observe(viewLifecycleOwner) { messages ->
            chatAdapter.submitList(messages)
            if (messages.isNotEmpty()) {
                recyclerView.scrollToPosition(messages.size - 1)
            }
        }

        getReceiverUsername(receiverId)

        // Setup tombol kirim
        sendButton.setOnClickListener {
            val message = messageInput.text.toString().trim()
            if (message.isNotEmpty()) {
                chatViewModel.sendMessage(senderId, receiverId, message)
                messageInput.text.clear() // Kosongkan input setelah mengirim
            } else {
                Toast.makeText(requireContext(), "Message cannot be empty", Toast.LENGTH_SHORT).show()
            }
        }

        // Setup tombol kembali
        backButton.setOnClickListener {
            findNavController().popBackStack()
        }

        return binding.root
    }

    private fun getReceiverUsername(receiverId: String) {
        FirebaseFirestore.getInstance()
            .collection("users")
            .document(receiverId)
            .get()
            .addOnSuccessListener { document ->
                document?.let {
                    val username = it.getString("username") ?: "Unknown User"
                    postUsernameTop.text = username

                    // Set profile picture
                    val profilePicture = it.getString("profilePicture")
                    profilePicture?.let { pictureUrl ->
                        if (pictureUrl.isNotEmpty()) {
                            Glide.with(requireContext())
                                .load(pictureUrl)
                                .placeholder(R.drawable.defaultprofilepicture)
                                .error(R.drawable.defaultprofilepicture)
                                .into(binding.imageViewProfile)
                        } else {
                            binding.imageViewProfile.setImageResource(R.drawable.defaultprofilepicture)
                        }
                    } ?: run {
                        binding.imageViewProfile.setImageResource(R.drawable.defaultprofilepicture)
                    }
                }
            }
            .addOnFailureListener { exception ->
                postUsernameTop.text = "Error Loading"
                binding.imageViewProfile.setImageResource(R.drawable.defaultprofilepicture)
                Log.e("ChatFragment", "Error getting receiver's details", exception)
            }
    }
}