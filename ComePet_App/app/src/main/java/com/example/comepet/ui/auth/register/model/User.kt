package com.example.comepet.ui.auth.register.model

data class User(
    var name: String = "",
    var username: String = "",
    var email: String = "",
    var phone: String = "",
    var bio: String = "",
    var location: String = "",
    var profilePicture: String = ""
)