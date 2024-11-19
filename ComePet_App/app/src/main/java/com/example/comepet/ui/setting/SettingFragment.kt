package com.example.comepet.ui.setting

import android.animation.ObjectAnimator
import android.content.Context
import android.media.Image
import androidx.fragment.app.viewModels
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.CycleInterpolator
import android.widget.Button
import android.widget.ImageButton
import android.widget.TextView
import android.widget.Toast
import android.widget.ToggleButton
import androidx.appcompat.widget.SwitchCompat
import androidx.navigation.fragment.findNavController
import com.example.comepet.R
import com.example.comepet.ui.auth.BaseAuthFragment
import com.example.comepet.utils.SessionManager
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.auth
import com.google.firebase.firestore.CollectionReference
import com.google.firebase.firestore.FirebaseFirestore
import kotlin.math.log

class SettingFragment : BaseAuthFragment() {

    private lateinit var logoutButton: View
    private lateinit var cancelButton: ImageButton
    private lateinit var emailView: TextView
    private lateinit var statusEmail: TextView
    private lateinit var resendEmailButton: Button
    private lateinit var changePasswordButton: ImageButton
    private lateinit var changeEmailButton: ImageButton
    private lateinit var accountStatusButton: SwitchCompat
    private lateinit var db: FirebaseFirestore
    private lateinit var user: CollectionReference

    companion object {
        fun newInstance() = SettingFragment()
    }

    private val viewModel: SettingViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        db = FirebaseFirestore.getInstance()
        user = db.collection("users")
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
                    SessionManager.clearToken(requireContext())
                    mAuth.signOut()
                    findNavController().navigate(R.id.navigation_login)
                }
            }

        emailView = view.findViewById(R.id.emailView)
        emailView.text = Firebase.auth.currentUser?.email

        statusEmail = view.findViewById(R.id.statusEmail)
        statusEmail.text = "Checking..."
        resendEmailButton = view.findViewById(R.id.resendEmailButton)
        resendEmailButton.visibility = View.GONE
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
                resendEmailButton.visibility = if (isVerified) View.GONE else View.VISIBLE
            } else {
                statusEmail.text = "Status unknown"
                statusEmail.setTextColor(requireContext().getColor(R.color.not_verified_color))
                resendEmailButton.visibility = View.VISIBLE
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

        accountStatusButton = view.findViewById(R.id.accountStatusButton)
        val currentUserId = FirebaseAuth.getInstance().currentUser?.uid
        val userCollection = FirebaseFirestore.getInstance().collection("users")

        accountStatusButton.setOnCheckedChangeListener { _, isChecked ->
            if (currentUserId != null) {
                userCollection.document(currentUserId).update("accountStatus", isChecked)
                    .addOnSuccessListener {
                    }
                    .addOnFailureListener { e ->
                        accountStatusButton.isChecked = !isChecked
                        shakeView(accountStatusButton)
                        Toast.makeText(
                            requireContext(),
                            "Failed to update account status: ${e.localizedMessage}",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
            } else {
                accountStatusButton.isChecked = !isChecked
                shakeView(accountStatusButton)
                Toast.makeText(
                    requireContext(),
                    "User not authenticated.",
                    Toast.LENGTH_SHORT
                ).show()
            }
        }
    }

    private fun shakeView(view: View) {
        val animator = ObjectAnimator.ofFloat(view, "translationX", 0f, 25f, -25f, 25f, -25f, 15f, -15f, 6f, -6f, 0f)
        animator.duration = 500
        animator.interpolator = CycleInterpolator(1f)
        animator.start()
    }
}