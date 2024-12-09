package com.example.comepet.ui.chat

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.comepet.R
import com.example.comepet.databinding.FragmentChatSearchBinding

class ChatSearchFragment : Fragment() {
    private lateinit var binding: FragmentChatSearchBinding
    private lateinit var chatSearchViewModel: ChatSearchViewModel
    private lateinit var chatSearchAdapter: ChatSearchAdapter

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentChatSearchBinding.inflate(inflater, container, false)

        chatSearchViewModel = ViewModelProvider(this)[ChatSearchViewModel::class.java]

        chatSearchAdapter = ChatSearchAdapter { chatUser ->
            val bundle = Bundle().apply {
                putString("receiverId", chatUser.userId)
            }
            findNavController().navigate(R.id.action_chatSearchFragment_to_chatFragment, bundle)
        }

        binding.recyclerViewChatSearch.apply {
            layoutManager = LinearLayoutManager(context)
            adapter = chatSearchAdapter
        }

        chatSearchViewModel.chatUsers.observe(viewLifecycleOwner) { chatUsers ->
            chatSearchAdapter.submitList(chatUsers)

            binding.emptyStateChat.visibility = if (chatUsers.isEmpty()) View.VISIBLE else View.GONE
        }

        chatSearchViewModel.loadChatUsers()

        binding.backButton.setOnClickListener {
            findNavController().popBackStack()
        }

        return binding.root
    }
}