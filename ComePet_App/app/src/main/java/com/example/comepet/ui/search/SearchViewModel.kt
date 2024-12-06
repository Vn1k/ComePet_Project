package com.example.comepet.ui.search

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

    fun searchUserIds(query: String) {
        viewModelScope.launch {
            try {
                val userIdsList = mutableListOf<String>()
                val documents = firestore.collection("users")
                    .whereGreaterThanOrEqualTo("userId", query)
                    .whereLessThanOrEqualTo("userId", query + "\uf8ff")
                    .get()
                    .await()

                for (document in documents) {
                    val userId = document.getString("userId")
                    if (userId != null) {
                        userIdsList.add(userId)
                    }
                }
                _userIds.value = userIdsList
            } catch (exception: Exception) {
                // Handle any potential errors
                exception.printStackTrace()
            }
        }
    }
}