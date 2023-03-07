package com.aston.rickandmorty.presentation.viewHolders

import androidx.recyclerview.widget.RecyclerView
import com.aston.rickandmorty.R
import com.aston.rickandmorty.databinding.CharacterItemBinding
import com.aston.rickandmorty.domain.entity.CharacterModel
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import kotlinx.coroutines.delay

class CharacterViewHolder(
    private val binding: CharacterItemBinding
) : RecyclerView.ViewHolder(binding.root) {

    fun populate(data: CharacterModel, clickListener: ((id: Int) -> Unit)?) {
        Glide.with(binding.root)
            .load(data.image)
            .placeholder(R.drawable.toolbar_image)
            .transform(RoundedCorners(20))
            .into(binding.characterImage)
        binding.characterNameValue.text = data.name
        binding.characterGenderValue.text = data.gender
        binding.characterSpeciesValue.text = data.species
        binding.characterStatusValue.text = data.status
        binding.characterConstraintLayout.setOnClickListener {
            clickListener?.invoke(data.id)
        }
    }

    suspend fun animate() {
        binding.characterConstraintLayout.alpha = 0.2f
        delay(1000)
        binding.characterConstraintLayout.alpha = 1f
    }
}