package com.aston.rickandmorty.presentation.viewHolders

import android.content.res.Resources
import android.view.RoundedCorner
import android.view.animation.Transformation
import androidx.constraintlayout.widget.ConstraintSet.Transform
import androidx.recyclerview.widget.RecyclerView
import com.aston.rickandmorty.R
import com.aston.rickandmorty.databinding.CharacterItemBinding
import com.aston.rickandmorty.domain.entity.CharacterModel
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.CenterCrop
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.bumptech.glide.request.RequestOptions

class CharacterViewHolder(
    private val binding: CharacterItemBinding
) : RecyclerView.ViewHolder(binding.root) {

    fun populate(data: CharacterModel, clickListener: ((id:Int)-> Unit)?) {
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
}