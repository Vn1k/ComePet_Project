package com.example.comepet.ui.post.tagpet

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.comepet.R
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.QueryDocumentSnapshot
import com.google.firebase.firestore.QuerySnapshot
import com.google.firebase.auth.FirebaseAuth


class TagpetFragment : Fragment() {

    private lateinit var backButtonToUpload : ImageButton
    private lateinit var db: FirebaseFirestore
    private lateinit var auth: FirebaseAuth


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        db = FirebaseFirestore.getInstance()
        auth = FirebaseAuth.getInstance()
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_tagpet, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        backButtonToUpload = view.findViewById(R.id.backButtonToUpload)

        backButtonToUpload.setOnClickListener {
            findNavController().navigate(R.id.navigation_tagpet_to_navigation_upload)
        }

        FetchPet()
    }

    private fun FetchPet() {
        val userId = auth.currentUser?.uid

        if (userId != null) {
            db.collection("users").document(userId).collection("petIdLists")
                .get()
                .addOnSuccessListener { result ->
                    val petList = mutableListOf<Pet>()
                    for (document in result) {
                        val name = document.getString("name")
                        val petProfilePicture = document.getString("petProfilePicture")
                        val ras = document.getString("ras")

                        val pet = Pet(name, petProfilePicture, ras)
                        petList.add(pet)
                    }

                    view?.let { nonNullView ->
                        val recyclerView: RecyclerView = nonNullView.findViewById(R.id.petProfileRecyclerView)
                        recyclerView.layoutManager = LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)
                        recyclerView.adapter = PetAdapter(petList)
                    }
                }
                .addOnFailureListener { exception ->
                    println("Error getting documents: $exception")
                }
        } else {
            println("User is not logged in")
        }
    }
}