package com.example.comepet.ui.profile.subfragments

import android.widget.Button
import androidx.navigation.fragment.findNavController
import com.example.comepet.R
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.ImageButton
import android.widget.TextView
import androidx.fragment.app.Fragment
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore



class ProfileEditFragment : Fragment() {
    private lateinit var saveButton: Button
    private lateinit var editTextName: EditText
    private lateinit var editTextUsername: EditText
    private lateinit var editTextPhone: EditText
    private lateinit var editTextBio: EditText
    private lateinit var editTextLocation: EditText
    private lateinit var cameraButton: ImageButton

    private lateinit var mAuth: FirebaseAuth
    private lateinit var db: FirebaseFirestore


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mAuth = FirebaseAuth.getInstance()
        db = FirebaseFirestore.getInstance()
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_profile_edit, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        saveButton = view.findViewById(R.id.buttonSave)
        saveButton.setOnClickListener {
            saveUserData()
            findNavController().navigate(R.id.navigation_edit_profile_to_navigation_profile)
        }
        cameraButton.setOnClickListener {

        }

        editTextName = view.findViewById(R.id.editTextName)
        editTextUsername = view.findViewById(R.id.editTextUsername)
        editTextPhone = view.findViewById(R.id.editTextPhone)
        editTextBio = view.findViewById(R.id.editTextBio)
        editTextLocation = view.findViewById(R.id.editTextLocation)
        getCurrentUserData()

    }

    private fun getCurrentUserData() {
        val currentUser = mAuth.currentUser

        if (currentUser != null) {
            db.collection("users")
                .document(currentUser.uid)
                .get()
                .addOnSuccessListener { document ->
                    if (document != null && document.exists()) {
                        // Retrieve the fields
                        val name = document.getString("name") ?: ""
                        val username = document.getString("username") ?: ""
                        val phone = document.getString("phone") ?: ""
                        val bio = document.getString("bio") ?: ""
                        val location = document.getString("location") ?: ""
                        val profilePicture = document.getString("profilePicture") ?: ""

                        // Update the UI
                        editTextName.setText(name)
                        editTextUsername.setText(username)
                        editTextPhone.setText(phone)
                        editTextBio.setText(bio)
                        editTextLocation.setText(location)

                    }
                }
                .addOnFailureListener { exception ->
                    Log.e("ProfileFragment", "Error getting user data: ", exception)
                }
        }
    }

    private fun saveUserData(){
        val currentUser = mAuth.currentUser

        if (currentUser != null) {
            val name = editTextName.text.toString()
            val username = editTextUsername.text.toString()
            val phone = editTextPhone.text.toString()
            val bio = editTextBio.text.toString()
            val location = editTextLocation.text.toString()

            val user = hashMapOf(
                "name" to name,
                "username" to username,
                "phone" to phone,
                "bio" to bio,
                "location" to location
            )

            db.collection("users")
                .document(currentUser.uid)
                .set(user)
                .addOnSuccessListener {
                    Log.d("ProfileEditFragment", "User data saved")
                }
                .addOnFailureListener { exception ->
                    Log.e("ProfileEditFragment", "Error saving user data: ", exception)
                }
        }
    }

    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment ProfileEditFragment.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            ProfileEditFragment().apply {
                arguments = Bundle().apply {

                }
            }
    }
}