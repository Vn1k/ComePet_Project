package com.example.comepet.ui.setting

import androidx.fragment.app.viewModels
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.navigation.fragment.findNavController
import com.example.comepet.R
import com.example.comepet.ui.auth.BaseAuthFragment
import com.google.firebase.Firebase
import com.google.firebase.auth.auth

class SettingFragment : BaseAuthFragment() {

    private lateinit var logoutButton: View

    companion object {
        fun newInstance() = SettingFragment()
    }

    private val viewModel: SettingViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // TODO: Use the ViewModel
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return inflater.inflate(R.layout.fragment_setting, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        logoutButton = view.findViewById(R.id.logout_button)
            val user = Firebase.auth.currentUser
            if (user == null) {
                logoutButton.visibility = View.GONE
            } else {
                logoutButton.visibility = View.VISIBLE
                logoutButton.setOnClickListener {
                    Firebase.auth.signOut()
                    findNavController().navigate(R.id.navigation_login)
                }
            }
    }
}