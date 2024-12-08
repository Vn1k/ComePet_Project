package com.example.comepet.ui.search

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.comepet.ui.auth.register.model.User
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.launch

class SearchViewModel : ViewModel() {
    private val db = FirebaseFirestore.getInstance()
    private val _userList = MutableLiveData<List<User>>()
    val userList: LiveData<List<User>> get() = _userList

    init {
        fetchAllUsers()
    }

    private fun fetchAllUsers() {
        db.collection("users")
            .get()
            .addOnSuccessListener { result ->
                val users = mutableListOf<User>()
                for (document in result) {
                    val user = document.toObject(User::class.java).copy(
                        userId = document.id
                    )
                    users.add(user)
                }
                _userList.postValue(users)
            }
            .addOnFailureListener { exception ->
                Log.e("SearchViewModel", "Error fetching user data", exception)
            }
    }
}
