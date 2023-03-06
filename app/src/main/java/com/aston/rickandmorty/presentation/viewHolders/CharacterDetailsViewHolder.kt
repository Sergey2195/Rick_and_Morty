package com.aston.rickandmorty.presentation.viewHolders

import android.view.View
import androidx.recyclerview.widget.RecyclerView.ViewHolder
import com.aston.rickandmorty.R
import com.aston.rickandmorty.databinding.CharacterDetailsImageBinding
import com.aston.rickandmorty.databinding.CharacterDetailsItemBinding
import com.aston.rickandmorty.presentation.adapterModels.CharacterDetailsModelAdapter
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.RoundedCorners

abstract class CharacterDetailsViewHolder(view: View) : ViewHolder(view) {
    abstract fun populate(itemData: CharacterDetailsModelAdapter)
}

class CharacterDetailsViewHolderImage(private val binding: CharacterDetailsImageBinding) :
    CharacterDetailsViewHolder(binding.root) {
    override fun populate(itemData: CharacterDetailsModelAdapter) {
        Glide.with(binding.root)
            .load(itemData.value)
            .placeholder(R.drawable.toolbar_image)
            .transform(RoundedCorners(20))
            .into(binding.characterDetailsImageView)
    }

}

class CharacterDetailsViewHolderTitleValue(private val binding: CharacterDetailsItemBinding) :
    CharacterDetailsViewHolder(binding.root) {
    override fun populate(itemData: CharacterDetailsModelAdapter) = with(binding) {
        titleTextView.text = itemData.title
        valueTextView.text = itemData.value
        if (itemData.isClickable){
            //todo url like
        }else{

        }
    }
}