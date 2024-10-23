package com.example.comepet.ui.post.upload

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.Toast
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.comepet.R
import com.example.comepet.databinding.FragmentUploadBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.Blob
import com.google.firebase.firestore.FirebaseFirestore
import java.io.ByteArrayOutputStream


class UploadFragment : Fragment() {

    private lateinit var captureResult: ImageView
    private lateinit var backButtonToPost : ImageButton

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_upload, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Temukan ImageView dari layout fragment_upload.xml
        captureResult = view.findViewById(R.id.captureResult)

        // Periksa apakah ada gambar yang dikirim melalui Bundle
        val capturedImage = arguments?.getParcelable<Bitmap>("capturedImage")

        // Jika ada gambar yang dikirim, tampilkan di ImageView
        capturedImage?.let {
            captureResult.setImageBitmap(it)
        }

        backButtonToPost = view.findViewById(R.id.backButtonToPost)

        backButtonToPost.setOnClickListener {
            findNavController().navigate(R.id.navigation_upload_to_navigation_post)
        }

    }

}