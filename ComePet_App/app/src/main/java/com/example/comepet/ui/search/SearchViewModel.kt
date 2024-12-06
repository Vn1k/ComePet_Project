package com.example.comepet.ui.search

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.launch

class SearchViewModel : ViewModel() {
    private val firestore = FirebaseFirestore.getInstance()

    private val _userIds = MutableLiveData<List<String>>()
    val userIds: LiveData<List<String>> get() = _userIds

    private val _filteredUserIds = MutableLiveData<List<String>>()
    val filteredUserIds: LiveData<List<String>> = _filteredUserIds

    fun fetchAllUserIds() {
        viewModelScope.launch {
            try {
                val userIdsList = mutableListOf<String>()
                firestore.collection("users")
                    .get()
                    .addOnSuccessListener { documents ->
                        for (document in documents) {
                            val username = document.getString("username")
                            if (username != null) {
                                userIdsList.add(username)
                            }
                        }
                        _userIds.value = userIdsList
                        Log.d("SearchViewModel", "Fetched user IDs: $userIdsList")
                    }
                    .addOnFailureListener { exception ->
                        Log.e("SearchViewModel", "Error fetching user IDs: ${exception.message}")
                    }
            } catch (exception: Exception) {
                Log.e("SearchViewModel", "Exception during fetching: ${exception.message}")
            }
        }
    }


    // Search berdasarkan input query
    fun searchUserIds(query: String) {
        val allUsers = _userIds.value ?: emptyList()
        _filteredUserIds.value = allUsers.filter {
            it.startsWith(query, ignoreCase = true)
        }
        Log.d("SearchViewModel", "Filtered user IDs: ${_filteredUserIds.value}")

        viewModelScope.launch {
            try {
                val filteredList = mutableListOf<String>()
                val documents = firestore.collection("users")
                    .whereGreaterThanOrEqualTo("username", query)
                    .whereLessThanOrEqualTo("username", query + "\uf8ff")
                    .get()
                    .await()

                for (document in documents) {
                    val username = document.getString("username")
                    if (username != null) {
                        filteredList.add(username)
                    }
                }
                _filteredUserIds.value = filteredList
            } catch (exception: Exception) {
                exception.printStackTrace()
            }
        }
    }
}