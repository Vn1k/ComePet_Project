package com.example.comepet.ui.auth.register

import android.content.Context
import android.widget.Toast
import androidx.core.content.ContentProviderCompat.requireContext
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.comepet.ui.auth.register.model.User
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.CollectionReference
import com.google.firebase.firestore.FirebaseFirestore

class RegisterViewModel : ViewModel() {
//    private val mAuth: FirebaseAuth = FirebaseAuth.getInstance()
//    private val db: FirebaseFirestore = FirebaseFirestore.getInstance()
//
//    private val _registerStatus = MutableLiveData<Boolean>()
//    val registerStatus: LiveData<Boolean> = _registerStatus
//
//    fun registerUser(
//        name: String,
//        username: String,
//        email: String,
//        password: String,
//        confirm_password: String
//    ) {
//        if (name.isEmpty()) {
//            _registerStatus.value = false
//            return
//        } else if (username.isEmpty()) {
//            _registerStatus.value = false
//            return
//        } else if (email.isEmpty()) {
//            _registerStatus.value = false
//            return
//        } else if (password.isEmpty()) {
//            _registerStatus.value = false
//            return
//        } else if (confirm_password.isEmpty()) {
//            _registerStatus.value = false
//            return
//        }
//        if (password == confirm_password) {
//            mAuth.createUserWithEmailAndPassword(email, password)
//                .addOnCompleteListener { task ->
//                    if (task.isSuccessful) {
//                        val userId = mAuth.currentUser?.uid
//                        if (userId != null) {
//                            val user = User(
//                                name = name,
//                                username = username,
//                                email = email,
//                                password = password
//                            )
//                            db.collection("users")
//                                .document(userId)
//                                .set(user)
//                                .addOnSuccessListener {
//                                    _registerStatus.value = true
//                                }
//                                .addOnFailureListener {
//                                    _registerStatus.value = false
//                                }
//                        } else {
//                            _registerStatus.value = false
//                        }
//                    }
//                }
//        } else {
//            _registerStatus.value = false
//        }
//    }
}