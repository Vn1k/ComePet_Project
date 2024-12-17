//package com.example.comepet.ui.profile.subfragments
//
//import android.net.Uri
//import android.os.Bundle
//import androidx.fragment.app.Fragment
//import android.view.LayoutInflater
//import android.view.View
//import android.view.ViewGroup
//import android.widget.Button
//import androidx.navigation.fragment.findNavController
//import com.canhub.cropper.CropImageView
//import com.example.comepet.R
//class CropImageFragment : Fragment() {
//    private lateinit var cropImageView: CropImageView
//    private lateinit var croppedImageUri: Uri
//
//    override fun onCreateView(
//        inflater: LayoutInflater, container: ViewGroup?,
//        savedInstanceState: Bundle?
//    ): View? {
//        // Inflate the fragment's layout
//        val binding = FragmentCropImageBinding.inflate(inflater, container, false)
//
//        cropImageView = binding.cropImageView
//
//        // Get the URI from arguments or from the parent fragment
//        val imageUri = arguments?.getParcelable<Uri>("imageUri") ?: Uri.EMPTY
//        cropImageView.setImageUriAsync(imageUri)
//
//        // Handle the crop button click
//        binding.btnCrop.setOnClickListener {
//            cropImageView.Asy()
//        }
//
//        // Handle when cropping is complete
//        cropImageView.setOnCropImageCompleteListener { view, result ->
//            croppedImageUri = result.uri
//            // Pass the cropped image URI back to ProfileEditFragment
//            setResult(croppedImageUri)
//        }
//
//        return binding.root
//    }
//
//    private fun setResult(croppedImageUri: Uri) {
//        val resultIntent = Intent().apply {
//            putExtra("croppedImageUri", croppedImageUri)
//        }
//        requireActivity().supportFragmentManager.setFragmentResult("cropImageResult", resultIntent)
//        // Close the fragment or pop it from the back stack
//        parentFragmentManager.popBackStack()
//    }
//}
