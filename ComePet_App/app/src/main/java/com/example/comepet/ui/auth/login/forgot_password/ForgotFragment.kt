package com.example.comepet.ui.auth.login.forgot_password

import android.os.Bundle
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

class ForgotFragment : Fragment() {

    private lateinit var backButton: ImageButton
    private lateinit var emailField: TextInputEditText
    private lateinit var mAuth: FirebaseAuth
    private lateinit var sendButton: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mAuth = FirebaseAuth.getInstance()
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_forgot, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        backButton = view.findViewById(R.id.backButton)
        sendButton = view.findViewById(R.id.sendButton)
        emailField = view.findViewById(R.id.email_input_Field)

        sendButton.setOnClickListener {
            val email = emailField.text.toString().trim()
            mAuth.sendPasswordResetEmail(email)
                .addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        Toast.makeText(context, "Email sent", Toast.LENGTH_SHORT).show()
                        findNavController().navigate(R.id.navigation_forgot_to_navigation_login)
                    } else {
                        Toast.makeText(context, "Failed to send email", Toast.LENGTH_SHORT).show()
                    }
                }
        }

        backButton.setOnClickListener {
            findNavController().popBackStack()
        }
    }

}