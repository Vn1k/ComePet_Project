package com.example.comepet.ui.profile.subfragments

import android.content.res.Resources
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.comepet.databinding.FragmentProfilePetsBinding

val Int.dp: Int
    get() = (this * Resources.getSystem().displayMetrics.density).toInt()


private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

class ProfilePetsFragment : Fragment() {
    private var _binding: FragmentProfilePetsBinding? = null
    private val binding get() = _binding!!
    private val carouselAdapter = PetCarouselAdapter()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentProfilePetsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupCarousel()
        loadPetData()
    }

    private fun setupCarousel() {
        binding.viewPager.apply {
            adapter = carouselAdapter
            // Add padding for peek effect
            setPadding(50, 0, 50, 0)
            // Show part of next/previous items
            clipToPadding = false
            // Add space between items
            (getChildAt(0) as RecyclerView).apply {
                setPadding(8.dp, 0, 8.dp, 0)
                clipToPadding = false
            }
        }
    }

    private fun loadPetData() {
        // Sample data - replace with actual data source
        val samplePets = listOf(
            PetData(1, "Max", "2 years", ""),
            PetData(2, "Luna", "1 year", ""),
            PetData(3, "Charlie", "3 years", "")
        )
        carouselAdapter.submitList(samplePets)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}