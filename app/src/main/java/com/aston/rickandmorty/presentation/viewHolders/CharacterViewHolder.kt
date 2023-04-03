package com.aston.rickandmorty.presentation.viewHolders

import androidx.recyclerview.widget.RecyclerView
import com.aston.rickandmorty.R
import com.aston.rickandmorty.databinding.CharacterItemBinding
import com.aston.rickandmorty.domain.entity.CharacterModel
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.RoundedCorners

class CharacterViewHolder(
    private val binding: CharacterItemBinding
) : RecyclerView.ViewHolder(binding.root) {

    fun populate(data: CharacterModel, clickListener: ((id: Int) -> Unit)?) {
        with(binding) {
            loadImage(data)
            characterNameValue.text = data.name
            characterGenderValue.text = data.gender
            characterSpeciesValue.text = data.species
            characterStatusValue.text = data.status
            characterConstraintLayout.setOnClickListener { clickListener?.invoke(data.id) }
        }
    }

    private fun loadImage(data: CharacterModel) {
        Glide.with(binding.root)
            .load(data.image)
            .placeholder(R.drawable.toolbar_image)
            .transform(RoundedCorners(20))
            .into(binding.characterImage)
    }
}