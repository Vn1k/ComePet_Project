package com.example.comepet.ui.profile.subfragments

import android.annotation.SuppressLint
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.comepet.R
import com.example.comepet.ui.profile.subfragments.ProfilePostsFragment.Companion
import com.example.comepet.ui.profile.subfragments.ProfilePostsFragment.ImageGalleryAdapter
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.auth.User
import com.google.firebase.storage.FirebaseStorage

class ProfileShelterFragment : Fragment(){

    private lateinit var locShelter: TextView
    private lateinit var userId: String
    private lateinit var recyclerView: RecyclerView
    private val imageUrls = mutableListOf<String>()
    private lateinit var imageAdapter: ImageGalleryAdapter

    private lateinit var db: FirebaseFirestore
    private lateinit var auth: FirebaseAuth
    private lateinit var storage: FirebaseStorage

    companion object {
        private const val ARG_USER_ID = "userId"

        fun newInstance(userId: String): ProfilePostsFragment {
            val fragment = ProfilePostsFragment()
            val args = Bundle()
            args.putString(ARG_USER_ID, userId)
            fragment.arguments = args
            return fragment
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        auth = FirebaseAuth.getInstance()
        db = FirebaseFirestore.getInstance()
        storage = FirebaseStorage.getInstance()

        userId = auth.currentUser?.uid ?: ""
        Log.d("ProfileShelterFragment", "onCreate called")
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_profile_shelter, container, false)

        // Inisialisasi RecyclerView
        recyclerView = view.findViewById(R.id.shelterRecyclerView)
        recyclerView.layoutManager = GridLayoutManager(context, 3)
        imageAdapter = ImageGalleryAdapter(imageUrls)
        recyclerView.adapter = imageAdapter

        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        locShelter = view.findViewById(R.id.locShelter)

        if (userId.isNotEmpty()) {
            getUserData()
            fetchUserShelter()
        } else {
            Toast.makeText(requireContext(), "User not logged in", Toast.LENGTH_SHORT).show()
        }
    }

    private fun getUserData() {
        db.collection("users")
            .document(userId)
            .get()
            .addOnSuccessListener { document ->
                if (document != null && document.exists()) {
                    val location = document.getString("location") ?: "Location not found"
                    locShelter.text = location
                } else {
                    Log.e("ProfileFragment", "Document does not exist")
                    Toast.makeText(requireContext(), "Document does not exist", Toast.LENGTH_SHORT)
                        .show()
                }
            }
            .addOnFailureListener { exception ->
                Log.e("ProfileFragment", "Error getting user data", exception)
                Toast.makeText(requireContext(), "Error getting user data", Toast.LENGTH_SHORT)
                    .show()
            }
    }

    @SuppressLint("NotifyDataSetChanged")
    private fun fetchUserShelter() {
        Log.d("ProfileShelterFragment", "Fetching user shelter data")
        db.collection("users")
            .document(userId)
            .collection("shelters")
            .orderBy("date", com.google.firebase.firestore.Query.Direction.DESCENDING)
            .get()
            .addOnSuccessListener { querySnapshot ->
                Log.d("ProfileShelterFragment", "Data fetched successfully")
                imageUrls.clear()
                for (document in querySnapshot.documents) {
                    val imageUrl = document.getString("imageUrl")
                    Log.d("ProfileShelterFragment", "Image URL: $imageUrl")
                    if (imageUrl != null) {
                        imageUrls.add(imageUrl)
                    }
                }
                imageAdapter.notifyDataSetChanged()
            }
            .addOnFailureListener { exception ->
                Log.e("ProfileShelterFragment", "Error fetching data", exception)
            }
    }



    class ImageGalleryAdapter(private val imageUrls: List<String>) :
        RecyclerView.Adapter<ImageGalleryAdapter.ImageViewHolder>() {

        class ImageViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
            val imageView: ImageView = itemView.findViewById(R.id.galleryShelter)
        }

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ImageViewHolder {
            val view = LayoutInflater.from(parent.context)
                .inflate(R.layout.gallery_shelter_profile, parent, false)
            return ImageViewHolder(view)
        }

        override fun onBindViewHolder(holder: ImageViewHolder, position: Int) {
            Glide.with(holder.itemView.context)
                .load(imageUrls[position])
                .centerCrop()
                .into(holder.imageView)
        }

        override fun getItemCount(): Int = imageUrls.size
    }

}