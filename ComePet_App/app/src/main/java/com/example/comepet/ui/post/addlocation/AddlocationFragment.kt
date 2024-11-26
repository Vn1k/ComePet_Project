package com.example.comepet.ui.post.addlocation

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.ImageButton
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.comepet.R
//import com.example.comepet.adapters.LocationAdapter
//import com.example.comepet.models.LocationItem
import com.google.android.libraries.places.api.Places
import com.google.android.libraries.places.api.model.AutocompleteSessionToken
import com.google.android.libraries.places.api.net.FindAutocompletePredictionsRequest
import com.google.android.libraries.places.api.net.PlacesClient
import timber.log.Timber

class AddlocationFragment : Fragment() {

    private lateinit var locationRecyclerView: RecyclerView
    private lateinit var adapter: LocationAdapter
    private lateinit var placesClient: PlacesClient
    private lateinit var token: AutocompleteSessionToken
    private lateinit var backButtonToUpload: ImageButton
    private lateinit var searchEditText: EditText
    private var lastSearchTime: Long = 0
    private val debounceDelay: Long = 500 // 0.5 detik

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // Initialize the Places API
        Places.initialize(requireContext(), getString(R.string.google_maps_key))
        placesClient = Places.createClient(requireContext())
        token = AutocompleteSessionToken.newInstance()
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_addlocation, container, false)

        // Initialize RecyclerView
        locationRecyclerView = view.findViewById(R.id.locationRecyclerView)
        locationRecyclerView.layoutManager = LinearLayoutManager(context)

        // Set up Adapter with empty list
        adapter = LocationAdapter(emptyList())
        locationRecyclerView.adapter = adapter

        searchEditText = view.findViewById(R.id.searchEditText)
        searchEditText.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
                val now = System.currentTimeMillis()
                if (now - lastSearchTime > debounceDelay) {
                    lastSearchTime = now
                    s?.let { search ->
                        if (search.length >= 3) { // Minimal 3 karakter
                            searchLocation(search.toString())
                        } else if (adapter.itemCount > 0) {
                            // Jika kurang dari 3 karakter, kosongkan list jika tidak empty
                            adapter.updateLocations(emptyList())
                        }
                    }
                }
            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
        })

        backButtonToUpload = view.findViewById(R.id.backButtonToUpload)
        backButtonToUpload.setOnClickListener {
            findNavController().navigate(R.id.navigation_addlocation_to_navigation_upload)
        }

        return view
    }

    private fun searchLocation(query: String) {
        val request = FindAutocompletePredictionsRequest.builder()
            .setSessionToken(token)
            .setQuery(query)
            .build()

        placesClient.findAutocompletePredictions(request)
            .addOnSuccessListener { response ->
                val locations = response.autocompletePredictions.map { prediction ->
                    LocationItem(
                        name = prediction.getPrimaryText(null).toString(),
                        address = prediction.getSecondaryText(null).toString()
                    )
                }
                adapter.updateLocations(locations)
            }
            .addOnFailureListener { exception ->
                logError("Error searching location", exception)
                showToast("Error: ${exception.message}")
            }
    }

    private fun showToast(message: String, duration: Int = Toast.LENGTH_SHORT) {
        activity?.let {
            Toast.makeText(it, message, duration).show()
        }
    }

    private fun logError(message: String, throwable: Throwable? = null) {
        Timber.e(throwable, message)
    }
}
