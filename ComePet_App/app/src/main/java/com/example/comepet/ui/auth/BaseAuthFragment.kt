package com.example.comepet.ui.auth

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.example.comepet.R
import com.google.firebase.auth.FirebaseAuth

open class BaseAuthFragment : Fragment() {
    lateinit var mAuth: FirebaseAuth
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        mAuth = FirebaseAuth.getInstance()
        val currentUser = mAuth.currentUser
        if(currentUser == null) {
            redirectToOption()
        } else if (isUserBlocked()) {
            redirectToOption()
        }
    }

    private fun isUserBlocked(): Boolean {
        return false
    }

    private fun redirectToOption() {
        findNavController().navigate(R.id.navigation_login)
    }
}