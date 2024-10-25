package com.example.comepet.ui.auth.register

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.Toast
import androidx.navigation.fragment.findNavController
import com.example.comepet.R
import com.example.comepet.ui.auth.GlobalVar
import com.example.comepet.ui.auth.register.model.User
import com.google.android.material.textfield.TextInputEditText
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.CollectionReference
import com.google.firebase.firestore.FirebaseFirestore

class RegisterFragment : Fragment() {
    private lateinit var mAuth: FirebaseAuth
    private lateinit var db: FirebaseFirestore
    private lateinit var user: CollectionReference

    private lateinit var name_input_Field: TextInputEditText
    private lateinit var username_input_Field: TextInputEditText
    private lateinit var email_input_Field: TextInputEditText
    private lateinit var password_input_Field: TextInputEditText
    private lateinit var confirm_password_input_Field: TextInputEditText

    private lateinit var register_button: Button
    private lateinit var login_button: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return inflater.inflate(R.layout.fragment_register, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        mAuth = FirebaseAuth.getInstance()
        db = FirebaseFirestore.getInstance()
        user = db.collection("users")

        name_input_Field = view.findViewById(R.id.name_input_Field)
        username_input_Field = view.findViewById(R.id.username_input_Field)
        email_input_Field = view.findViewById(R.id.email_input_Field)
        password_input_Field = view.findViewById(R.id.password_input_Field)
        confirm_password_input_Field = view.findViewById(R.id.confirm_password_input_Field)

        register_button = view.findViewById(R.id.registerButton)
        login_button = view.findViewById(R.id.loginButton)

        register_button.setOnClickListener{
            val name = name_input_Field.text.toString().trim()
            val username = username_input_Field.text.toString().trim()
            val email = email_input_Field.text.toString().trim()
            val password = password_input_Field.text.toString().trim()
            val confirmPassword = confirm_password_input_Field.text.toString().trim()

            if (name.isEmpty()) {
                Toast.makeText(context, "Please enter your name", Toast.LENGTH_SHORT).show()
            }else if (username.isEmpty()) {
                Toast.makeText(context, "Please enter your username", Toast.LENGTH_SHORT).show()
            } else if (email.isEmpty()) {
                Toast.makeText(context, "Please enter your email", Toast.LENGTH_SHORT).show()
            } else if (password != confirmPassword) {
                Toast.makeText(context, "Password does not match", Toast.LENGTH_SHORT).show()
            } else {
                isUserRegistered(email, username) { isRegistered, message ->
                    if (isRegistered) {
                        Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
                    } else {
                        mAuth.createUserWithEmailAndPassword(email, password)
                            .addOnCompleteListener(requireActivity()) { task ->
                                if (task.isSuccessful) {
                                    val userId = mAuth.currentUser?.uid
                                    if (userId != null) {
                                        val user = User(
                                            name = name_input_Field.text.toString(),
                                            username = username_input_Field.text.toString(),
                                            email = email_input_Field.text.toString()
                                        )
                                        db.collection("users").document(userId)
                                            .set(user)
                                            .addOnSuccessListener {
                                                GlobalVar.currentUser = user
                                                Toast.makeText(requireContext(), "Account Created", Toast.LENGTH_SHORT).show()
                                                findNavController().navigate(R.id.navigation_register_to_navigation_boarding)
                                            }
                                            .addOnFailureListener {
                                                Toast.makeText(requireContext(), "Failed to create account", Toast.LENGTH_SHORT).show()
                                            }
                                    }
                                } else {
                                    Toast.makeText(requireContext(), "Failed to create account", Toast.LENGTH_SHORT).show()
                                }
                            }
                    }
                }
            }
        }

        login_button.setOnClickListener {
            findNavController().navigate(R.id.navigation_register_to_navigation_login)
        }
    }

    private fun isUserRegistered(email: String, username: String, onResult: (Boolean, String) -> Unit) {
        mAuth.fetchSignInMethodsForEmail(email)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    val signInMethods = task.result?.signInMethods
                    if (signInMethods != null && signInMethods.isNotEmpty()) {
                        onResult(true, "Email already registered")
                    } else {
                        db.collection("users")
                            .whereEqualTo("email", email)
                            .get()
                            .addOnSuccessListener { emailDocuments ->
                                if (!emailDocuments.isEmpty) {
                                    onResult(true, "Email already registered")
                                } else {
                                    db.collection("users")
                                        .whereEqualTo("username", username)
                                        .get()
                                        .addOnSuccessListener { usernameDocuments ->
                                            if (!usernameDocuments.isEmpty) {
                                                onResult(true, "Username already taken")
                                            } else {
                                                onResult(false, "")
                                            }
                                        }
                                        .addOnFailureListener {
                                            onResult(false, "Failed to check username")
                                        }
                                }
                            }
                            .addOnFailureListener {
                                onResult(false, "Failed to check email")
                            }
                    }
                } else {
                    onResult(false, "Failed to check email in Auth")
                }
            }
    }
}