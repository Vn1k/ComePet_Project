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
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.LinearLayout
import com.bumptech.glide.Glide
import android.widget.Toast
import androidx.core.content.ContentProviderCompat.requireContext
import androidx.navigation.fragment.findNavController
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
    private lateinit var db: FirebaseFirestore
    private lateinit var auth: FirebaseAuth

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_upload, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        captureResult = view.findViewById(R.id.captureResult)

        val imageUriString = arguments?.getString("capturedImageUri")

        // Mengambil Bitmap
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

            // Handle Bitmap if available
            if (capturedImage != null) {
                val byteArrayOutputStream = ByteArrayOutputStream()
                capturedImage.compress(Bitmap.CompressFormat.JPEG, 100, byteArrayOutputStream)
                val byteArray = byteArrayOutputStream.toByteArray()

                uploadBitmapToFirebaseStorage(capturedImage, "feeds") {
                    Log.d("Navigation", "Navigating to Post Feeds")
                    findNavController().navigate(R.id.navigation_upload_to_navigation_postFeeds)
                }
            } else {
                imageUriString?.let { uriString ->
                    val imageUri = Uri.parse(uriString)
                    uploadImageToFirebaseStorage(imageUri, "feeds") {
                        Log.d("Navigation", "Navigating to Post Feeds")
                        findNavController().navigate(R.id.navigation_upload_to_navigation_postFeeds)
                    }
                }
            }
        }

        // Tombol post shelter
        postShelter.setOnClickListener {
            Toast.makeText(context, "Wait a Moment", Toast.LENGTH_SHORT).show()

            // Handle Bitmap if available
            if (capturedImage != null) {

                val byteArrayOutputStream = ByteArrayOutputStream()
                capturedImage.compress(Bitmap.CompressFormat.JPEG, 100, byteArrayOutputStream)
                val byteArray = byteArrayOutputStream.toByteArray()

                uploadBitmapToFirebaseStorage(capturedImage, "feeds") {
                    Log.d("Navigation", "Navigating to Post Feeds")
                    findNavController().navigate(R.id.navigation_upload_to_navigation_postFeeds)
                }
            } else {
                imageUriString?.let { uriString ->
                    val imageUri = Uri.parse(uriString)
                    uploadImageToFirebaseStorage(imageUri, "shelters") {
                        Log.d("Navigation", "Navigating to Post Shelter")
                        findNavController().navigate(R.id.navigation_upload_to_navigation_postShelter)
                    }
                }
            }
        }
    }

    private fun uploadBitmapToFirebaseStorage(bitmap: Bitmap, collection: String, onUploadSuccess: () -> Unit) {
        val storageReference: StorageReference = FirebaseStorage.getInstance().reference
        val filePath = storageReference.child("uploads/${System.currentTimeMillis()}.jpg")

        val baos = ByteArrayOutputStream()
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos)
        val data = baos.toByteArray()

        filePath.putBytes(data).addOnSuccessListener {
            filePath.downloadUrl.addOnSuccessListener { uri ->
                saveImageUrlToFirestore(uri.toString(), collection, onUploadSuccess)
            }.addOnFailureListener {
                Log.e("Upload", "Failed to get download URL", it)
                Toast.makeText(context, "Failed to get download URL", Toast.LENGTH_SHORT).show()
            }
        }.addOnFailureListener {
            Log.e("Upload", "Image upload failed", it)
            Toast.makeText(context, "Image upload failed", Toast.LENGTH_SHORT).show()
        }
    }

    private fun uploadImageToFirebaseStorage(imageUri: Uri, collection: String, onUploadSuccess: () -> Unit) {
        val storageReference: StorageReference = FirebaseStorage.getInstance().reference
        val filePath = storageReference.child("uploads/${System.currentTimeMillis()}.jpg")

        filePath.putFile(imageUri).addOnSuccessListener {
            filePath.downloadUrl.addOnSuccessListener { uri ->
                saveImageUrlToFirestore(uri.toString(), collection, onUploadSuccess)
            }.addOnFailureListener {
                Log.e("Upload", "Failed to get download URL", it)
                Toast.makeText(context, "Failed to get download URL", Toast.LENGTH_SHORT).show()
            }
        }.addOnFailureListener {
            Log.e("Upload", "Image upload failed", it)
            Toast.makeText(context, "Image upload failed", Toast.LENGTH_SHORT).show()
        }
    }

    private fun saveImageUrlToFirestore(downloadUrl: String, collection: String, onUploadSuccess: () -> Unit) {
        val data = hashMapOf(
            "imageUrl" to downloadUrl,
            "timestamp" to System.currentTimeMillis()
        )

        db.collection(collection).add(data)
            .addOnSuccessListener {
                Toast.makeText(context, "Upload Successfully", Toast.LENGTH_SHORT).show()
                onUploadSuccess()
            }
            .addOnFailureListener { exception ->
                Log.e("Firestore", "Failed to save image URL", exception)
                Toast.makeText(context, "Failed to save image URL", Toast.LENGTH_SHORT).show()
            }
    }

    // Method to upload Bitmap
//    private fun uploadBitmapToFirebaseStorage(bitmap: Bitmap, collection: String, onUploadSuccess: () -> Unit) {
//        val storageReference: StorageReference = FirebaseStorage.getInstance().reference
//        val filePath = storageReference.child("uploads/${System.currentTimeMillis()}.jpg")
//
//        // Convert Bitmap to ByteArray
//        val baos = ByteArrayOutputStream()
//        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos)
//        val data = baos.toByteArray()
//
//        // Upload ByteArray to Firebase Storage
//        filePath.putBytes(data).addOnSuccessListener {
//            Log.d("Upload", "Image uploaded successfully")
//            Toast.makeText(context, "Upload Successful", Toast.LENGTH_SHORT).show()
//
//            // Get download URL
//            filePath.downloadUrl.addOnSuccessListener { uri ->
//                val downloadUrl = uri.toString()
//                Log.d("Upload", "Download URL: $downloadUrl")
//                saveImageUrlToFirestore(downloadUrl, collection, onUploadSuccess)
//                onUploadSuccess() // Call callback after upload success
//            }.addOnFailureListener {
//                Log.e("Upload", "Failed to get download URL", it)
//                Toast.makeText(context, "Failed to get download URL", Toast.LENGTH_SHORT).show()
//            }
//        }.addOnFailureListener {
//            Log.e("Upload", "Image upload failed", it)
//            Toast.makeText(context, "Image upload failed", Toast.LENGTH_SHORT).show()
//        }
//    }
//
//    private fun uploadImageToFirebaseStorage(
//        imageUri: Uri,
//        collection: String,
//        onUploadSuccess: () -> Unit
//    ) {
//        val storageReference: StorageReference = FirebaseStorage.getInstance().reference
//        val filePath = storageReference.child("uploads/${System.currentTimeMillis()}.jpg")
//
//        // Mulai upload ke Firebase Storage
//        filePath.putFile(imageUri).addOnSuccessListener { taskSnapshot ->
//            Log.d("Upload", "Image uploaded successfully")
//            Toast.makeText(requireContext(), "Upload Successful", Toast.LENGTH_SHORT).show()
//
//            // Ambil URL download dari file yang di-upload
//            filePath.downloadUrl.addOnSuccessListener { uri ->
//                val downloadUrl = uri.toString()
//                Log.d("Upload", "Download URL: $downloadUrl")
//                saveImageUrlToFirestore(downloadUrl, collection, onUploadSuccess)
//            }.addOnFailureListener { exception ->
//                Log.e("Upload", "Failed to get download URL", exception)
//                Toast.makeText(requireContext(), "Failed to get download URL", Toast.LENGTH_SHORT)
//                    .show()
//            }
//        }.addOnFailureListener { exception ->
//            Log.e("Upload", "Image upload failed", exception)
//            Toast.makeText(requireContext(), "Image upload failed", Toast.LENGTH_SHORT).show()
//        }
//    }
//
//
//    private fun saveImageUrlToFirestore(
//        downloadUrl: String,
//        collection: String,
//        onUploadSuccess: () -> Unit
//    ) {
//        val data = hashMapOf(
//            "imageUrl" to downloadUrl,
//            "timestamp" to System.currentTimeMillis()
//        )
//
//        // Simpan URL gambar ke Firestore
//        db.collection(collection).add(data)
//            .addOnSuccessListener {
//                Log.d("Firestore", "Image uploaded to Firestore")
//                Toast.makeText(requireContext(), "Image uploaded to Firestore", Toast.LENGTH_SHORT)
//                    .show()
//                onUploadSuccess() // Panggil callback setelah sukses menyimpan
//            }
//            .addOnFailureListener { exception ->
//                Log.e("Firestore", "Failed to upload image to Firestore", exception)
//                Toast.makeText(
//                    requireContext(),
//                    "Failed to upload image to Firestore",
//                    Toast.LENGTH_SHORT
//                ).show()
//            }
//    }
}


