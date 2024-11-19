package com.example.comepet.ui.profile.subfragments

import android.app.Activity
import android.app.AlertDialog
import android.content.Intent
import android.net.Uri
import android.widget.Button
import androidx.navigation.fragment.findNavController
import com.example.comepet.R
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.ImageButton
import android.widget.TextView
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.Fragment
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import java.util.UUID


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
    private lateinit var storage: FirebaseStorage

    private var selectedImageUri: Uri? = null

    // Register activity result launchers
    private val galleryLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
        if (result.resultCode == Activity.RESULT_OK) {
            selectedImageUri = result.data?.data
            uploadImageToFirebase()
        }
    }

    private val cameraLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
        if (result.resultCode == Activity.RESULT_OK) {
            selectedImageUri = result.data?.data
            uploadImageToFirebase()
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mAuth = FirebaseAuth.getInstance()
        db = FirebaseFirestore.getInstance()
        storage = FirebaseStorage.getInstance()
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
        cameraButton = view.findViewById(R.id.cameraButton)
        saveButton.setOnClickListener {
            saveUserData()
            findNavController().navigate(R.id.navigation_edit_profile_to_navigation_profile)
        }
        cameraButton.setOnClickListener {
            showImagePickerDialog()
        }

        editTextName = view.findViewById(R.id.editTextName)
        editTextUsername = view.findViewById(R.id.editTextUsername)
        editTextPhone = view.findViewById(R.id.editTextPhone)
        editTextBio = view.findViewById(R.id.editTextBio)
        editTextLocation = view.findViewById(R.id.editTextLocation)
        getCurrentUserData()

        cameraButton.setOnClickListener {
            showImagePickerDialog()
        }
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

    private fun showImagePickerDialog(){
        val options = arrayOf("Take Photo", "Choose from Gallery")
        AlertDialog.Builder(requireContext())
            .setTitle("Choose Profile Picture")
            .setItems(options) { _, which ->
                when (which) {
                    0 -> openCamera()
                    1 -> openGallery()
                }
            }
            .show()
    }

    private fun openCamera() {
        val intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        cameraLauncher.launch(intent)
    }

    private fun openGallery() {
        val intent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
        galleryLauncher.launch(intent)
    }

    private fun uploadImageToFirebase(){
        selectedImageUri?.let { uri ->
            // Show loading indicator
            // You might want to add a ProgressBar to your layout and show it here

            val filename = UUID.randomUUID().toString()
            val ref = storage.reference.child("profile_pictures/$filename")

            ref.putFile(uri)
                .addOnSuccessListener {
                    // Get the download URL
                    ref.downloadUrl.addOnSuccessListener { downloadUri ->
                        // Update user's profile picture URL in Firestore
                        updateProfilePictureUrl(downloadUri.toString())
                    }
                }
                .addOnFailureListener { e ->
                    Toast.makeText(requireContext(), "Failed to upload image", Toast.LENGTH_SHORT).show()
                    Log.e("ProfileEditFragment", "Error uploading image", e)
                }
        }

    }

    private fun updateProfilePictureUrl(imageUrl: String) {
        val currentUser = mAuth.currentUser

        if (currentUser != null) {
            db.collection("users")
                .document(currentUser.uid)
                .update("profilePicture", imageUrl)
                .addOnSuccessListener {
                    Toast.makeText(requireContext(), "Profile picture updated", Toast.LENGTH_SHORT).show()
                }
                .addOnFailureListener { e ->
                    Toast.makeText(requireContext(), "Failed to update profile picture", Toast.LENGTH_SHORT).show()
                    Log.e("ProfileEditFragment", "Error updating profile picture URL", e)
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