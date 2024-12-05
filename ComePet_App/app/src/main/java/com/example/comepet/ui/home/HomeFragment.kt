package com.example.comepet.ui.home

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.comepet.R
import com.example.comepet.ui.auth.BaseAuthFragment

class HomeFragment : BaseAuthFragment() {

    private val homeViewModel: HomeViewModel by viewModels()
    private lateinit var recyclerView: RecyclerView
    private lateinit var postAdapter: PostAdapter
    private lateinit var ChatButton: ImageButton

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_home, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        ChatButton = view.findViewById(R.id.ChatButton)
        ChatButton.setOnClickListener {
            findNavController().navigate(R.id.navigation_home_to_navigation_chat_search)
        }

        postAdapter = PostAdapter(mutableListOf())
        recyclerView = view.findViewById(R.id.recyclerViewPosts)
        recyclerView.layoutManager = LinearLayoutManager(context)
        recyclerView.adapter = postAdapter

        homeViewModel.posts.observe(viewLifecycleOwner) { postList ->
            postAdapter.updatePosts(postList)
        }
    }
}