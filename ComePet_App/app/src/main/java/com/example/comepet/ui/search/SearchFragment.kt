package com.example.comepet.ui.search

import androidx.fragment.app.viewModels
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.comepet.R

class SearchFragment : Fragment() {

    private lateinit var searchInput: EditText
    private lateinit var recyclerView: RecyclerView
    private val viewModel: SearchViewModel by viewModels()

    companion object {
        fun newInstance() = SearchFragment()
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        searchInput = view.findViewById(R.id.searchBar)
        recyclerView = view.findViewById(R.id.recyclerViewChats)

        recyclerView.layoutManager = LinearLayoutManager(context)
        val adapter = UserIdAdapter()
        recyclerView.adapter = adapter

        adapter.onItemClick = { userId ->
            // Navigasi ke fragment detail, atau tampilkan toast
            Toast.makeText(context, "User ID: $userId", Toast.LENGTH_SHORT).show()
        }

        // Observe the user IDs LiveData
        viewModel.userIds.observe(viewLifecycleOwner, Observer { userIds ->
            adapter.submitList(userIds)
        })

        // Search when user types in the search bar
        searchInput.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                val query = s.toString()
                viewModel.searchUserIds(query)
            }
            override fun afterTextChanged(s: Editable?) {}
        })

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return inflater.inflate(R.layout.fragment_search, container, false)
    }
}