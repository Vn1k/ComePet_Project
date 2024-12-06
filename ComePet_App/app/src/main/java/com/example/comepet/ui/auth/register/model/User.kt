package com.example.comepet.ui.auth.register.model

data class Pet(
    val petId: String = "",
    val petName: String = "",
    val sex: String = "",
    val dateOfBirth: String = "",
    val species: String = "",
    val breed: String = "",
    val description: String = ""
)

data class User(
    var name: String = "",
    var username: String = "",
    var email: String = "",
    var phone: String = "",
    var bio: String = "",
    var location: String = "",
    var profilePicture: String = "",
    var accountStatus: Boolean = false,
    var petIdLists: List<Pet> = listOf()
)