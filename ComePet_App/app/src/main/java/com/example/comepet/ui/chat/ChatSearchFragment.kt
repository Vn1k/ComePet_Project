package com.example.comepet.ui.chat

import androidx.fragment.app.viewModels
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.comepet.R
import com.example.comepet.ui.auth.BaseAuthFragment

class ChatSearchFragment : BaseAuthFragment() {

    private val chatSearchViewModel: ChatSearchViewModel by viewModels()
    private lateinit var recyclerView: RecyclerView
    private lateinit var chatSearchAdapter: ChatSearchAdapter
    private lateinit var backbuttonTohome: ImageView
    private lateinit var imageViewSearch: ImageView

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return inflater.inflate(R.layout.fragment_chat, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        backbuttonTohome = view.findViewById(R.id.backbuttonTohome)
        imageViewSearch = view.findViewById(R.id.imageViewSearch)

        backbuttonTohome.setOnClickListener {
            findNavController().navigate(R.id.action_chatSearchFragment_to_homeFragment)
        }

        imageViewSearch.setOnClickListener {
            findNavController().navigate(R.id.action_chatSearchFragment_to_chatFragment)
        }

        chatSearchAdapter = ChatSearchAdapter(mutableListOf())
        recyclerView = view.findViewById(R.id.recyclerViewChatContact)
        recyclerView.layoutManager = LinearLayoutManager(context)
        recyclerView.adapter = chatSearchAdapter

        chatSearchViewModel.chats.observe(viewLifecycleOwner) { chatList ->
            Log.d("ChatSearchFragment", "Chat List Size: ${chatList.size}")
            chatSearchAdapter.updateChats(chatList)
        }
    }
}