package com.example.comepet.ui.profile.subfragments

import android.app.Activity
import android.app.AlertDialog
import android.content.Intent
import android.graphics.Bitmap
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.ProgressBar
import android.widget.TextView
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.bumptech.glide.Glide
import com.canhub.cropper.CropImageContract
import com.canhub.cropper.CropImageContractOptions
import com.canhub.cropper.CropImageOptions
import com.canhub.cropper.CropImageView
import com.example.comepet.R
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.storage
import java.io.File
import java.io.FileOutputStream
import java.util.UUID



class ProfileEditFragment : Fragment() {
    private lateinit var saveButton: Button
    private lateinit var editTextName: EditText
    private lateinit var editTextUsername: EditText
    private lateinit var editTextPhone: EditText
    private lateinit var editTextBio: EditText
    private lateinit var editTextLocation: EditText
    private lateinit var cameraButton: ImageButton
    private lateinit var profilePictureImageView: ImageView
    private lateinit var userDataLoadingProgressBar: ProgressBar

    private lateinit var mAuth: FirebaseAuth
    private lateinit var db: FirebaseFirestore
    private lateinit var storage: FirebaseStorage

    private var selectedImageUri: Uri? = null

    private lateinit var cropImageView: CropImageView



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        initFirebase()
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View? {
        return inflater.inflate(R.layout.fragment_profile_edit, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        findNavController().currentBackStackEntry?.savedStateHandle?.getLiveData<Uri>("cropped_uri")
            ?.observe(viewLifecycleOwner) { croppedUri ->
                croppedUri?.let { compressAndUploadImage(it) }
            }

        initViews(view)
        setOnClickListeners()
        getCurrentUserData()
//        configCropImage()
    }

    private fun initFirebase() {
        mAuth = FirebaseAuth.getInstance()
        db = FirebaseFirestore.getInstance()
        storage = FirebaseStorage.getInstance()
    }

    private fun initViews(view: View){
        saveButton = view.findViewById(R.id.buttonSave)
        editTextName = view.findViewById(R.id.editTextName)
        editTextUsername = view.findViewById(R.id.editTextUsername)
        editTextPhone = view.findViewById(R.id.editTextPhone)
        editTextBio = view.findViewById(R.id.editTextBio)
        editTextLocation = view.findViewById(R.id.editTextLocation)
        cameraButton = view.findViewById(R.id.cameraButton)
        profilePictureImageView = view.findViewById(R.id.Profile_Picture)
        userDataLoadingProgressBar = view.findViewById(R.id.loadingProgressBar)

    }

    private fun setOnClickListeners(){
        saveButton.setOnClickListener {
            saveUserData()
            findNavController().navigate(R.id.navigation_edit_profile_to_navigation_profile)
        }
        cameraButton.setOnClickListener {
            showImagePickerDialog()
        }
    }

    private fun getCurrentUserData() {
        val currentUser = mAuth.currentUser
        // Show loading before network call
        userDataLoadingProgressBar.visibility = View.VISIBLE

        if (currentUser != null) {
            db.collection("users")
                .document(currentUser.uid)
                .get()
                .addOnSuccessListener { document ->
                    // Hide loading on success
                    userDataLoadingProgressBar.visibility = View.GONE
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

                        if (profilePicture.isNotEmpty()){
                            Glide.with(requireContext())
                                .load(profilePicture) // profilePicture is the URL from your database
                                .into(profilePictureImageView)
                        }
                    }
                }
                .addOnFailureListener { exception ->
                    // Hide loading on success
                    userDataLoadingProgressBar.visibility = View.GONE
                    Log.e("ProfileFragment", "Error getting user data: ", exception)
                    Toast.makeText(requireContext(), "Failed to load user data", Toast.LENGTH_SHORT).show()
                }
        }
    }

    private fun saveUserData() {
        val currentUser = mAuth.currentUser

        if (currentUser != null) {
            val name = editTextName.text.toString()
            val username = editTextUsername.text.toString()
            val phone = editTextPhone.text.toString()
            val bio = editTextBio.text.toString()
            val location = editTextLocation.text.toString()

            if (validateInput(name, username, phone, bio, location)) {
                val userDataToUpdate = hashMapOf(
                    "name" to name,
                    "username" to username,
                    "phone" to phone,
                    "bio" to bio,
                    "location" to location
                )

                db.collection("users")
                    .document(currentUser.uid)
                    .set(userDataToUpdate)
                    .addOnSuccessListener {
                        Log.d("ProfileEditFragment", "User data saved successfully")
                        Toast.makeText(requireContext(), "Profile updated successfully", Toast.LENGTH_SHORT).show()
                    }
                    .addOnFailureListener { exception ->
                        Log.e("ProfileEditFragment", "Error saving user data: ", exception)
                        Toast.makeText(requireContext(), "Failed to update profile. Please try again.", Toast.LENGTH_SHORT).show()
                    }
            }
        }
    }

    private fun validateInput(name: String, username: String, phone: String, bio: String, location: String): Boolean {
        if (name.isEmpty() || username.isEmpty() || phone.isEmpty() || location.isEmpty()) {
            Toast.makeText(requireContext(), "Please fill in all required fields", Toast.LENGTH_SHORT).show()
            return false
        }

        if (phone.length < 10) {
            Toast.makeText(requireContext(), "Invalid phone number. Please enter a 10-digit number", Toast.LENGTH_SHORT).show()
            return false
        }

        if (bio.length > 200) {
            Toast.makeText(requireContext(), "Bio is too long. Please limit to 200 characters", Toast.LENGTH_SHORT).show()
            return false
        }

        return true
    }

    private fun openGallery() {
        val intent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
        galleryLauncher.launch(intent)
    }

    private fun uploadImageToFirebase() {
        selectedImageUri?.let { uri ->
            // Show loading indicator
            userDataLoadingProgressBar.visibility = View.VISIBLE

            val filename = UUID.randomUUID().toString()
            val ref = storage.reference.child("profile_pictures/$filename")

            ref.putFile(uri)
                .addOnSuccessListener {
                    ref.downloadUrl.addOnSuccessListener { downloadUri ->
                        updateProfilePictureUrl(downloadUri.toString())
                        userDataLoadingProgressBar.visibility = View.GONE
                    }
                }
                .addOnFailureListener { e ->
                    userDataLoadingProgressBar.visibility = View.GONE
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
                    // Use Glide to load the image
                    Glide.with(requireContext())
                        .load(imageUrl)
                        .into(profilePictureImageView)
                }
                .addOnFailureListener { exception ->
                    Log.e("ProfileEditFragment", "Error updating profile picture URL: ", exception)
                    Toast.makeText(requireContext(), "Failed to update profile picture. Please try again.", Toast.LENGTH_SHORT).show()
                }
        }
    }


    private val galleryLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
        if (result.resultCode == Activity.RESULT_OK) {
            result.data?.data?.let { uri ->
                startCrop(uri)
            }
        }
    }


    //    Cropping and compressing image
    private val cropImageLauncher = registerForActivityResult(CropImageContract()) { result ->
        if (result.isSuccessful) {
            val croppedImageUri = result.uriContent
            val croppedImageFilePath = result.getUriFilePath(requireContext()) // optional
            croppedImageUri?.let { compressAndUploadImage(it) }
        } else {
            val exception = result.error
            Toast.makeText(requireContext(), "Crop failed: ${exception?.message}", Toast.LENGTH_SHORT).show()
        }
    }

    private fun showImagePickerDialog() {
        val intent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
        galleryLauncher.launch(intent)
    }

    private fun startCrop(sourceUri: Uri) {
        val bundle = Bundle().apply {
            putParcelable("IMAGE_URI", sourceUri)
        }
        findNavController().navigate(R.id.navigation_edit_profile_to_navigation_crop)
    }


    private fun compressAndUploadImage(imageUri: Uri) {
        try {
            val bitmap = MediaStore.Images.Media.getBitmap(requireContext().contentResolver, imageUri)
            val compressedBitmap = Bitmap.createScaledBitmap(bitmap, 500, 500, true)

            val compressedFile = File(requireContext().cacheDir, "compressed_profile_pic.jpg")
            val outputStream = FileOutputStream(compressedFile)
            compressedBitmap.compress(Bitmap.CompressFormat.JPEG, 75, outputStream)
            outputStream.close()

            val compressedUri = Uri.fromFile(compressedFile)
            uploadImageToFirebase(compressedUri)
        } catch (e: Exception) {
            Log.e("ProfileEditFragment", "Image compression error", e)
            Toast.makeText(requireContext(), "Failed to process image", Toast.LENGTH_SHORT).show()
        }
    }

    private fun uploadImageToFirebase(uri: Uri) {
        val filename = UUID.randomUUID().toString()
        val ref = Firebase.storage.reference.child("profile_pictures/$filename")

        userDataLoadingProgressBar.visibility = View.VISIBLE

        ref.putFile(uri)
            .addOnSuccessListener {
                ref.downloadUrl.addOnSuccessListener { downloadUri ->
                    updateProfilePictureUrl(downloadUri.toString())
                    userDataLoadingProgressBar.visibility = View.GONE
                }
            }
            .addOnFailureListener { e ->
                userDataLoadingProgressBar.visibility = View.GONE
                Toast.makeText(requireContext(), "Failed to upload image", Toast.LENGTH_SHORT).show()
                Log.e("ProfileEditFragment", "Error uploading image", e)
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