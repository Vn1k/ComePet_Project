package com.example.comepet.ui.profile.subfragments

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.comepet.R
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage

class ProfilePostsFragment : Fragment() {

    private lateinit var mAuth: FirebaseAuth
    private lateinit var db: FirebaseFirestore
    private lateinit var storage: FirebaseStorage
    private lateinit var recyclerView: RecyclerView
    private lateinit var imageAdapter: ImageGalleryAdapter

    private val imageUrls = mutableListOf<String>()
    private var userId: String? = null

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
        initFirebase()

        // Log the raw arguments
        Log.d("ProfilePostsFragment", "Raw arguments: ${arguments}")

        // Log the retrieved user ID from arguments
        val argUserId = arguments?.getString(ARG_USER_ID)
        Log.d("ProfilePostsFragment", "User ID from arguments: $argUserId")

        // Retrieve userId from arguments
        userId = arguments?.getString(ARG_USER_ID) ?: mAuth.currentUser?.uid

        // Log the final userId
        Log.d("ProfilePostsFragment", "Final userId: $userId")
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View? {
        val view = inflater.inflate(R.layout.fragment_profile_posts, container, false)

        recyclerView = view.findViewById(R.id.profilePostsRecyclerView)
        recyclerView.layoutManager = GridLayoutManager(context, 3)

        imageAdapter = ImageGalleryAdapter(imageUrls)
        recyclerView.adapter = imageAdapter

        fetchUserPosts()

        return view
    }

    private fun initFirebase() {
        mAuth = FirebaseAuth.getInstance()
        db = FirebaseFirestore.getInstance()
        storage = FirebaseStorage.getInstance()
    }

    private fun fetchUserPosts() {
        userId?.let { targetUserId ->
            db.collection("users").document(targetUserId)
                .collection("feeds")
                .orderBy("date", com.google.firebase.firestore.Query.Direction.DESCENDING)
                .get()
                .addOnSuccessListener { querySnapshot ->
                    imageUrls.clear()
                    for (document in querySnapshot.documents) {
                        val imageUrl = document.getString("imageUrl")
                        imageUrl?.let {
                            imageUrls.add(it)
                        }
                    }
                    imageAdapter.notifyDataSetChanged()
                }
                .addOnFailureListener { exception ->
                    // Handle any errors here
                    // You might want to show a toast or log the error
                }
        }
    }

    // Adapter for the RecyclerView
    class ImageGalleryAdapter(private val imageUrls: List<String>) :
        RecyclerView.Adapter<ImageGalleryAdapter.ImageViewHolder>() {

        class ImageViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
            val imageView: ImageView = itemView.findViewById(R.id.galleryImageView)
        }

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ImageViewHolder {
            val view = LayoutInflater.from(parent.context)
                .inflate(R.layout.gallery_item_profile, parent, false)
            return ImageViewHolder(view)
        }

        override fun onBindViewHolder(holder: ImageViewHolder, position: Int) {
            Glide.with(holder.itemView.context)
                .load(imageUrls[position])
                .centerCrop()
                .into(holder.imageView)
        }

        override fun getItemCount() = imageUrls.size
    }
}