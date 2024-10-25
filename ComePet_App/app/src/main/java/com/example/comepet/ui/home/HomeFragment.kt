package com.example.comepet.ui.home

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.ImageView
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.comepet.R
import com.example.comepet.ui.auth.BaseAuthFragment

class HomeFragment : BaseAuthFragment() {

    private lateinit var viewModel: HomeViewModel
    private lateinit var ChatButton: ImageButton

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_home, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewModel = ViewModelProvider(this).get(HomeViewModel::class.java)
        ChatButton = view.findViewById(R.id.ChatButton)
        ChatButton.setOnClickListener {
            findNavController().navigate(R.id.navigation_home_to_navigation_chat_search)
        }

        val recyclerView = view.findViewById<RecyclerView>(R.id.recyclerViewPosts)
        recyclerView.layoutManager = LinearLayoutManager(context)

        val adapter = PostAdapter(viewModel.getPosts())
        recyclerView.adapter = adapter
    }
}