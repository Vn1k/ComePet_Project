package com.example.comepet.ui.post.location

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.RecyclerView
import com.example.comepet.R
import com.google.android.gms.maps.model.LatLng
import com.google.android.libraries.places.api.Places
import com.google.android.libraries.places.api.model.Place
import com.google.android.libraries.places.api.model.RectangularBounds
import com.google.android.libraries.places.api.net.PlacesClient
import com.google.android.libraries.places.api.net.SearchByTextRequest
import com.google.android.material.textfield.TextInputEditText
import java.util.Arrays


class LocationFragment : Fragment() {
    private lateinit var searchLocation: TextInputEditText
    private lateinit var placesClient: PlacesClient

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_location, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        placesClient = Places.createClient(requireContext())

        // Setup the search location text input
        searchLocation = view.findViewById(R.id.search_location)

        searchLocation.setOnEditorActionListener { _, _, _ ->
            val query = searchLocation.text.toString()
            if (query.isNotEmpty()) {
                searchForPlaces(query)
            }
            true
        }
    }

    private fun searchForPlaces(query: String) {
        val placeFields = Arrays.asList(Place.Field.ID, Place.Field.NAME)

        val request = SearchByTextRequest.builder(query, placeFields)
            .setMaxResultCount(10)
            .build()

        placesClient.searchByText(request)
            .addOnSuccessListener { response ->
                val places: List<Place> = response.getPlaces()
                // Handle the places here, e.g., update your RecyclerView
                showSearchResults(places)
            }
            .addOnFailureListener { exception ->
                Log.e("LocationFragment", "Place search failed: ${exception.message}")
            }
    }

    private fun showSearchResults(places: List<Place>) {
        val recyclerView: RecyclerView = view?.findViewById(R.id.recycler_view_places) ?: return
        recyclerView.visibility = View.VISIBLE

        val adapter = PlacesAdapter(places)
        recyclerView.adapter = adapter
    }
}