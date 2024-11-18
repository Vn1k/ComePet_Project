package com.example.comepet.ui.setting.subfragments

import android.os.Bundle
import android.os.Handler
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageButton
import androidx.navigation.fragment.findNavController
import com.example.comepet.R
import com.google.android.material.textfield.TextInputEditText
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.CollectionReference
import com.google.firebase.firestore.FirebaseFirestore

class ChangeEmailFragment : Fragment() {

    private lateinit var changeEmailButton: Button
    private lateinit var cancelButton: ImageButton
    private lateinit var currentEmailField: TextInputEditText
    private lateinit var newEmailField: TextInputEditText
    private lateinit var passwordField: TextInputEditText
    private lateinit var mAuth: FirebaseAuth
    private lateinit var db: FirebaseFirestore
    private lateinit var user: CollectionReference

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mAuth = FirebaseAuth.getInstance()
        db = FirebaseFirestore.getInstance()
        user = db.collection("users")
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_change_email, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        cancelButton = view.findViewById(R.id.cancelButton)
        cancelButton.setOnClickListener{
            findNavController().popBackStack()
        }

        currentEmailField = view.findViewById(R.id.currentEmailField)
        newEmailField = view.findViewById(R.id.newEmailField)
        passwordField = view.findViewById(R.id.passwordField)
        changeEmailButton = view.findViewById(R.id.changeEmailButton)
        changeEmailButton.setOnClickListener {
            val currentEmail = currentEmailField.text.toString().trim()
            val newEmail = newEmailField.text.toString().trim()
            val password = passwordField.text.toString().trim()

            if(currentEmail.isEmpty()){
                currentEmailField.error = "Please enter your current email"
                return@setOnClickListener
            }
            if(password.isEmpty()){
                passwordField.error = "Please enter your password"
                return@setOnClickListener
            }
            if(newEmail.isEmpty()){
                newEmailField.error = "Please enter your new email"
                return@setOnClickListener
            }
        }
    }
}