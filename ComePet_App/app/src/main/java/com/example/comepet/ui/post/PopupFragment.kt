package com.example.comepet.ui.post

import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.DialogFragment
import androidx.navigation.fragment.findNavController
import com.example.comepet.R
import com.example.comepet.databinding.FragmentPostBinding

class PopupFragment : DialogFragment() {

    private lateinit var binding: FragmentPostBinding
    private var capturedImageUri: Uri? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentPostBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Handle Camera Button
        binding.cameraButton.setOnClickListener {
            findNavController().navigate(R.id.navigation_post_to_navigation_camera)
            dismiss()  // Dismiss the PopupFragment after navigating to CameraFragment
        }

        // Handle Gallery Button
        binding.galleryButton.setOnClickListener {
            openGallery()
            dismiss()  // Dismiss the PopupFragment after opening the gallery
        }
    }

    // Function to open gallery and select an image
    private val pickImageLauncher =
        registerForActivityResult(ActivityResultContracts.GetContent()) { uri: Uri? ->
            uri?.let {
                capturedImageUri = it

                // Pass the image URI to UploadFragment
                val bundle = Bundle().apply {
                    putString("capturedImageUri", capturedImageUri.toString())
                }

                // Navigate to UploadFragment with the selected image URI
                findNavController().navigate(
                    R.id.navigation_post_to_navigation_upload,
                    bundle
                )
            }
        }

    private fun openGallery() {
        pickImageLauncher.launch("image/*")
    }

    companion object {
        fun newInstance(): PopupFragment {
            return PopupFragment()
        }
    }
}
