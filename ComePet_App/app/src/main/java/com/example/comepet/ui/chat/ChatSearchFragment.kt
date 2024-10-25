package com.example.comepet.ui.chat

import androidx.fragment.app.viewModels
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.navigation.fragment.findNavController
import com.example.comepet.R

class ChatSearchFragment : Fragment() {

    private lateinit var backbuttonTohome: ImageView
    private lateinit var imageViewSearch: ImageView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // TODO: Use the ViewModel
    }

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
    }
}