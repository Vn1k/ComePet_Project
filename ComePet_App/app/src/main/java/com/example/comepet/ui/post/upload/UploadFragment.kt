package com.example.comepet.ui.post.upload

import android.annotation.SuppressLint
import android.graphics.Bitmap
import android.net.Uri
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.activity.addCallback
import androidx.core.widget.addTextChangedListener
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.NavOptions
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.bumptech.glide.Glide
import com.example.comepet.R
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import java.io.ByteArrayOutputStream
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class UploadFragment : Fragment() {

    private lateinit var captureResult: ImageView
    private lateinit var backButtonToPost: ImageButton
    private lateinit var tagPet: LinearLayout
    private lateinit var addLocation: LinearLayout
    private lateinit var postFeeds: Button
    private lateinit var postShelter: Button
    private lateinit var captionEditText: EditText
    private var selectedLocation: String? = null
    private lateinit var selectedPetNameTextView: TextView
    private lateinit var selectedPetImageView: ImageView
    private lateinit var selectedPetTypeTextView: TextView

    private lateinit var uploadViewModel: UploadViewModel

    private lateinit var db: FirebaseFirestore
    private lateinit var auth: FirebaseAuth

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return inflater.inflate(R.layout.fragment_upload, container, false)
    }

    @SuppressLint("CutPasteId")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Inisialisasi view
        captionEditText = view.findViewById(R.id.caption)
        captureResult = view.findViewById(R.id.captureResult)
        backButtonToPost = view.findViewById(R.id.backButtonToPost)
        tagPet = view.findViewById(R.id.tagPet)
        addLocation = view.findViewById(R.id.addLocation)
        postFeeds = view.findViewById(R.id.postFeeds)
        postShelter = view.findViewById(R.id.postShelter)
        uploadViewModel = ViewModelProvider(requireActivity()).get(UploadViewModel::class.java)

        selectedPetNameTextView = view.findViewById(R.id.petSelectedName)
        selectedPetImageView = view.findViewById(R.id.petSelectedImage)

        // Inisialisasi Firebase
        db = FirebaseFirestore.getInstance()
        auth = FirebaseAuth.getInstance()

        uploadViewModel.selectedImageBitmap?.let {
            captureResult.setImageBitmap(it)
        } ?: uploadViewModel.selectedImageUri?.let {
            val imageUri = Uri.parse(it)
            Glide.with(this).load(imageUri).into(captureResult)
        }
//        uploadViewModel.caption?.let { captionEditText.setText(it) }
        uploadViewModel.selectedLocation?.let {
            view.findViewById<TextView>(R.id.selectedLocationText).text = it
        }

        // Ambil data gambar dari argumen (hanya jika pertama kali dibuka)
        if (uploadViewModel.selectedImageBitmap == null && uploadViewModel.selectedImageUri == null) {
            arguments?.getParcelable<Bitmap>("capturedImage")?.let { bitmap ->
                uploadViewModel.selectedImageBitmap = bitmap
                captureResult.setImageBitmap(bitmap)
            } ?: arguments?.getString("capturedImageUri")?.let { uriString ->
                uploadViewModel.selectedImageUri = uriString
                Glide.with(this).load(Uri.parse(uriString)).into(captureResult)
            } ?: Toast.makeText(context, "No image received", Toast.LENGTH_SHORT).show()
        }

        val petSelectedNameTextView: TextView = view.findViewById(R.id.petSelectedName)
        val petSelectedProfilePicture: ImageView = view.findViewById(R.id.petSelectedImage)

        parentFragmentManager.setFragmentResultListener("SELECTED_PET_REQUEST", viewLifecycleOwner) { _, bundle ->
            val petName = bundle.getString("selectedPetName")
            val petImageUrl = bundle.getString("selectedPetProfilePicture")

            if (petName != null && petImageUrl != null) {
                selectedPetNameTextView.text = petName
                Glide.with(requireContext()).load(petImageUrl).into(selectedPetImageView)
                uploadViewModel.selectedPetName = petName
            } else {
                Log.e("UploadFragment", "Data pet tidak lengkap atau null")
            }
            // Debug log untuk menampilkan informasi pet yang diterima
            Log.d("UploadFragment", "Pet received: Name = $petName, Image URL = $petImageUrl")

            petSelectedNameTextView.text = petName
            petSelectedProfilePicture.setImageURI(Uri.parse(petImageUrl))
        }

        parentFragmentManager.setFragmentResultListener("SELECTED_LOCATION_REQUEST", viewLifecycleOwner) { _, bundle ->
            val location = bundle.getString("selectedLocation")
            Log.d("UploadFragment", "Location received: $location")

            // If location is not null, update the TextView and save it in the ViewModel
            if (location != null) {
                val locationTextView: TextView = view.findViewById(R.id.locationSelectedName)
                locationTextView.text = location

                // Save the location in the ViewModel for later use during upload
                uploadViewModel.selectedLocation = location
            }
        }

        uploadViewModel.selectedPetName?.let { selectedPetNameTextView.text = it }
        uploadViewModel.selectedLocation?.let { view.findViewById<TextView>(R.id.selectedLocationText).text = it }

        backButtonToPost.setOnClickListener {
            uploadViewModel.resetSelectedImage()
            val sourceFragment = arguments?.getInt("sourceFragment", R.id.navigation_home) ?: R.id.navigation_home
            findNavController().popBackStack(sourceFragment, false)
        }

        tagPet.setOnClickListener {
            findNavController().navigate(R.id.navigation_upload_to_navigation_tagpet)
        }

        addLocation.setOnClickListener {
            findNavController().navigate(R.id.navigation_upload_to_navigation_location)
        }

        // Tombol post feeds
        postFeeds.setOnClickListener {
            handleUpload("feeds", uploadViewModel.selectedImageBitmap, uploadViewModel.selectedImageUri)
        }

        // Tombol post shelter
        postShelter.setOnClickListener {
            handleUpload("shelters", uploadViewModel.selectedImageBitmap, uploadViewModel.selectedImageUri)
        }

        addLocation.setOnClickListener {
            findNavController().navigate(R.id.navigation_upload_to_navigation_location)
        }

        captionEditText.addTextChangedListener {
            uploadViewModel.caption = it.toString()
        }

    }

    private fun handleUpload(collection: String, capturedImage: Bitmap?, imageUriString: String?) {
        Toast.makeText(context, "Wait a Moment", Toast.LENGTH_SHORT).show()

        val captionText = captionEditText.text.toString()
        val userId = auth.currentUser?.uid ?: run {
            Toast.makeText(context, "User not authenticated", Toast.LENGTH_SHORT).show()
            return
        }

        val petSelectedName = selectedPetNameTextView.text.toString()
        val selectedLocationText = uploadViewModel.selectedLocation ?: "..."

        val bitmap = uploadViewModel.selectedImageBitmap
        val uri = uploadViewModel.selectedImageUri?.let { Uri.parse(it) }

        if (bitmap != null) {
            uploadBitmapToFirebaseStorage(bitmap, collection, captionText, petSelectedName, selectedLocationText, userId) {
                navigateToPost(collection)
            }
        } else if (uri != null) {
            uploadImageToFirebaseStorage(uri, collection, captionText, petSelectedName, selectedLocationText, userId) {
                navigateToPost(collection)
            }
        } else {
            Toast.makeText(context, "No image to upload", Toast.LENGTH_SHORT).show()
        }
    }

    private fun navigateToPost(collection: String) {
        uploadViewModel.resetSelectedImage()
        val destination = if (collection == "feeds") {
            R.id.navigation_upload_to_navigation_postFeeds
        } else {
            R.id.navigation_upload_to_navigation_postShelter
        }
        findNavController().navigate(destination)
    }

    private fun uploadBitmapToFirebaseStorage(
        bitmap: Bitmap,
        collection: String,
        captionText: String,
        petSelectedName: String,
        selectedLocationText: String,
        userId: String,
        onUploadSuccess: () -> Unit
    ) {
        val storageReference = FirebaseStorage.getInstance().reference
        val filePath = storageReference.child("uploads/${System.currentTimeMillis()}.jpg")

        val baos = ByteArrayOutputStream()
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos)
        val data = baos.toByteArray()

        filePath.putBytes(data).addOnSuccessListener {
            filePath.downloadUrl.addOnSuccessListener { uri ->
                saveImageUrlToFirestore(uri.toString(), collection, captionText, userId, petSelectedName, selectedLocationText, onUploadSuccess)
            }
        }
    }

    private fun uploadImageToFirebaseStorage(
        imageUri: Uri,
        collection: String,
        captionText: String,
        petSelectedName: String,
        selectedLocationText: String,
        userId: String,
        onUploadSuccess: () -> Unit
    ) {
        val storageReference = FirebaseStorage.getInstance().reference
        val filePath = storageReference.child("uploads/${System.currentTimeMillis()}.jpg")

        filePath.putFile(imageUri).addOnSuccessListener {
            filePath.downloadUrl.addOnSuccessListener { uri ->
                saveImageUrlToFirestore(uri.toString(), collection, captionText, userId, petSelectedName, selectedLocationText, onUploadSuccess)
            }
        }
    }

    private fun saveImageUrlToFirestore(
        downloadUrl: String,
        collection: String,
        captionText: String,
        userId: String,
        petSelectedName: String?,
        selectedLocationText: String?,
        onUploadSuccess: () -> Unit
    ) {

        val imagesCollection = db.collection("users").document(userId).collection(collection)

        val date = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault()).format(Date())

        val postData = mapOf(
            "imageUrl" to downloadUrl,
            "caption" to captionText,
            "date" to date,
            "petName" to (petSelectedName ?: "Unknown"),
            "location" to (selectedLocationText ?: "Unknown")
        )

        imagesCollection
            .add(postData)
            .addOnSuccessListener {
                Toast.makeText(context, "Post uploaded successfully!", Toast.LENGTH_SHORT).show()
                onUploadSuccess()
            }
            .addOnFailureListener { e ->
                Toast.makeText(context, "Error uploading post: ${e.message}", Toast.LENGTH_SHORT).show()
                Log.e("UploadFragment", "Error uploading post", e)
            }
    }
}
