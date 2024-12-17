package com.example.comepet.ui.post.location

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.example.comepet.R
import com.google.android.libraries.places.api.Places
import com.google.android.libraries.places.api.model.AutocompletePrediction
import com.google.android.libraries.places.api.model.Place
import com.google.android.libraries.places.api.net.FindAutocompletePredictionsRequest
import com.google.android.libraries.places.api.net.PlacesClient

class LocationFragment : Fragment() {

    private lateinit var placesClient: PlacesClient

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        if (!Places.isInitialized()) {
            Places.initialize(requireContext(), "AIzaSyC4FeUXCDHBa4TXiSWHy1qwtwzyTQ_6so0")
        }
        placesClient = Places.createClient(requireContext())
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        fetchPlacePredictions("cafe") // Example: Fetch predictions for "cafe"
        return inflater.inflate(R.layout.fragment_location, container, false)
    }

    private fun fetchPlacePredictions(query: String) {
        val request = FindAutocompletePredictionsRequest.builder()
            .setQuery(query)
            .setTypeFilter(null)
            .setCountry("US")
            .build()

        placesClient.findAutocompletePredictions(request)
            .addOnSuccessListener { response ->
                for (prediction: AutocompletePrediction in response.autocompletePredictions) {
                    Log.d("LocationFragment", "Place: ${prediction.getPrimaryText(null)}")
                }
            }
            .addOnFailureListener { exception ->
                Log.e("LocationFragment", "Place prediction fetch failed: $exception")
            }
    }
}