package com.example.comepet.ui.home

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

data class Post(
    val userId: String = "",
    val email: String = "",
    var username: String = "",
    var name: String = "",
    val caption: String = "",
    val imageUrl: String = "",
    val date: String = "",
    var isLiked: Boolean = false,
    val profileImage: String = "",
    val address: String = "",
    val phoneNumber: String = "",
    val bio: String = ""
)

class HomeViewModel : ViewModel() {
    private val _posts = MutableLiveData<List<Post>>()
    val posts: LiveData<List<Post>> get() = _posts

    private val databaseRef: DatabaseReference = FirebaseDatabase
        .getInstance("https://comepet-e8840-default-rtdb.firebaseio.com/")
        .getReference("posts")

    private val usersRef: DatabaseReference = FirebaseDatabase
        .getInstance("https://comepet-e8840-default-rtdb.firebaseio.com/")
        .getReference("users")

    init {
        fetchPosts()
    }

    fun fetchPosts() {
        databaseRef.orderByChild("date").addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val postList = mutableListOf<Post>()
                val userMap = mutableMapOf<String, String>()

                usersRef.addListenerForSingleValueEvent(object : ValueEventListener {
                    override fun onDataChange(userSnapshot: DataSnapshot) {
                        for (user in userSnapshot.children) {
                            val userId = user.key
                            val username = user.child("username").getValue(String::class.java)
                            if (userId != null && username != null) {
                                userMap[userId] = username
                            }
                        }

                        for (postSnapshot in snapshot.children) {
                            val post = postSnapshot.getValue(Post::class.java)
                            post?.let {
                                it.username = userMap[it.userId] ?: ""
                                postList.add(it)
                            }
                        }

                        postList.reverse()

                        if (_posts.value != postList) {
                            _posts.value = postList
                        }
                        Log.d("HomeViewModel", "Fetched ${postList.size} posts.")
                    }

                    override fun onCancelled(error: DatabaseError) {
                        Log.e("HomeViewModel", "Failed to fetch users: ${error.message}")
                    }
                })
            }

            override fun onCancelled(error: DatabaseError) {
                Log.e("HomeViewModel", "Failed to fetch posts: ${error.message}")
            }
        })
    }
}