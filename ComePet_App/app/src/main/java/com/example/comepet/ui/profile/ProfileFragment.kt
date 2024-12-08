package com.example.comepet.ui.profile

import androidx.fragment.app.viewModels
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.navigation.fragment.findNavController
import androidx.viewbinding.ViewBinding
import com.bumptech.glide.Glide
import com.example.comepet.R
import com.example.comepet.databinding.FragmentProfileBinding
import com.example.comepet.ui.auth.BaseAuthFragment
import com.example.comepet.ui.profile.subfragments.ProfilePetsFragment
import com.example.comepet.ui.profile.subfragments.ProfilePostsFragment
import com.example.comepet.ui.profile.subfragments.ProfileShelterFragment
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import java.util.UUID

class ProfileFragment : BaseAuthFragment() {
    private var _binding: FragmentProfileBinding? = null
    private val binding get() = _binding!!
    private val viewModel: ProfileViewModel by viewModels()
    private lateinit var settingButton: ImageButton
    private lateinit var editProfileButton: Button
    private lateinit var addPetButton: Button
    private lateinit var messageButton: Button

    private lateinit var Profile_Name: TextView
    private lateinit var Profile_Picture: ImageView
    private lateinit var Profile_Username: TextView
    private lateinit var Profile_Status: TextView
    private lateinit var Profile_Bio: TextView

    override lateinit var mAuth: FirebaseAuth
    private lateinit var db: FirebaseFirestore
    private lateinit var storage: FirebaseStorage

    private var userId: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // Initialize Firebase
        mAuth = FirebaseAuth.getInstance()
        db = FirebaseFirestore.getInstance()
        storage = FirebaseStorage.getInstance()

        // Retrieve userId passed as an argument
        userId = arguments?.getString("userId") ?: mAuth.currentUser?.uid
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentProfileBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        initViews(view)

        if (savedInstanceState == null) {
            loadPostsFragment()
        }

        setupClickListeners()

        getUserData()

        updateUIBasedOnProfileOwner()
    }

    private fun updateUIBasedOnProfileOwner() {
        val isCurrentUserProfile = userId == mAuth.currentUser?.uid

        editProfileButton.visibility = if (isCurrentUserProfile) View.VISIBLE else View.GONE
        addPetButton.visibility = if (isCurrentUserProfile) View.VISIBLE else View.GONE
        settingButton.visibility = if (isCurrentUserProfile) View.VISIBLE else View.GONE

        if (!isCurrentUserProfile) {
            messageButton.visibility = View.VISIBLE
            messageButton.text = "Message" // or "Follow" depending on your app's design
        } else {
            messageButton.visibility = View.GONE
        }
    }

    private fun getUserData() {
        val userToFetch = userId ?: mAuth.currentUser?.uid
        userToFetch?.let {
            db.collection("users").document(it).get()
                .addOnSuccessListener { document ->
                    if (document != null) {
                        val name = document.getString("name") ?: ""
                        val username = document.getString("username") ?: ""
                        val status = document.getBoolean("accountStatus") ?: false
                        val bio = document.getString("bio") ?: ""
                        val profile_picture = document.getString("profilePicture") ?: ""

                        Profile_Name.text = name
                        Profile_Username.text = username
                        Profile_Status.text = if (status) "Available" else "Unavailable"
                        Profile_Status.setTextColor(
                            ContextCompat.getColor(
                                requireContext(),
                                if (status) R.color.green else R.color.red
                            )
                        )
                        Profile_Bio.text = bio

                        if (profile_picture.isNotEmpty()) {
                            Glide.with(requireContext())
                                .load(profile_picture)
                                .placeholder(R.drawable.defaultprofilepicture)
                                .into(Profile_Picture)
                        } else {
                            Profile_Picture.setImageResource(R.drawable.defaultprofilepicture)
                        }

                        binding.profileShelter.visibility = if (status) View.GONE else View.VISIBLE
                    }
                }
                .addOnFailureListener { exception ->
                    Log.e("ProfileFragment", "Error getting user data", exception)
                    Toast.makeText(requireContext(), "Error getting user data", Toast.LENGTH_SHORT).show()
                }
        }
    }


    private fun initViews(view: View) {
        Profile_Name = view.findViewById(R.id.Profile_Name)
        Profile_Picture = view.findViewById(R.id.Profile_Picture)
        Profile_Username = view.findViewById(R.id.Profile_Username)
        Profile_Status = view.findViewById(R.id.Profile_Status)
        Profile_Bio = view.findViewById(R.id.Profile_Bio)

        settingButton = view.findViewById(R.id.settingButton)
        editProfileButton = view.findViewById(R.id.buttonEditProfile)
        addPetButton = view.findViewById(R.id.buttonAddPet)
        messageButton = view.findViewById(R.id.buttonMessage)
    }

    private fun setupClickListeners() {
        settingButton.setOnClickListener {
            findNavController().navigate(R.id.navigation_profile_to_navigation_setting)
        }
        editProfileButton.setOnClickListener {
            findNavController().navigate(R.id.navigation_profile_to_navigation_edit_profile)
        }
        addPetButton.setOnClickListener {
            findNavController().navigate(R.id.navigation_profile_to_navigation_add_pet)
        }
        messageButton.setOnClickListener {
            userId?.let { targetUserId ->
                val bundle = Bundle().apply {
                    putString("receiverId", targetUserId)
                }
                findNavController().navigate(R.id.navigation_chat, bundle)
            } ?: run {
                Toast.makeText(requireContext(), "User ID not available", Toast.LENGTH_SHORT).show()
            }
        }

        binding.apply {
            profileGrid.setOnClickListener {
                loadPostsFragment()
                updateButtonStates(profileGrid)
            }

            profilePaw.setOnClickListener {
                loadPetsFragment()
                updateButtonStates(profilePaw)
            }

            profileShelter.setOnClickListener {
                loadShelterFragment()
                updateButtonStates(profileShelter)
            }
        }
    }

    private fun updateButtonStates(selectedButton: View) {
        // Reset all buttons to default state
        binding.apply {
            profileGrid.alpha = 0.5f
            profilePaw.alpha = 0.5f
            profileShelter.alpha = 0.5f

            // Highlight selected button
            selectedButton.alpha = 1.0f
        }
    }

    private fun loadPostsFragment() {
        childFragmentManager.beginTransaction()
            .replace(R.id.profile_content_container, ProfilePostsFragment())
            .commit()
    }

    private fun loadPetsFragment() {
        childFragmentManager.beginTransaction()
            .replace(R.id.profile_content_container, ProfilePetsFragment())
            .commit()
    }

    private fun loadShelterFragment() {
        childFragmentManager.beginTransaction()
            .replace(R.id.profile_content_container, ProfileShelterFragment())
            .commit()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    companion object {
        private const val ARG_USER_ID = "userId"

        fun newInstance(userId: String): ProfileFragment {
            val fragment = ProfileFragment()
            val args = Bundle()
            args.putString(ARG_USER_ID, userId)
            fragment.arguments = args
            return fragment
        }
    }
}
