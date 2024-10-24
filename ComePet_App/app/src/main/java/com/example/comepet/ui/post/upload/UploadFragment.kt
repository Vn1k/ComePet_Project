package com.example.comepet.ui.post.upload

import android.graphics.Bitmap
import android.net.Uri
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.LinearLayout
import com.bumptech.glide.Glide
import android.widget.Toast
import androidx.core.content.ContentProviderCompat.requireContext
import androidx.navigation.fragment.findNavController
import com.example.comepet.R
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.Blob
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
    private lateinit var db: FirebaseFirestore
    private lateinit var auth: FirebaseAuth
    private lateinit var captionEditText: EditText

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_upload, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        captionEditText = view.findViewById(R.id.caption)
        captureResult = view.findViewById(R.id.captureResult)

        val imageUriString = arguments?.getString("capturedImageUri")
        val capturedImage = arguments?.getParcelable<Bitmap>("capturedImage")

        if (capturedImage != null) {
            // Tampilkan bitmap di ImageView
            captureResult.setImageBitmap(capturedImage)
        } else {
            // Mengambil URI dari argumen
            val imageUriString = arguments?.getString("capturedImageUri")
            if (!imageUriString.isNullOrEmpty()) {
                // Tampilkan gambar menggunakan Glide
                val imageUri = Uri.parse(imageUriString)
                Glide.with(this).load(imageUri).into(captureResult)
            } else {
                Toast.makeText(context, "No image received", Toast.LENGTH_SHORT).show()
            }
        }


        backButtonToPost = view.findViewById(R.id.backButtonToPost)
        tagPet = view.findViewById(R.id.tagPet)
        addLocation = view.findViewById(R.id.addLocation)
        postFeeds = view.findViewById(R.id.postFeeds)
        postShelter = view.findViewById(R.id.postShelter)

        // Inisialisasi Firestore dan Auth
        db = FirebaseFirestore.getInstance()
        auth = FirebaseAuth.getInstance()

        // Tombol kembali ke halaman post
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
            Toast.makeText(context, "Wait a Moment", Toast.LENGTH_SHORT).show()

            val captionText = captionEditText.text.toString()

            // Handle Bitmap if available
            if (capturedImage != null) {
                uploadBitmapToFirebaseStorage(capturedImage, "feeds", captionText) {
                    Log.d("Navigation", "Navigating to Post Feeds")
                    findNavController().navigate(R.id.navigation_upload_to_navigation_postFeeds)
                }
            } else {
                imageUriString?.let { uriString ->
                    val imageUri = Uri.parse(uriString)
                    uploadImageToFirebaseStorage(imageUri, "feeds", captionText) {
                        Log.d("Navigation", "Navigating to Post Feeds")
                        findNavController().navigate(R.id.navigation_upload_to_navigation_postFeeds)
                    }
                }
            }
        }

        // Tombol post shelter
        postShelter.setOnClickListener {
            Toast.makeText(context, "Wait a Moment", Toast.LENGTH_SHORT).show()

            val captionText = captionEditText.text.toString()

            // Handle Bitmap if available
            if (capturedImage != null) {
                uploadBitmapToFirebaseStorage(capturedImage, "shelters", captionText) {
                    Log.d("Navigation", "Navigating to Post Feeds")
                    findNavController().navigate(R.id.navigation_upload_to_navigation_postShelter)
                }
            } else {
                imageUriString?.let { uriString ->
                    val imageUri = Uri.parse(uriString)
                    uploadImageToFirebaseStorage(imageUri, "shelters", captionText) {
                        Log.d("Navigation", "Navigating to Post Shelter")
                        findNavController().navigate(R.id.navigation_upload_to_navigation_postShelter)
                    }
                }
            }
        }
    }

    private fun uploadBitmapToFirebaseStorage(bitmap: Bitmap, collection: String, captionText: String, onUploadSuccess: () -> Unit) {
        val storageReference: StorageReference = FirebaseStorage.getInstance().reference
        val filePath = storageReference.child("uploads/${System.currentTimeMillis()}.jpg")
        Log.d("Upload", "Uploading to collection: $collection")

        val baos = ByteArrayOutputStream()
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos)
        val data = baos.toByteArray()

        filePath.putBytes(data).addOnSuccessListener {
            filePath.downloadUrl.addOnSuccessListener { uri ->
                Log.d("Upload", "Image uploaded successfully, URL: $uri")
                // Simpan URL dan caption ke Firestore
                saveImageUrlToFirestore(uri.toString(), collection, captionText, onUploadSuccess)
            }.addOnFailureListener {
                Log.e("Upload", "Failed to get download URL", it)
                Toast.makeText(context, "Failed to get download URL", Toast.LENGTH_SHORT).show()
            }
        }.addOnFailureListener {
            Log.e("Upload", "Image upload failed", it)
            Toast.makeText(context, "Image upload failed", Toast.LENGTH_SHORT).show()
        }
    }

    private fun uploadImageToFirebaseStorage(imageUri: Uri, collection: String, captionText: String, onUploadSuccess: () -> Unit) {
        val storageReference: StorageReference = FirebaseStorage.getInstance().reference
        val filePath = storageReference.child("uploads/${System.currentTimeMillis()}.jpg")
        Log.d("Upload", "Uploading to collection: $collection")

        filePath.putFile(imageUri).addOnSuccessListener {
            filePath.downloadUrl.addOnSuccessListener { uri ->
                Log.d("Upload", "Image uploaded successfully, URL: $uri")
                // Simpan URL dan caption ke Firestore
                saveImageUrlToFirestore(uri.toString(), collection, captionText, onUploadSuccess)
            }.addOnFailureListener {
                Log.e("Upload", "Failed to get download URL", it)
                Toast.makeText(context, "Failed to get download URL", Toast.LENGTH_SHORT).show()
            }
        }.addOnFailureListener {
            Log.e("Upload", "Image upload failed", it)
            Toast.makeText(context, "Image upload failed", Toast.LENGTH_SHORT).show()
        }
    }

    private fun saveImageUrlToFirestore(downloadUrl: String, collection: String, captionText: String, onUploadSuccess: () -> Unit) {

        val currentUser = auth.currentUser
        if (currentUser == null) {
            Toast.makeText(requireContext(), "User not authenticated", Toast.LENGTH_SHORT).show()
            return
        }

        val userId = currentUser.uid
        val imagesCollection = db.collection("users").document(userId).collection(collection)

        val imageData = mapOf(
            "imageUrl" to downloadUrl,
            "caption" to captionText,
            "timestamp" to System.currentTimeMillis()
        )

        imagesCollection.add(imageData)
            .addOnSuccessListener {
                Toast.makeText(requireContext(), "Image uploaded successfully", Toast.LENGTH_SHORT).show()
                onUploadSuccess()
            }
            .addOnFailureListener { exception ->
                Log.e("Firestore", "Failed to save image URL", exception)
                Toast.makeText(requireContext(), "Failed to save image URL", Toast.LENGTH_SHORT).show()
            }
    }

    private fun bitmapToByteArray(bitmap: Bitmap): ByteArray {
        val byteArrayOutputStream = ByteArrayOutputStream()
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, byteArrayOutputStream)
        return byteArrayOutputStream.toByteArray()
    }

}


