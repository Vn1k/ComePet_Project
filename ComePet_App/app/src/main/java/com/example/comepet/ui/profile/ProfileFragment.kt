package com.example.comepet.ui.profile

import androidx.fragment.app.viewModels
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import androidx.navigation.fragment.findNavController
import androidx.viewbinding.ViewBinding
import com.example.comepet.R
import com.example.comepet.databinding.FragmentProfileBinding
import com.example.comepet.ui.auth.BaseAuthFragment
import com.example.comepet.ui.profile.subfragments.ProfilePetsFragment
import com.example.comepet.ui.profile.subfragments.ProfilePostsFragment
import com.example.comepet.ui.profile.subfragments.ProfileShelterFragment

class ProfileFragment : BaseAuthFragment() {
    private var _binding: FragmentProfileBinding? = null
    private val binding get() = _binding!!
    private val viewModel: ProfileViewModel by viewModels()
    private lateinit var settingButton: ImageButton

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentProfileBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Set default fragment
        if (savedInstanceState == null) {
            loadPostsFragment()
        }

        setupClickListeners()

        settingButton = view.findViewById(R.id.settingButton)
        settingButton.setOnClickListener {
            findNavController().navigate(R.id.navigation_profile_to_navigation_setting)
        }
    }

    private fun setupClickListeners() {
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