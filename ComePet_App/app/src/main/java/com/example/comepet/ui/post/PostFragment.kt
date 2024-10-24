package com.example.comepet.ui.post

import android.Manifest
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.comepet.R
import com.example.comepet.databinding.FragmentPostBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class PostFragment : Fragment() {

    private lateinit var binding: FragmentPostBinding
    private lateinit var auth: FirebaseAuth
    private lateinit var firestore: FirebaseFirestore
    private lateinit var cameraButton: ImageButton
    private lateinit var backButtonToHome: ImageButton
    private lateinit var nextButton: TextView

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

        cameraButton = binding.cameraButton
        backButtonToHome = binding.backButtonToHome
        nextButton = binding.nextButton

        cameraButton.setOnClickListener {
            findNavController().navigate(R.id.navigation_post_to_navigation_camera)
        }

        backButtonToHome.setOnClickListener {
            findNavController().navigate(R.id.navigation_post_to_navigation_home)
        }

        nextButton.setOnClickListener {
            findNavController().navigate(R.id.navigation_post_to_navigation_upload)
        }

        checkPermissionAndLoadGallery()
    }

    private fun checkPermissionAndLoadGallery() {
        if (ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {
            loadGalleryImages()
        } else {
            // Show rationale or request permission
            if (shouldShowRequestPermissionRationale(Manifest.permission.READ_EXTERNAL_STORAGE)) {
                Toast.makeText(context, "We need access to your gallery to display images.", Toast.LENGTH_SHORT).show()
            }
            requestPermissionLauncher.launch(Manifest.permission.READ_EXTERNAL_STORAGE)
        }
    }


    private val requestPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { isGranted: Boolean ->
        if (isGranted) {
            loadGalleryImages()
        } else {
            Toast.makeText(context, "Permission denied", Toast.LENGTH_SHORT).show()
        }
    }

    private fun loadGalleryImages() {
        val imageList = mutableListOf<Uri>()
        val projection = arrayOf(MediaStore.Images.Media._ID)
        val sortOrder = "${MediaStore.Images.Media.DATE_ADDED} DESC"

        val query = requireContext().contentResolver.query(
            MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
            projection,
            null,
            null,
            sortOrder
        )

        query?.use { cursor ->
            val idColumn = cursor.getColumnIndexOrThrow(MediaStore.Images.Media._ID)
            while (cursor.moveToNext()) {
                val id = cursor.getLong(idColumn)
                val uri = Uri.withAppendedPath(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, id.toString())
                imageList.add(uri)
            }
        }

        if (imageList.isEmpty()) {
            Toast.makeText(context, "No images found", Toast.LENGTH_SHORT).show()
        } else {
            setupRecyclerView(imageList)
        }
    }

    private fun setupRecyclerView(imageList: List<Uri>) {
        val recyclerView = binding.galleryRecyclerView
        recyclerView.layoutManager = GridLayoutManager(context, 4)
        recyclerView.adapter = GalleryAdapter(imageList) { selectedImageUri ->
            binding.selectedImage.setImageURI(selectedImageUri)
        }
    }

    class GalleryAdapter(
        private val imageList: List<Uri>,
        private val onImageClick: (Uri) -> Unit
    ) : RecyclerView.Adapter<GalleryAdapter.GalleryViewHolder>() {

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): GalleryViewHolder {
            val view = LayoutInflater.from(parent.context).inflate(R.layout.item_image, parent, false)
            return GalleryViewHolder(view)
        }

        override fun onBindViewHolder(holder: GalleryViewHolder, position: Int) {
            val imageUri = imageList[position]
            holder.bind(imageUri)
        }

        override fun getItemCount() = imageList.size

        inner class GalleryViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
            private val imageView: ImageView = itemView.findViewById(R.id.imageThumbnail)

            fun bind(imageUri: Uri) {
                imageView.setImageURI(imageUri)
                itemView.setOnClickListener {
                    onImageClick(imageUri)
                }
            }
        }
    }
}