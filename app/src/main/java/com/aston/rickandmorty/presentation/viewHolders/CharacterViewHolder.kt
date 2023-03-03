package com.aston.rickandmorty.presentation.viewHolders

import androidx.recyclerview.widget.RecyclerView
import com.aston.rickandmorty.R
import com.aston.rickandmorty.databinding.CharacterItemBinding
import com.aston.rickandmorty.domain.entity.CharacterModel
import com.bumptech.glide.Glide

class CharacterViewHolder(
    private val binding: CharacterItemBinding
) : RecyclerView.ViewHolder(binding.root) {

    fun populate(data: CharacterModel, clickListener: ((id:Int)-> Unit)?) {
        //TODO binding.characterImage.setImageDrawable()
        Glide.with(binding.root)
            .load(data.image)
            .placeholder(R.drawable.toolbar_image)
            .into(binding.characterImage)
        binding.characterNameValue.text = data.name
        binding.characterGenderValue.text = data.gender
        binding.characterSpeciesValue.text = data.species
        binding.characterStatusValue.text = data.status
        binding.characterConstraintLayout.setOnClickListener {
            clickListener?.invoke(data.id)
        }
    }
}