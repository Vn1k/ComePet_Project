package com.example.comepet.ui.setting

import android.media.Image
import androidx.fragment.app.viewModels
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageButton
import android.widget.TextView
import android.widget.Toast
import androidx.navigation.fragment.findNavController
import com.example.comepet.R
import com.example.comepet.ui.auth.BaseAuthFragment
import com.google.firebase.Firebase
import com.google.firebase.auth.auth

class SettingFragment : BaseAuthFragment() {

    private lateinit var logoutButton: View
    private lateinit var cancelButton: ImageButton
    private lateinit var emailView: TextView
    private lateinit var statusEmail: TextView
    private lateinit var resendEmailButton: Button
    private lateinit var changePasswordButton: ImageButton
    private lateinit var changeEmailButton: ImageButton

    companion object {
        fun newInstance() = SettingFragment()
    }

    private val viewModel: SettingViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return inflater.inflate(R.layout.fragment_setting, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        cancelButton = view.findViewById(R.id.cancelButton)
        cancelButton.setOnClickListener{
            findNavController().popBackStack()
        }

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

        emailView = view.findViewById(R.id.emailView)
        emailView.text = Firebase.auth.currentUser?.email

        statusEmail = view.findViewById(R.id.statusEmail)
        resendEmailButton = view.findViewById(R.id.resendEmailButton)
        Firebase.auth.currentUser?.reload()?.addOnCompleteListener { task ->
            if (!isAdded) return@addOnCompleteListener

            if (task.isSuccessful) {
                val isVerified = Firebase.auth.currentUser?.isEmailVerified == true
                statusEmail.text = if (isVerified) "Verified" else "Not Verified"

                val verifiedColor = if (isVerified) {
                    requireContext().getColor(R.color.verified_color)
                } else {
                    requireContext().getColor(R.color.not_verified_color)
                }

                statusEmail.setTextColor(verifiedColor)
            } else {
                statusEmail.text = "Not Verified"
                statusEmail.setTextColor(requireContext().getColor(R.color.not_verified_color))
            }
        }

        resendEmailButton.setOnClickListener {
            if (!isAdded) return@setOnClickListener

            Firebase.auth.currentUser?.sendEmailVerification()
                ?.addOnCompleteListener { task ->
                    if (!isAdded) return@addOnCompleteListener

                    val message = if (task.isSuccessful) {
                        "Verification email sent"
                    } else {
                        "Failed to send email"
                    }
                    Toast.makeText(requireContext(), message, Toast.LENGTH_SHORT).show()
                }
        }

        changePasswordButton = view.findViewById(R.id.ChangePasswordButton)
        changePasswordButton.setOnClickListener {
            findNavController().navigate(R.id.navigation_setting_to_navigation_change_password)
        }

        changeEmailButton = view.findViewById(R.id.ChangeEmailButton)
        changeEmailButton.setOnClickListener{
            findNavController().navigate(R.id.navigation_setting_to_navigation_change_email)
        }
    }
}