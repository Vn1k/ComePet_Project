package com.example.comepet.ui.profile.subfragments

import android.app.Activity
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
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
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.bumptech.glide.Glide
import com.example.comepet.R
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import java.io.ByteArrayOutputStream
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

        initViews(view)
        setOnClickListeners()
        getCurrentUserData()
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
            openGallery()
        }
    }

    private fun getCurrentUserData() {
        val currentUser = mAuth.currentUser
        userDataLoadingProgressBar.visibility = View.VISIBLE

        currentUser?.let {
            db.collection("users").document(currentUser.uid).get()
                .addOnSuccessListener { document ->
                    userDataLoadingProgressBar.visibility = View.GONE
                    if (document.exists()) {
                        val name = document.getString("name") ?: ""
                        val username = document.getString("username") ?: ""
                        val phone = document.getString("phone") ?: ""
                        val bio = document.getString("bio") ?: ""
                        val location = document.getString("location") ?: ""
                        val profilePicture = document.getString("profilePicture") ?: ""

                        // Populate UI with current user data
                        editTextName.setText(name)
                        editTextUsername.setText(username)
                        editTextPhone.setText(phone)
                        editTextBio.setText(bio)
                        editTextLocation.setText(location)

                        if (profilePicture.isNotEmpty()) {
                            Glide.with(requireContext())
                                .load(profilePicture)
                                .placeholder(R.drawable.defaultprofilepicture)
                                .into(profilePictureImageView)
                        }
                    }
                }
                .addOnFailureListener { exception ->
                    userDataLoadingProgressBar.visibility = View.GONE
                    Log.e("ProfileFragment", "Error fetching user data", exception)
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
                val userDataToUpdate = hashMapOf<String, Any>(
                    "name" to name,
                    "username" to username,
                    "phone" to phone,
                    "bio" to bio,
                    "location" to location
                )

                db.collection("users")
                    .document(currentUser.uid)
                    .update(userDataToUpdate)
                    .addOnSuccessListener {
                        Toast.makeText(requireContext(), "Profile updated successfully", Toast.LENGTH_SHORT).show()
                    }
                    .addOnFailureListener { exception ->
                        Log.e("ProfileEditFragment", "Error updating user data", exception)
                        Toast.makeText(requireContext(), "Failed to update profile", Toast.LENGTH_SHORT).show()
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

    private val galleryLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
        if (result.resultCode == Activity.RESULT_OK) {
            result.data?.data?.let { uri ->
                compressAndUploadImage(uri)
            }
        }
    }

    private fun compressAndUploadImage(imageUri: Uri) {
        try {
            // Calculate image dimensions before loading full bitmap
            val options = BitmapFactory.Options().apply {
                inJustDecodeBounds = true
            }
            val inputStream = requireContext().contentResolver.openInputStream(imageUri)
            BitmapFactory.decodeStream(inputStream, null, options)
            inputStream?.close()

            // Calculate inSampleSize
            options.inSampleSize = calculateInSampleSize(options, 800, 800)
            options.inJustDecodeBounds = false

            // Decode bitmap with reduced memory footprint
            val inputStreamForBitmap = requireContext().contentResolver.openInputStream(imageUri)
            val bitmap = BitmapFactory.decodeStream(inputStreamForBitmap, null, options)
            inputStreamForBitmap?.close()

            bitmap?.let {
                // Compress bitmap to JPEG
                val outputStream = ByteArrayOutputStream()
                bitmap.compress(Bitmap.CompressFormat.JPEG, 75, outputStream)
                val compressedBytes = outputStream.toByteArray()

                // Create temp file from compressed bytes
                val tempFile = createTempFile()
                val fileOutputStream = FileOutputStream(tempFile)
                fileOutputStream.write(compressedBytes)
                fileOutputStream.close()

                val compressedUri = Uri.fromFile(tempFile)
                uploadImageToFirebase(compressedUri)
            } ?: run {
                Toast.makeText(requireContext(), "Failed to load image", Toast.LENGTH_SHORT).show()
            }
        } catch (e: Exception) {
            Toast.makeText(requireContext(), "Image compression failed", Toast.LENGTH_SHORT).show()
            Log.e("ProfileEditFragment", "Error compressing image", e)
        }
    }

    private fun calculateInSampleSize(
        options: BitmapFactory.Options,
        reqWidth: Int,
        reqHeight: Int
    ): Int {
        // Raw height and width of image
        val height = options.outHeight
        val width = options.outWidth
        var inSampleSize = 1

        if (height > reqHeight || width > reqWidth) {
            val halfHeight = height / 2
            val halfWidth = width / 2

            // Calculate the largest inSampleSize value that is a power of 2 and keeps both
            // height and width larger than the requested height and width.
            while ((halfHeight / inSampleSize) >= reqHeight && (halfWidth / inSampleSize) >= reqWidth) {
                inSampleSize *= 2
            }
        }

        return inSampleSize
    }

    private fun uploadImageToFirebase(uri: Uri) {
        uri.let {
            userDataLoadingProgressBar.visibility = View.VISIBLE
            val filename = UUID.randomUUID().toString()
            val ref: StorageReference = storage.reference.child("profile_pictures/$filename")

            ref.putFile(it)
                .addOnSuccessListener {
                    ref.downloadUrl.addOnSuccessListener { downloadUri ->
                        updateProfilePictureUrl(downloadUri.toString())
                        userDataLoadingProgressBar.visibility = View.GONE
                    }
                }
                .addOnFailureListener { exception ->
                    userDataLoadingProgressBar.visibility = View.GONE
                    Toast.makeText(requireContext(), "Failed to upload image", Toast.LENGTH_SHORT).show()
                    Log.e("ProfileEditFragment", "Error uploading image", exception)
                }
        }
    }

    private fun updateProfilePictureUrl(imageUrl: String) {
        val currentUser = mAuth.currentUser
        currentUser?.let {
            db.collection("users")
                .document(currentUser.uid)
                .update("profilePicture", imageUrl)
                .addOnSuccessListener {
                    Toast.makeText(requireContext(), "Profile picture updated", Toast.LENGTH_SHORT).show()
                    Glide.with(requireContext())
                        .load(imageUrl)
                        .into(profilePictureImageView)
                }
                .addOnFailureListener { exception ->
                    Log.e("ProfileEditFragment", "Error updating profile picture URL", exception)
                    Toast.makeText(requireContext(), "Failed to update profile picture", Toast.LENGTH_SHORT).show()
                }
        }
    }

    private fun compressBitmap(bitmap: Bitmap): Bitmap {
        val maxSize = 1024 * 1024 // 1MB
        val ratio = Math.min(
            maxSize.toFloat() / bitmap.width,
            maxSize.toFloat() / bitmap.height
        )
        val width = (bitmap.width * ratio).toInt()
        val height = (bitmap.height * ratio).toInt()
        return Bitmap.createScaledBitmap(bitmap, width, height, true)
    }

    private fun createTempFile(): File {
        val tempFile = File(requireContext().cacheDir, "${UUID.randomUUID()}.jpg")
        tempFile.createNewFile()
        return tempFile
    }

    companion object {
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            ProfileEditFragment().apply {
                arguments = Bundle().apply {
                }
            }
    }
}