package com.example.comepet.ui.auth.login

import android.content.Context
import androidx.fragment.app.viewModels
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.navigation.fragment.findNavController
import com.example.comepet.R
import com.example.comepet.ui.auth.GlobalVar
import com.example.comepet.ui.auth.register.model.User
import com.example.comepet.utils.SessionManager
import com.google.android.material.textfield.TextInputEditText
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class LoginFragment : Fragment() {

    private lateinit var mAuth: FirebaseAuth
    private lateinit var registerButton: Button
    private lateinit var loginButton: Button
    private lateinit var email_input_field: TextInputEditText
    private lateinit var password_input_field: TextInputEditText
    private lateinit var forgotPassword: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mAuth = FirebaseAuth.getInstance()
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return inflater.inflate(R.layout.fragment_login, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val user = mAuth.currentUser
        if (user != null) {
            findNavController().navigate(R.id.navigation_home)
            return
        }

        registerButton = view.findViewById(R.id.registerButton)
        loginButton = view.findViewById(R.id.loginButton)
        email_input_field = view.findViewById(R.id.email_input_Field)
        password_input_field = view.findViewById(R.id.password_input_Field)

        loginButton.setOnClickListener {
            val email = email_input_field.text.toString().trim()
            val password = password_input_field.text.toString().trim()

            Login(email, password)
        }

        registerButton.setOnClickListener {
            findNavController().navigate(R.id.navigation_login_to_navigation_register)
        }

        forgotPassword = view.findViewById(R.id.forgotPassword)
        forgotPassword.setOnClickListener {
            findNavController().navigate(R.id.navigation_login_to_navigation_forgot)
        }
    }

    private fun Login(email: String, password: String) {
        if (email.isEmpty()) {
            Toast.makeText(requireContext(), "Email is required", Toast.LENGTH_SHORT).show()
        } else if (password.isEmpty()) {
            Toast.makeText(requireContext(), "Password is required", Toast.LENGTH_SHORT).show()
        } else {
            mAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        val db = FirebaseFirestore.getInstance().collection("users")
                            .document(FirebaseAuth.getInstance().currentUser?.uid.toString())
                        db.get().addOnSuccessListener { document ->
                            if (document != null) {
                                val user = document.toObject(User::class.java)
                                if (user != null) {
                                    GlobalVar.currentUser = user
                                    val token = FirebaseAuth.getInstance().currentUser?.uid
                                    if (token != null) {
                                        onLoginSuccess(token)
                                    }
                                }
                            }
                        }.addOnFailureListener { exception ->
                            Log.d("LoginFragment", "Error: ${exception.message}")
                        }
                    } else {
                        Toast.makeText(requireContext(), "Password and email is incorrect", Toast.LENGTH_SHORT).show()
                    }
                }
        }
    }

    private fun onLoginSuccess(token: String) {
        SessionManager.saveToken(requireContext(), token)
        val navController = findNavController()
        navController.navigate(R.id.navigation_welcome)
    }
}