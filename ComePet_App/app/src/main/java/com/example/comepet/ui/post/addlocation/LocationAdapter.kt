package com.example.comepet.ui.post.addlocation

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.comepet.R

// LocationAdapter.kt
class LocationAdapter(private var locations: List<LocationItem>) :
    RecyclerView.Adapter<LocationAdapter.LocationViewHolder>() {

    // Fungsi untuk memperbarui data dan memberi tahu adapter untuk merefresh UI
    fun updateLocations(newLocations: List<LocationItem>) {
        this.locations = newLocations
        notifyDataSetChanged()
    }

    inner class LocationViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        // Menemukan views yang ada di item_layout
        val locationName: TextView = itemView.findViewById(R.id.locationName)
        val locationAddress: TextView = itemView.findViewById(R.id.locationAddress)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): LocationViewHolder {
        // Menginflate layout item_location untuk setiap item
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_location, parent, false)
        return LocationViewHolder(view)
    }

    override fun onBindViewHolder(holder: LocationViewHolder, position: Int) {
        // Mengambil data lokasi pada posisi tertentu dan mengisi text view dengan data
        val location = locations[position]
        holder.locationName.text = location.name
        holder.locationAddress.text = location.address
    }

    override fun getItemCount() = locations.size
}
