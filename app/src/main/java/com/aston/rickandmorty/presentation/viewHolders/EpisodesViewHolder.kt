package com.aston.rickandmorty.presentation.viewHolders

import androidx.recyclerview.widget.RecyclerView.ViewHolder
import com.aston.rickandmorty.databinding.EpisodeItemBinding
import com.aston.rickandmorty.domain.entity.EpisodeModel

class EpisodesViewHolder(private val binding: EpisodeItemBinding) : ViewHolder(binding.root) {

    fun populate(data: EpisodeModel, clickListener: ((id: Int) -> Unit)?) {
        binding.episodeConstraintLayout.setOnClickListener { clickListener?.invoke(data.id) }
        binding.episodeNameValue.text = data.name
        binding.episodeNumberValue.text = data.number
        binding.airDateValue.text = data.dateRelease
    }
}