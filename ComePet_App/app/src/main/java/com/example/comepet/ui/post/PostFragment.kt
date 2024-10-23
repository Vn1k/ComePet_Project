package com.example.comepet.ui.post

import android.Manifest
import android.content.ContentResolver
import android.content.pm.PackageManager
import android.net.Uri
import androidx.fragment.app.viewModels
import android.os.Bundle
import android.os.Handler
import android.provider.MediaStore
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.comepet.MainActivity
import com.example.comepet.R
import com.example.comepet.databinding.FragmentPostBinding
import com.example.comepet.ui.post.camera.CameraFragment
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class PostFragment : Fragment() {

    private lateinit var binding: FragmentPostBinding
    private lateinit var auth: FirebaseAuth
    private lateinit var firestore: FirebaseFirestore
    private val CAMERA_REQUEST_CODE = 100
    private lateinit var db: FirebaseFirestore
    private lateinit var cameraButton: ImageButton
    private lateinit var backButtonToHome : ImageButton

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        auth = FirebaseAuth.getInstance()
        firestore = FirebaseFirestore.getInstance()
        db = FirebaseFirestore.getInstance()
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentPostBinding.inflate(inflater, container, false)
        return binding.root
    }


    private fun openCameraFragment() {
        Log.d("PostFragment", "Opening Camera Fragment")
        val cameraFragment = CameraFragment()
        parentFragmentManager.beginTransaction()
            .replace(R.id.preview_view, cameraFragment)
            .addToBackStack(null)
            .commit()

    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == CAMERA_REQUEST_CODE) {
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                openCameraFragment()
            } else {
                Toast.makeText(context, "Camera permission denied", Toast.LENGTH_SHORT).show()
            }
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        auth = FirebaseAuth.getInstance()
        firestore = FirebaseFirestore.getInstance()

        cameraButton = view.findViewById(R.id.cameraButton)
        backButtonToHome = view.findViewById(R.id.backButtonToHome)

        cameraButton.setOnClickListener {
            // Navigasi ke CameraFragment
            findNavController().navigate(R.id.navigation_post_to_navigation_camera)
        }

        backButtonToHome.setOnClickListener {
            findNavController().navigate(R.id.navigation_post_to_navigation_home)
        }

        // Load gallery images
        if (ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            requestPermissionLauncher.launch(Manifest.permission.READ_EXTERNAL_STORAGE)
        } else {
            loadGalleryImages()
        }

    }

//    AMBIL GALLERY

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

        setupRecyclerView(imageList)
    }

    // Tampilkan gambar yang ada di galeri
    private fun setupRecyclerView(imageList: List<Uri>) {
        val recyclerView = binding.galleryRecyclerView
        recyclerView.layoutManager = GridLayoutManager(context, 4)
        recyclerView.adapter = GalleryAdapter(imageList) { selectedImageUri ->
            binding.selectedImage.setImageURI(selectedImageUri) // Tampilkan gambar yang dipilih
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
