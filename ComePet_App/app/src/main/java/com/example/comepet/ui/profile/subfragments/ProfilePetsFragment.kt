package com.example.comepet.ui.profile.subfragments

import android.content.res.Resources
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.comepet.R
import com.example.comepet.databinding.FragmentProfilePetsBinding
import com.example.comepet.databinding.ItemPetCardBinding
import com.example.comepet.ui.auth.register.model.Pet
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage

val Int.dp: Int
    get() = (this * Resources.getSystem().displayMetrics.density).toInt()


class ProfilePetsFragment : Fragment() {
    private var _binding: FragmentProfilePetsBinding? = null
    private val binding get() = _binding!!
//    private val firestore by lazy { FirebaseFirestore.getInstance() }
//    private val auth by lazy { FirebaseAuth.getInstance() }
    private var userId: String? = null

    private lateinit var mAuth: FirebaseAuth
    private lateinit var db: FirebaseFirestore
    private lateinit var storage: FirebaseStorage

    // Inner class for PetCarouselAdapter
    private inner class PetCarouselAdapter : RecyclerView.Adapter<PetCarouselAdapter.PetViewHolder>() {
        private var pets = listOf<Pet>()

        fun submitList(newPets: List<Pet>) {
            pets = newPets
            notifyDataSetChanged()
        }

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PetViewHolder {
            val binding = ItemPetCardBinding.inflate(
                LayoutInflater.from(parent.context), parent, false
            )
            return PetViewHolder(binding)
        }

        override fun onBindViewHolder(holder: PetViewHolder, position: Int) {
            holder.bind(pets[position])
        }

        override fun getItemCount() = pets.size

        inner class PetViewHolder(private val binding: ItemPetCardBinding) :
            RecyclerView.ViewHolder(binding.root) {
            fun bind(pet: Pet) {
                binding.petName.text = pet.petName
                binding.petBirthday.text = pet.dateOfBirth
                binding.petBio.text = pet.description
                binding.petBreed.text = pet.breed

                Glide.with(binding.root.context)
                    .load(pet.profilePicture)
                    .into(binding.petImage)

                val backgroundColor = if (pet.sex == "M") {
                    binding.root.context.getColor(R.color.soft_blue)
                } else {
                    binding.root.context.getColor(R.color.pink)
                }
                binding.petCard.setBackgroundColor(backgroundColor)
            }
        }
    }

    // Create a single instance of the adapter
    private val carouselAdapter = PetCarouselAdapter()

    companion object {
        private const val ARG_USER_ID = "userId"

        fun newInstance(userId: String): ProfilePetsFragment {
            val fragment = ProfilePetsFragment()
            val args = Bundle()
            args.putString(ARG_USER_ID, userId)
            fragment.arguments = args
            return fragment
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        initFirebase()

        // Detailed logging
        Log.d("ProfilePetsFragment", "Arguments: ${arguments.toString()}")
        Log.d("ProfilePetsFragment", "ARG_USER_ID value: ${arguments?.getString(ARG_USER_ID)}")

        // Retrieve targetUserId from arguments
        userId = arguments?.getString(ARG_USER_ID)

        Log.d("ProfilePetsFragment", "Received userId: $userId")
    }
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentProfilePetsBinding.inflate(inflater, container, false)
        fetchPets()
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupCarousel()
    }

    private fun initFirebase() {
        mAuth = FirebaseAuth.getInstance()
        db = FirebaseFirestore.getInstance()
        storage = FirebaseStorage.getInstance()
    }

    private fun fetchPets() {
        Log.d("ProfilePetsFragment", "Attempting to fetch pets for userId: $userId")

        userId?.let { targetUserId ->
            Log.d("ProfilePetsFragment", "Fetching pets from Firestore for user: $targetUserId")
            db.collection("users").document(targetUserId)
                .collection("pets")
                .get()
                .addOnSuccessListener { result ->
                    val pets = result.documents.mapNotNull { document ->
                        document.toObject(Pet::class.java)?.let { pet ->
                            Pet(
                                petId = document.id,
                                petName = pet.petName,
                                sex = pet.sex,
                                dateOfBirth = pet.dateOfBirth,
                                species = pet.species,
                                breed = pet.breed,
                                description = pet.description,
                                profilePicture = pet.profilePicture
                            )
                        }
                    }
                    Log.d("ProfilePetsFragment", "Fetched ${pets.size} pets")
                    carouselAdapter.submitList(pets)
                }
                .addOnFailureListener { exception ->
                    Log.e("ProfilePetsFragment", "Failed to fetch pets", exception)
                    Toast.makeText(
                        context,
                        "Failed to fetch pets: ${exception.message}",
                        Toast.LENGTH_SHORT
                    ).show()
                }
        } ?: run {
            Log.e("ProfilePetsFragment", "No user ID available")
            Toast.makeText(context, "User ID not found", Toast.LENGTH_SHORT).show()
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