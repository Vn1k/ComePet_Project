package com.example.comepet.ui.post.location

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.example.comepet.R
import com.google.android.libraries.places.api.Places
import com.google.android.libraries.places.api.model.AutocompletePrediction
import com.google.android.libraries.places.api.model.Place
import com.google.android.libraries.places.api.net.FindAutocompletePredictionsRequest
import com.google.android.libraries.places.api.net.PlacesClient

class LocationFragment : Fragment() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_location, container, false)

        // Reference the EditText and Button
        val locationInput: EditText = view.findViewById(R.id.editTextLocation)
        val saveButton: Button = view.findViewById(R.id.buttonSaveLocation)

        // Set an OnClickListener for the button
        saveButton.setOnClickListener {
            val location = locationInput.text.toString().trim() // Trim input to avoid leading/trailing spaces
            if (location.isNotEmpty()) {
                // Log the location to be sent to UploadFragment
                Log.d("LocationFragment", "Location to be sent: $location")

                // Kirim data lokasi ke UploadFragment menggunakan setFragmentResult
                parentFragmentManager.setFragmentResult("SELECTED_LOCATION_REQUEST", bundleOf(
                    "selectedLocation" to location
                ))

                // Pindah ke UploadFragment
                findNavController().navigate(R.id.navigation_location_to_navigation_upload)

                // Show a Toast indicating the location is saved
                Toast.makeText(requireContext(), "Location saved: $location", Toast.LENGTH_SHORT).show()
            } else {
                Toast.makeText(requireContext(), "Please enter a location", Toast.LENGTH_SHORT).show()
            }
        }


        return view
    }
}