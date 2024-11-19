package com.example.comepet.ui.post.addlocation

import android.Manifest
import android.content.pm.PackageManager
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.setFragmentResult
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.comepet.R
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.gms.tasks.OnSuccessListener
import java.util.*

class AddlocationFragment : Fragment() {

    private lateinit var locationRecyclerView: RecyclerView
    private lateinit var adapter: LocationAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_addlocation, container, false)

        // Initialize RecyclerView
        locationRecyclerView = view.findViewById(R.id.locationRecyclerView)
        locationRecyclerView.layoutManager = LinearLayoutManager(context)

        // Sample Data
        val sampleLocations = listOf(
            Location("University", "123 University Street"),
            Location("Mountain", "45 Mountain Road"),
            Location("Beach", "78 Beachside Avenue")
        )

        // Set up Adapter
        adapter = LocationAdapter(sampleLocations)
        locationRecyclerView.adapter = adapter

        return view
    }
}

data class Location(val name: String, val address: String)


