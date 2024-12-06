package com.example.comepet.ui.auth

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.example.comepet.R
import com.example.comepet.ui.auth.register.model.Pet
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

open class BaseAuthFragment : Fragment() {
    open lateinit var mAuth: FirebaseAuth
    private val db = FirebaseFirestore.getInstance()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        mAuth = FirebaseAuth.getInstance()
        val currentUser = mAuth.currentUser
        if(currentUser == null) {
            redirectToOption()
        } else if (isUserBlocked()) {
            redirectToOption()
        }
    }

    // Function to add a new pet
    protected fun addPet(userId: String, pet: Pet) {
        db.collection("users")
            .document(userId)
            .collection("petIdLists")
            .document(pet.petId)
            .set(pet)
    }

    // Function to get all pets for a user
    protected fun getPets(userId: String, onComplete: (List<Pet>) -> Unit) {
        db.collection("users")
            .document(userId)
            .collection("petIdLists")
            .get()
            .addOnSuccessListener { documents ->
                val petsList = documents.mapNotNull { it.toObject(Pet::class.java) }
                onComplete(petsList)
            }
    }

    private fun isUserBlocked(): Boolean {
        return false
    }

    private fun redirectToOption() {
        findNavController().navigate(R.id.navigation_login)
    }
}


// To add pet:
// Adding a new pet
//val newPet = Pet(
//    petId = "pet123",
//    name = "Max",
//    birthday = "2020-01-01"
//)
//addPet(mAuth.currentUser?.uid ?: "", newPet)
//
//// Getting all pets
//getPets(mAuth.currentUser?.uid ?: "") { petsList ->
//    // Do something with the list of pets
//    petsList.forEach { pet ->
//        println("Pet name: ${pet.name}")
//    }
//}