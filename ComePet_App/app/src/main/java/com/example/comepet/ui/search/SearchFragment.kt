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
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.comepet.R

class SearchFragment : Fragment() {
    private lateinit var viewModel: SearchViewModel
    private lateinit var userAdapter: UserAdapter
    private lateinit var searchBar: EditText

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_search, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewModel = ViewModelProvider(this).get(SearchViewModel::class.java)

        userAdapter = UserAdapter { userId ->
            val bundle = Bundle().apply {
                putString("userId", userId)
            }
            findNavController().navigate(R.id.navigation_profile, bundle)
        }

        val recyclerView = view.findViewById<RecyclerView>(R.id.recyclerViewSearch)
        searchBar = view.findViewById(R.id.searchBar)

        recyclerView.layoutManager = LinearLayoutManager(context)
        recyclerView.adapter = userAdapter

        viewModel.userList.observe(viewLifecycleOwner) { users ->
            userAdapter.submitList(users)
        }

        searchBar.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
                val query = s.toString().lowercase()
                val filteredList = viewModel.userList.value?.filter {
                    it.name.lowercase().contains(query) || it.username.lowercase().contains(query) || it.profilePicture.lowercase().contains(query)
                }
                if (filteredList != null) {
                    userAdapter.submitList(filteredList)
                }
            }
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
        })
    }
}
