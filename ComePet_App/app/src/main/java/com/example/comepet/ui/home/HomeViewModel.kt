package com.example.comepet.ui.home

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.comepet.R
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
    val location: String = "",
    val imageUrl: Int = 0,
    val date: String = "",
    var isLiked: Boolean = false,
    val likeCount: Int = 0,
    val commentCount: Int = 0,
    val profileImage: Int = 0,
    val address: String = "",
    val phoneNumber: String = "",
    val bio: String = ""
)

class HomeViewModel : ViewModel() {

    fun getPosts(): List<Post> {
        return listOf(
            Post(
                username = "Calista Belva",
                location = "Tangerang, Indonesia",
                caption = "Lousi adalah kucing yang lucu",
                date = "2024-10-25",
                imageUrl = R.drawable.lousimini,
                profileImage = R.drawable.calista_circle,
                likeCount = 0,
                commentCount = 0
            )
        )
    }
}