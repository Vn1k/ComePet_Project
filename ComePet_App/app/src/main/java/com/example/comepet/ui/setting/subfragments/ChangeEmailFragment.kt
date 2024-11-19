package com.example.comepet.ui.setting.subfragments

import android.os.Bundle
import android.os.Handler
import android.text.Editable
import android.util.Log
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
import com.google.firebase.auth.EmailAuthProvider
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.CollectionReference
import com.google.firebase.firestore.FirebaseFirestore

class ChangeEmailFragment : Fragment() {

    private lateinit var changeEmailButton: Button
    private lateinit var cancelButton: ImageButton
    private lateinit var currentEmailField: TextInputEditText
    private lateinit var newEmailField: TextInputEditText
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
        cancelButton.setOnClickListener {
            findNavController().popBackStack()
        }

        currentEmailField = view.findViewById(R.id.currentEmailField)
        currentEmailField.text = Editable.Factory.getInstance().newEditable(mAuth.currentUser?.email)
        changeEmailButton = view.findViewById(R.id.changeEmailButton)
        newEmailField = view.findViewById(R.id.newEmailField)
        changeEmailButton.setOnClickListener {
            val newEmail = newEmailField.text.toString().trim()
            if (newEmail.isEmpty()) {
                newEmailField.error = "Please enter your new email"
                return@setOnClickListener
            }

            val currentUser = mAuth.currentUser
            if (currentUser == null) {
                newEmailField.error = "No authenticated user found"
                return@setOnClickListener
            }

            currentUser.verifyBeforeUpdateEmail(newEmail).addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    newEmailField.error = null
                    newEmailField.isEnabled = false
                    changeEmailButton.isEnabled = false

                    if (isAdded) {
                        Toast.makeText(
                            requireContext(),
                            "Verification email sent to $newEmail. Please verify to complete the update.",
                            Toast.LENGTH_LONG
                        ).show()
                    }

                    FirebaseAuth.getInstance().addAuthStateListener { auth ->
                        val updatedUser = auth.currentUser
                        if (updatedUser != null && updatedUser.email == newEmail) {
                            user.document(updatedUser.uid).update("email", newEmail)
                                .addOnSuccessListener {
                                    if (isAdded) {
                                        Log.d("Firestore", "Email updated in Firestore successfully.")
                                        Toast.makeText(
                                            requireContext(),
                                            "Email updated in Firestore.",
                                            Toast.LENGTH_SHORT
                                        ).show()
                                    }
                                }
                                .addOnFailureListener { e ->
                                    if (isAdded) {
                                        Log.e("Firestore", "Failed to update Firestore: ${e.localizedMessage}")
                                        Toast.makeText(
                                            requireContext(),
                                            "Failed to update Firestore.",
                                            Toast.LENGTH_SHORT
                                        ).show()
                                    }
                                }
                        }
                    }
                } else {
                    val errorMessage = task.exception?.localizedMessage ?: "Failed to update email"
                    newEmailField.error = errorMessage
                }
            }
        }
    }
}