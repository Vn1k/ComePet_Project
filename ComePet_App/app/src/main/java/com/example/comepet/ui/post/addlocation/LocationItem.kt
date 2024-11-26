package com.example.comepet.ui.post.addlocation

import com.google.gson.annotations.SerializedName

data class LocationItem(
    @SerializedName("name") val name: String,
    @SerializedName("address") val address: String
)
