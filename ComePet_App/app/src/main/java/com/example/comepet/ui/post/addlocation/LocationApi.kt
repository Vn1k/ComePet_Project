package com.example.comepet.ui.post.addlocation

import retrofit2.Call
import retrofit2.Response
import retrofit2.http.GET

// LocationApi.kt
interface LocationApi {
    @GET("locations")
    suspend fun getLocations(): Response<List<LocationItem>>
}


