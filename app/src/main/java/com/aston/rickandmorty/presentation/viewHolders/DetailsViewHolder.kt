package com.aston.rickandmorty.presentation.viewHolders

import android.view.View
import androidx.recyclerview.widget.RecyclerView.ViewHolder
import com.aston.rickandmorty.R
import com.aston.rickandmorty.databinding.CharacterItemBinding
import com.aston.rickandmorty.databinding.DetailsTextBinding
import com.aston.rickandmorty.presentation.adapterModels.DetailsModelAdapter
import com.aston.rickandmorty.presentation.adapterModels.DetailsModelCharacter
import com.aston.rickandmorty.presentation.adapterModels.DetailsModelText
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.RoundedCorners

abstract class DetailsViewHolder(val view: View) : ViewHolder(view) {
    abstract fun populate(data: DetailsModelAdapter, clickListener: ((id: Int) -> Unit)?)
}

class DetailsTextViewHolder(private val binding: DetailsTextBinding) :
    DetailsViewHolder(binding.root) {

    override fun populate(data: DetailsModelAdapter, clickListener: ((id: Int) -> Unit)?) {
        val typedData = data as DetailsModelText
        binding.detailsTextView.text = typedData.text
    }
}

class DetailsCharacterViewHolder(private val binding: CharacterItemBinding): DetailsViewHolder(binding.root){
    override fun populate(data: DetailsModelAdapter, clickListener: ((id: Int) -> Unit)?) {
        val typedData = data as DetailsModelCharacter
        Glide.with(binding.root)
            .load(typedData.characterData.image)
            .placeholder(R.drawable.toolbar_image)
            .transform(RoundedCorners(20))
            .into(binding.characterImage)
        binding.characterNameValue.text = typedData.characterData.name
        binding.characterGenderValue.text = typedData.characterData.gender
        binding.characterSpeciesValue.text = typedData.characterData.species
        binding.characterStatusValue.text = typedData.characterData.status
        binding.characterConstraintLayout.setOnClickListener {
            clickListener?.invoke(typedData.characterData.id)
        }
    }
}

