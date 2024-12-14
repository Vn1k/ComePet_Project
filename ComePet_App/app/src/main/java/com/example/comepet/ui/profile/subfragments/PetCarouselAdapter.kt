package com.example.comepet.ui.profile.subfragments

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.comepet.R
import com.example.comepet.databinding.ItemPetBinding
import com.example.comepet.databinding.ItemPetCardBinding
import com.example.comepet.ui.auth.register.model.Pet
import com.example.comepet.ui.auth.register.model.User

class PetCarouselAdapter : RecyclerView.Adapter<PetCarouselAdapter.PetViewHolder>() {
    private var pets = listOf<Pet>()

    fun submitList(newPets: List<Pet>) {
        pets = newPets
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PetViewHolder {
        // Inflate the correct layout and binding for the CardView
        val binding = ItemPetCardBinding.inflate(
            LayoutInflater.from(parent.context), parent, false
        )
        return PetViewHolder(binding)
    }

    override fun onBindViewHolder(holder: PetViewHolder, position: Int) {
        holder.bind(pets[position])
    }

    override fun getItemCount() = pets.size

    inner class PetViewHolder(private val binding: ItemPetCardBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(pet: Pet) {

            binding.petName.text = pet.petName
            binding.petBirthday.text = pet.dateOfBirth
            binding.petBio.text = pet.description
            binding.petBreed.text = pet.breed

            Glide.with(binding.root.context)
                .load(pet.profilePicture)
                .into(binding.petImage)

        }
    }
}