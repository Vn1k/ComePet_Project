package com.example.comepet.ui.profile.subfragments

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import com.example.comepet.R
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.auth.User

class ProfileShelterFragment : Fragment(){

    private lateinit var locShelter: TextView
    private lateinit var userId: String

    private lateinit var db: FirebaseFirestore
    private lateinit var auth: FirebaseAuth

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_profile_shelter, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        locShelter = view.findViewById(R.id.locShelter)

        auth = FirebaseAuth.getInstance()
        db = FirebaseFirestore.getInstance()

        userId = auth.currentUser?.uid ?: ""

        if (userId.isNotEmpty()) {
            getUserData()
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
}