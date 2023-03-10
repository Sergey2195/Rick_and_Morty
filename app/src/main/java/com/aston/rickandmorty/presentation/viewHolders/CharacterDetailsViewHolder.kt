package com.aston.rickandmorty.presentation.viewHolders

import android.view.View
import androidx.recyclerview.widget.RecyclerView.ViewHolder
import com.aston.rickandmorty.R
import com.aston.rickandmorty.databinding.CharacterDetailsEpisodesTypeBinding
import com.aston.rickandmorty.databinding.CharacterDetailsImageBinding
import com.aston.rickandmorty.databinding.DetailsTitleValueBinding
import com.aston.rickandmorty.databinding.LocationItemBinding
import com.aston.rickandmorty.presentation.adapterModels.*
import com.aston.rickandmorty.presentation.adapters.CharacterDetailsEpisodesAdapter
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.RoundedCorners

abstract class CharacterDetailsViewHolder(view: View) : ViewHolder(view) {
    abstract fun populate(
        itemData: CharacterDetailsModelAdapter,
        clickListener: ((id: Int) -> Unit)?
    )
}

class CharacterDetailsViewHolderImage(private val binding: CharacterDetailsImageBinding) :
    CharacterDetailsViewHolder(binding.root) {
    override fun populate(
        itemData: CharacterDetailsModelAdapter,
        clickListener: ((id: Int) -> Unit)?
    ) {
        val data = itemData as CharacterDetailsImageModelAdapter
        Glide.with(binding.root)
            .load(data.imageUrl)
            .placeholder(R.drawable.toolbar_image)
            .transform(RoundedCorners(20))
            .into(binding.characterDetailsImageView)
    }

}

class CharacterDetailsViewHolderTitleValue(private val binding: DetailsTitleValueBinding) :
    CharacterDetailsViewHolder(binding.root) {
    override fun populate(
        itemData: CharacterDetailsModelAdapter,
        clickListener: ((id: Int) -> Unit)?
    ) {
        val data = itemData as CharacterDetailsTitleValueModelAdapter
        binding.titleTextView.text = data.title
        binding.valueTextView.text = data.value
    }
}

class CharacterDetailsViewHolderLocation(private val binding: LocationItemBinding) :
    CharacterDetailsViewHolder(binding.root) {
    override fun populate(
        itemData: CharacterDetailsModelAdapter,
        clickListener: ((id: Int) -> Unit)?
    ) {
        val data = itemData as CharacterDetailsLocationModelAdapter
        binding.locationNameValue.text = data.locationModel.name
        binding.locationTypeValue.text = data.locationModel.type
        binding.locationDimensionValue.text = data.locationModel.dimension
        binding.locationConstraintLayout.setOnClickListener {
            clickListener?.invoke(data.locationModel.id)
        }
    }
}

class CharacterDetailsViewHolderEpisodes(private val binding: CharacterDetailsEpisodesTypeBinding) :
    CharacterDetailsViewHolder(binding.root) {
    override fun populate(
        itemData: CharacterDetailsModelAdapter,
        clickListener: ((id: Int) -> Unit)?
    ) {
        val data = itemData as CharacterDetailsEpisodesModelAdapter
        (binding.characterDetailsEpisodesRecyclerView.adapter as CharacterDetailsEpisodesAdapter)
            .submitData(data.episodesModel)
        (binding.characterDetailsEpisodesRecyclerView.adapter as CharacterDetailsEpisodesAdapter).clickListener =
            clickListener
    }

}