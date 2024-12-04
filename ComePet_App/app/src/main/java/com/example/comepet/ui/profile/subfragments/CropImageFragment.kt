package com.example.comepet.ui.profile.subfragments

import android.net.Uri
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.navigation.fragment.findNavController
import com.canhub.cropper.CropImageView
import com.example.comepet.R
class CropImageFragment : Fragment() {
    private lateinit var cropImageView: CropImageView
    private lateinit var cropButton: Button
    private var onCropCompleteListener: ((Uri?) -> Unit)? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_crop_image, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val imageUri = arguments?.getParcelable<Uri>("IMAGE_URI")
        imageUri?.let {
            cropImageView.setImageUriAsync(it)

            cropImageView.setAspectRatio(1, 1)
            cropImageView.setFixedAspectRatio(true)
            cropImageView.guidelines = CropImageView.Guidelines.ON
        }

        cropButton.setOnClickListener {
            cropImageView.croppedImageAsync()
        }

        cropImageView.setOnCropImageCompleteListener { _, result ->
            if (result.isSuccessful) {
                findNavController().previousBackStackEntry?.savedStateHandle?.set("cropped_uri", result.uriContent)
                findNavController().navigateUp()
            }
        }
    }

    fun setOnCropCompleteListener(listener: (Uri?) -> Unit) {
        onCropCompleteListener = listener
    }

    companion object {
        fun newInstance(imageUri: Uri): CropImageFragment {
            val fragment = CropImageFragment()
            val args = Bundle().apply {
                putParcelable("IMAGE_URI", imageUri)
            }
            fragment.arguments = args
            return fragment
        }
    }
}