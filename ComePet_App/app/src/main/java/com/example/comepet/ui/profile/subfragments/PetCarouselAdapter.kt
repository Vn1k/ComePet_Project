package com.example.comepet.ui.profile.subfragments

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.comepet.R
import com.example.comepet.databinding.ItemPetCardBinding

class PetCarouselAdapter : RecyclerView.Adapter<PetCarouselAdapter.PetViewHolder>() {
    private var pets = listOf<PetData>()

    fun submitList(newPets: List<PetData>) {
        pets = newPets
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PetViewHolder {
        val binding = ItemPetCardBinding.inflate(
            LayoutInflater.from(parent.context), parent, false
        )
        return PetViewHolder(binding)
    }

    override fun onBindViewHolder(holder: PetViewHolder, position: Int) {
        holder.bind(pets[position])
    }

    override fun getItemCount() = pets.size

    class PetViewHolder(private val binding: ItemPetCardBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(pet: PetData) {
            binding.apply {
                petName.text = pet.name
                petAge.text = pet.age
                // In real app, use proper image loading library like Glide
                petImage.setImageResource(R.drawable.temp_post_01)
            }
        }
    }
}