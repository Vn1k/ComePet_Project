package com.example.comepet.ui.post.camera

import android.content.Intent
import android.graphics.Bitmap
import android.os.Bundle
import android.Manifest
import android.app.Activity
import android.content.pm.PackageManager
import android.os.Handler
import android.os.Looper
import android.provider.MediaStore
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.example.comepet.R
import com.example.comepet.databinding.FragmentCameraBinding
import com.example.comepet.ui.post.upload.UploadFragment
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.Blob
import com.google.firebase.firestore.FirebaseFirestore
import java.io.ByteArrayOutputStream

class CameraFragment : Fragment() {

    private lateinit var db: FirebaseFirestore
    private lateinit var auth: FirebaseAuth
    private lateinit var binding: FragmentCameraBinding

    private var capturedImageBitmap: Bitmap? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        db = FirebaseFirestore.getInstance()
        auth = FirebaseAuth.getInstance()

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate layout untuk fragment
        binding = FragmentCameraBinding.inflate(inflater, container, false)

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        requestCameraPermission()

    }

    // Camera permission
    private fun requestCameraPermission() {
        if (ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.CAMERA)
            != PackageManager.PERMISSION_GRANTED
        ) {
            ActivityCompat.requestPermissions(
                requireActivity(),
                arrayOf(Manifest.permission.CAMERA),
                CAMERA_PERMISSION_REQUEST_CODE
            )
        } else {
            openCamera()
        }
    }

    // open camera
    private val takePictureLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
        if (result.resultCode == Activity.RESULT_OK) {
            val data = result.data
            val imageBitmap = data?.extras?.get("data") as? Bitmap
            imageBitmap?.let {
                capturedImageBitmap = it
                binding.previewView.setImageBitmap(it)
                // Langsung upload setelah gambar ditampilkan
                uploadImageToFirestore()
            }
        }
    }


    private fun openCamera() {
        val intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        takePictureLauncher.launch(intent)
    }

    private fun uploadImageToFirestore() {
        capturedImageBitmap?.let { bitmap ->
            val currentUser = auth.currentUser
            if (currentUser == null) {
                Toast.makeText(requireContext(), "User not authenticated", Toast.LENGTH_SHORT)
                    .show()
                return
            }

            val userId = currentUser.uid
            val imagesCollection = db.collection("users").document(userId).collection("images")

            val imageBytes = bitmapToByteArray(bitmap)
            val imageBlob = Blob.fromBytes(imageBytes)

            val image = hashMapOf(
                "image" to imageBlob
            )

            imagesCollection.add(image)
                .addOnSuccessListener {
                    Toast.makeText(requireContext(), "Image uploaded", Toast.LENGTH_SHORT).show()
                }
                .addOnFailureListener {
                    Toast.makeText(requireContext(), "Image upload failed", Toast.LENGTH_SHORT)
                        .show()
                }
        }
    }

    // Convert Bitmap to byte array
    private fun bitmapToByteArray(bitmap: Bitmap): ByteArray {
        val byteArrayOutputStream = ByteArrayOutputStream()
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, byteArrayOutputStream)
        return byteArrayOutputStream.toByteArray()
    }

    companion object {
        private const val CAMERA_PERMISSION_REQUEST_CODE = 1001
    }

}
