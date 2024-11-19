package com.example.comepet.ui.post.upload

import android.graphics.Bitmap
import android.net.Uri
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.navigation.fragment.findNavController
import com.bumptech.glide.Glide
import com.example.comepet.R
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import java.io.ByteArrayOutputStream

class UploadFragment : Fragment() {

    private lateinit var captureResult: ImageView
    private lateinit var backButtonToPost: ImageButton
    private lateinit var tagPet: LinearLayout
    private lateinit var addLocation: LinearLayout
    private lateinit var postFeeds: Button
    private lateinit var postShelter: Button
    private lateinit var captionEditText: EditText

    private lateinit var db: FirebaseFirestore
    private lateinit var auth: FirebaseAuth

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return inflater.inflate(R.layout.fragment_upload, container, false)
    }

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

        // Inisialisasi Firebase
        db = FirebaseFirestore.getInstance()
        auth = FirebaseAuth.getInstance()

        // Ambil data gambar dari argumen
        val capturedImage = arguments?.getParcelable<Bitmap>("capturedImage")
        val imageUriString = arguments?.getString("capturedImageUri")

        // Tampilkan gambar
        capturedImage?.let { captureResult.setImageBitmap(it) }
            ?: imageUriString?.let {
                val imageUri = Uri.parse(it)
                Glide.with(this).load(imageUri).into(captureResult)
            } ?: Toast.makeText(context, "No image received", Toast.LENGTH_SHORT).show()

        // Navigasi tombol
        backButtonToPost.setOnClickListener {
            findNavController().navigate(R.id.navigation_upload_to_navigation_post)
        }

        tagPet.setOnClickListener {
            findNavController().navigate(R.id.navigation_upload_to_navigation_tagpet)
        }

        addLocation.setOnClickListener {
            findNavController().navigate(R.id.navigation_upload_to_navigation_location)
        }

        // Tombol post feeds
        postFeeds.setOnClickListener {
            handleUpload("feeds", capturedImage, imageUriString)
        }

        // Tombol post shelter
        postShelter.setOnClickListener {
            handleUpload("shelters", capturedImage, imageUriString)
        }
    }

    private fun handleUpload(collection: String, capturedImage: Bitmap?, imageUriString: String?) {
        Toast.makeText(context, "Wait a Moment", Toast.LENGTH_SHORT).show()

        val captionText = captionEditText.text.toString()
        val userId = auth.currentUser?.uid ?: run {
            Toast.makeText(context, "User not authenticated", Toast.LENGTH_SHORT).show()
            return
        }

        if (capturedImage != null) {
            uploadBitmapToFirebaseStorage(capturedImage, collection, captionText, userId) {
                navigateToPost(collection)
            }
        } else {
            imageUriString?.let { uriString ->
                val imageUri = Uri.parse(uriString)
                uploadImageToFirebaseStorage(imageUri, collection, captionText, userId) {
                    navigateToPost(collection)
                }
            }
        }
    }

    private fun navigateToPost(collection: String) {
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
                saveImageUrlToFirestore(uri.toString(), collection, captionText, userId, onUploadSuccess)
            }
        }
    }

    private fun uploadImageToFirebaseStorage(
        imageUri: Uri,
        collection: String,
        captionText: String,
        userId: String,
        onUploadSuccess: () -> Unit
    ) {
        val storageReference = FirebaseStorage.getInstance().reference
        val filePath = storageReference.child("uploads/${System.currentTimeMillis()}.jpg")

        filePath.putFile(imageUri).addOnSuccessListener {
            filePath.downloadUrl.addOnSuccessListener { uri ->
                saveImageUrlToFirestore(uri.toString(), collection, captionText, userId, onUploadSuccess)
            }
        }
    }

    private fun saveImageUrlToFirestore(
        downloadUrl: String,
        collection: String,
        captionText: String,
        userId: String,
        onUploadSuccess: () -> Unit
    ) {
        val imagesCollection = db.collection("users").document(userId).collection(collection)

        val imageData = mapOf(
            "imageUrl" to downloadUrl,
            "caption" to captionText,
            "timestamp" to System.currentTimeMillis()
        )

        imagesCollection.add(imageData).addOnSuccessListener {
            onUploadSuccess()
        }
    }
}
