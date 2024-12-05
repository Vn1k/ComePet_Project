package com.example.comepet.ui.home

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.liveData
import com.example.comepet.R
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.QuerySnapshot
import kotlinx.coroutines.tasks.await
import java.util.UUID

data class Post(
    val userId: String,
    val email: String,
    var username: String,
    var name: String,
    val caption: String,
    val location: String,
    val imageUrl: String = "",
    val date: String = "",
    var isLiked: Boolean = false,
    var likeCount: Int = 0,
    var isCommented: Boolean = false,
    val commentCount: Int = 0,
    val profileImage: String = "",
    val address: String = "",
    val phoneNumber: String = "",
    val bio: String = "",
    val id: String = ""
)

class HomeViewModel : ViewModel() {

    private val _posts = MutableLiveData<List<Post>>()
    val posts: LiveData<List<Post>> get() = _posts

    private val db: FirebaseFirestore = FirebaseFirestore.getInstance()

    init {
        fetchPosts()
    }

    private fun fetchPosts() {
        db.collection("users")
            .get()
            .addOnSuccessListener { userSnapshot: QuerySnapshot ->
                val postList = mutableListOf<Post>()

                for (userDoc in userSnapshot.documents) {
                    val userId = userDoc.id
                    val username = userDoc.getString("username") ?: ""
                    val email = userDoc.getString("email") ?: ""
                    val name = userDoc.getString("name") ?: ""

                    db.collection("users").document(userId).collection("feeds")
                        .get()
                        .addOnSuccessListener { feedSnapshot: QuerySnapshot ->
                            for (feedDoc in feedSnapshot.documents) {
                                val feedId = feedDoc.id
                                val caption = feedDoc.getString("caption") ?: ""
                                val location = feedDoc.getString("location") ?: ""
                                val imageUrl = feedDoc.getString("imageUrl")?: ""
                                val profileImageUrl = userDoc.getString("profileImage") ?: ""

                                Log.d("HomeViewModel", "imageUrl: $imageUrl")
                                Log.d("HomeViewModel", "location: $location")
                                Log.d("HomeViewModel", "caption: $caption")

                                val likeCount = feedDoc.getLong("likeCount")?.toInt() ?: 0
                                val commentCount = feedDoc.getLong("commentCount")?.toInt() ?: 0

                                val post = Post(
                                    userId = userId,
                                    email = email,
                                    username = username,
                                    name = name,
                                    caption = caption,
                                    location = location,
                                    profileImage = profileImageUrl,
                                    imageUrl = imageUrl,
                                    date = "2024-11-19", // yang ini juga
                                    isLiked = false,
                                    isCommented = false,
                                    likeCount = likeCount,
                                    commentCount = commentCount,
                                    id = feedId
                                )
                                postList.add(post)
                            }
                            _posts.value = postList
                        }
                        .addOnFailureListener { error: Exception ->
                            Log.e("HomeViewModel", "Failed to fetch feeds: ${error.message}")
                        }
                }
            }
            .addOnFailureListener { error: Exception ->
                Log.e("HomeViewModel", "Failed to fetch users: ${error.message}")
            }
    }
}