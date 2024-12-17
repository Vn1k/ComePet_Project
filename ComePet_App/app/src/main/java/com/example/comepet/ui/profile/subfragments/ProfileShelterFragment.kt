package com.example.comepet.ui.profile.subfragments

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.example.comepet.databinding.FragmentProfileShelterBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class ProfileShelterFragment : Fragment() {
    private var _binding: FragmentProfileShelterBinding? = null
    private val binding get() = _binding!!

    private lateinit var mAuth: FirebaseAuth
    private lateinit var db: FirebaseFirestore

    private var userId: String? = null

    companion object {
        private const val ARG_USER_ID = "userId"

        fun newInstance(userId: String): ProfileShelterFragment {
            val fragment = ProfileShelterFragment()
            val args = Bundle()
            args.putString(ARG_USER_ID, userId)
            fragment.arguments = args
            return fragment
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        initFirebase()

        // Get the userId passed from the arguments
        userId = arguments?.getString(ARG_USER_ID)
        Log.d("ProfileShelterFragment", "User ID received: $userId")
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentProfileShelterBinding.inflate(inflater, container, false)

        // Fetch shelter location from Firestore
        fetchShelterLocation()

        return binding.root
    }

    private fun initFirebase() {
        mAuth = FirebaseAuth.getInstance()
        db = FirebaseFirestore.getInstance()
    }

    private fun fetchShelterLocation() {
        userId?.let { id ->
            db.collection("users").document(id)
                .get()
                .addOnSuccessListener { document ->
                    if (document.exists()) {
                        val location = document.getString("location") ?: "No Shelter Found"
                        binding.tvShelter.text = location // Update TextView
                        Log.d("ProfileShelterFragment", "Shelter location: $location")
                    } else {
                        Log.e("ProfileShelterFragment", "No such document")
                        Toast.makeText(context, "No shelter found for this user", Toast.LENGTH_SHORT).show()
                    }
                }
                .addOnFailureListener { exception ->
                    Log.e("ProfileShelterFragment", "Error fetching shelter location", exception)
                    Toast.makeText(
                        context,
                        "Error fetching shelter location: ${exception.message}",
                        Toast.LENGTH_SHORT
                    ).show()
                }
        } ?: run {
            Log.e("ProfileShelterFragment", "User ID is null")
            Toast.makeText(context, "User ID not provided", Toast.LENGTH_SHORT).show()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
