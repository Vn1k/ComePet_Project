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

    private lateinit var Profile_Name: TextView
    private lateinit var Profile_Picture: ImageView
    private lateinit var Profile_Username: TextView
    private lateinit var Profile_Status: TextView
    private lateinit var Profile_Bio: TextView

    override lateinit var mAuth: FirebaseAuth
    private lateinit var db: FirebaseFirestore
    private lateinit var storage: FirebaseStorage

    override fun onCreate(savedInstanceState: Bundle?){
        super.onCreate(savedInstanceState)
        // Initialize Firebase
        mAuth = FirebaseAuth.getInstance()
        db = FirebaseFirestore.getInstance()
        storage = FirebaseStorage.getInstance()
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

        // For the profile page
        initViews(view)

        // Set default fragment
        if (savedInstanceState == null) {
            loadPostsFragment()
        }
        // Set up click listeners for buttons and tabs
        setupClickListeners()

        // Load user data
        getCurrentUserData()
    }

    private fun getCurrentUserData() {
        val currentUser = mAuth.currentUser

        currentUser?.let {
            db.collection("users").document(currentUser.uid).get()
                .addOnSuccessListener { document ->
                    if (document != null) {
                        val name = document.getString("name") ?: ""
                        val username = document.getString("username") ?: ""
                        val status = document.getBoolean("accountStatus") ?: ""
                        val bio = document.getString("bio") ?: ""
                        val profile_picture = document.getString("profilePicture") ?: ""

                        // Fill the profile page with user data
                        Profile_Name.text = name
                        Profile_Username.text = username
                        // Set the status text
                        if (status == true) {
                            Profile_Status.text = "Available"
                            Profile_Status.setTextColor(ContextCompat.getColor(requireContext(), R.color.green)) // Replace with your green color resource
                        } else {
                            Profile_Status.text = "Unavailable"
                            Profile_Status.setTextColor(ContextCompat.getColor(requireContext(), R.color.red)) // Replace with your red color resource
                        }

                        Profile_Bio.text = bio

                        if (profile_picture.isNotEmpty()) {
                            Glide.with(requireContext())
                                .load(profile_picture)
                                .placeholder(R.drawable.defaultprofilepicture)
                                .into(Profile_Picture)
                        }
                        else {
                            Profile_Picture.setImageResource(R.drawable.defaultprofilepicture)
                        }
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
}