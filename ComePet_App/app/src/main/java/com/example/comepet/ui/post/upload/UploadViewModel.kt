package com.example.comepet.ui.post.upload

import androidx.lifecycle.ViewModel
import android.graphics.Bitmap

class UploadViewModel : ViewModel() {
    var selectedImageBitmap: Bitmap? = null
    var selectedImageUri: String? = null
    var selectedLocation: String? = null
    var caption: String? = null
    var selectedPetName: String? = null

    fun resetSelectedImage() {
        selectedImageBitmap = null
        selectedImageUri = null
    }

    fun resetLocationAndPetName() {
        selectedLocation = null
        selectedPetName = null
    }

}
