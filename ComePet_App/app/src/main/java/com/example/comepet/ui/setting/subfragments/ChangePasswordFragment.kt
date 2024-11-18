package com.example.comepet.ui.setting.subfragments

import android.os.Bundle
import android.os.Handler
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageButton
import android.widget.Toast
import androidx.navigation.fragment.findNavController
import com.example.comepet.R
import com.google.android.material.textfield.TextInputEditText
import com.google.firebase.auth.FirebaseAuth

class ChangePasswordFragment : Fragment() {
    private lateinit var changePasswordButton: Button
    private lateinit var cancelButton: ImageButton
    private lateinit var currentPasswordField: TextInputEditText
    private lateinit var newPasswordField: TextInputEditText
    private lateinit var mAuth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mAuth = FirebaseAuth.getInstance()
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_change_password, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val user = mAuth.currentUser
        currentPasswordField = view.findViewById(R.id.currentPasswordField)
        newPasswordField = view.findViewById(R.id.newPasswordField)

        cancelButton = view.findViewById(R.id.cancelButton)
        cancelButton.setOnClickListener{
            findNavController().popBackStack()
        }
        changePasswordButton = view.findViewById(R.id.changePasswordButton)
        changePasswordButton.setOnClickListener {
            val currentPassword = currentPasswordField.text.toString().trim()
            val newPassword = newPasswordField.text.toString().trim()
            if(currentPassword.isEmpty()){
                Toast.makeText(requireContext(), "Please enter your current password", Toast.LENGTH_SHORT).show()
            } else if(newPassword.isEmpty()){
                Toast.makeText(requireContext(), "Please enter your new password", Toast.LENGTH_SHORT).show()
            } else {
                user?.updatePassword(newPassword)?.addOnCompleteListener { task ->
                    if(task.isSuccessful){
                        Toast.makeText(requireContext(), "Password changed successfully", Toast.LENGTH_SHORT).show()
                        Handler().postDelayed({
                            mAuth.signOut()
                            findNavController().navigate(R.id.navigation_login)
                        }, 2000)
                    } else {
                        Toast.makeText(requireContext(), "Failed to change password", Toast.LENGTH_SHORT).show()
                    }
                }
            }
        }
    }
}