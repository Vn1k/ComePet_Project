package com.example.comepet.ui.profile.subfragments

import android.content.res.Resources
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.example.comepet.databinding.FragmentProfilePetsBinding
import com.example.comepet.ui.auth.register.model.Pet
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage

val Int.dp: Int
    get() = (this * Resources.getSystem().displayMetrics.density).toInt()


//private const val ARG_PARAM1 = "param1"
//private const val ARG_PARAM2 = "param2"

class ProfilePetsFragment : Fragment() {
    private var _binding: FragmentProfilePetsBinding? = null
    private val binding get() = _binding!!
    private val carouselAdapter = PetCarouselAdapter()
    private val firestore by lazy { FirebaseFirestore.getInstance() }
    private val auth by lazy { FirebaseAuth.getInstance() }
    private lateinit var userId: String

    private lateinit var db: FirebaseFirestore
    private lateinit var storage: FirebaseStorage

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentProfilePetsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupCarousel()
        fetchPets()
    }

    private fun fetchPets() {
        val currentUser = auth.currentUser
        if (currentUser != null) {
            val userId = currentUser.uid // Ambil UID pengguna yang sedang login

            firestore.collection("users")
                .document(userId)
                .collection("pets")
                .get()
                .addOnSuccessListener { result ->

                    val pets = result.documents.mapNotNull { document ->
                        val Pet = document.toObject(Pet::class.java)
                        Pet?.let {

                            Pet(
                                petId = document.id,
                                petName = it.petName,
                                sex = it.sex,
                                dateOfBirth = it.dateOfBirth,
                                species = it.species,
                                breed = it.breed,
                                description = it.description,
                                profilePicture = it.profilePicture
                            )
                        }
                    }
                    carouselAdapter.submitList(pets)
                }
                .addOnFailureListener { exception ->
                    Toast.makeText(context, "Failed to fetch pets: ${exception.message}", Toast.LENGTH_SHORT).show()
                }
        } else {
            Toast.makeText(context, "User not logged in", Toast.LENGTH_SHORT).show()
        }
    }


    private fun setupCarousel() {
        binding.viewPager.apply {
            adapter = carouselAdapter
            setPadding(50, 0, 50, 0)
            clipToPadding = false
            (getChildAt(0) as RecyclerView).apply {
                setPadding(8.dp, 0, 8.dp, 0)
                clipToPadding = false
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}