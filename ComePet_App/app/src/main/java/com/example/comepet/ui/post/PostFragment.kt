package com.example.comepet.ui.post

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.Color
import android.graphics.RenderEffect
import android.graphics.Shader
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.MediaStore
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import androidx.core.os.bundleOf
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.comepet.R
import com.example.comepet.databinding.FragmentPostBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
//import android.graphics.Color
//import com.eightbitlab.blurlibrary.BlurView

class PostFragment : DialogFragment() {

    private lateinit var binding: FragmentPostBinding
    private lateinit var auth: FirebaseAuth
    private lateinit var firestore: FirebaseFirestore
    private lateinit var cameraButton: TextView
    private lateinit var galleryButton: TextView

    private val PICK_IMAGE_REQUEST_CODE = 1
    private var capturedImageUri: Uri? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        auth = FirebaseAuth.getInstance()
        firestore = FirebaseFirestore.getInstance()
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentPostBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

//        val blurView = view.findViewById<BlurView>(R.id.blurView)
//        blurView?.let {
//            it.setupWith(requireActivity().window.decorView)
//                .setBlurRadius(10f)  // Mengatur radius blur
//                .setOverlayColor(Color.parseColor("#AA000000")) // Warna overlay hitam transparan
//        }

        cameraButton = binding.cameraButton
        galleryButton = binding.galleryButton

        cameraButton.setOnClickListener {
            findNavController().navigate(R.id.navigation_post_to_navigation_camera)
        }

        galleryButton.setOnClickListener {
            openGallery()
        }

    }

    // open Gallery
    private val pickImageLauncher =
        registerForActivityResult(ActivityResultContracts.GetContent()) { uri: Uri? ->
            uri?.let {
                capturedImageUri = it // Assign the selected URI to the variable

                // Send Uri to UploadFragment using NavController
                val bundle = Bundle().apply {
                    putString("capturedImageUri", capturedImageUri.toString()) // Store Uri as string
                }

                // Navigate to UploadFragment
                findNavController().navigate(
                    R.id.navigation_post_to_navigation_upload,
                    bundle
                )
            }
        }

    private fun openGallery() {
        // Launch the image picker for the gallery
        pickImageLauncher.launch("image/*")
    }

//    override fun onStart() {
//        super.onStart()
//        val blurView = view?.findViewById<BlurView>(R.id.blurView)
//        blurView?.let {
//            it.setupWith(requireActivity().window.decorView)
//                .setBlurRadius(10f)  // Anda bisa menyesuaikan blur radius
//                .setOverlayColor(Color.parseColor("#AA000000")) // Warna transparan hitam
//        }

        // Set layout popup jauh dari navbar
//        val dialog = dialog
//        dialog?.window?.setGravity(Gravity.CENTER)
//        val layoutParams = dialog?.window?.attributes
//        layoutParams?.y = 150 // Jarak dari navbar
//        dialog?.window?.attributes = layoutParams
//    }



}