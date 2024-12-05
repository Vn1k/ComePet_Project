package com.example.comepet.ui.post.tagpet

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.comepet.R

class PetAdapter(private val petList: List<Pet>) : RecyclerView.Adapter<PetAdapter.PetViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PetViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_pet, parent, false)
        return PetViewHolder(view)
    }

    override fun onBindViewHolder(holder: PetViewHolder, position: Int) {
        val pet = petList[position]
        holder.bind(pet)
    }

    override fun getItemCount(): Int = petList.size

    class PetViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val profileImageView: ImageView = itemView.findViewById(R.id.profilepet1)
        private val nameTextView: TextView = itemView.findViewById(R.id.username1)
        private val typeTextView: TextView = itemView.findViewById(R.id.typepet1)

        fun bind(pet: Pet) {
            nameTextView.text = pet.name
            typeTextView.text = pet.ras

            Glide.with(itemView.context)
                .load(pet.petProfilePicture)
                .placeholder(R.drawable.cat)
                .into(profileImageView)
        }
    }
}
