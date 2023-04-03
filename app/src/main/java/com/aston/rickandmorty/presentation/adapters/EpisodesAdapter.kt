package com.aston.rickandmorty.presentation.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.paging.PagingDataAdapter
import com.aston.rickandmorty.databinding.EpisodeItemBinding
import com.aston.rickandmorty.domain.entity.EpisodeModel
import com.aston.rickandmorty.presentation.diffUtils.EpisodesDiffUtilsCallback
import com.aston.rickandmorty.presentation.viewHolders.EpisodesViewHolder

class EpisodesAdapter :
    PagingDataAdapter<EpisodeModel, EpisodesViewHolder>(EpisodesDiffUtilsCallback()) {

    var clickListener: ((id: Int) -> Unit)? = null

    override fun onBindViewHolder(holder: EpisodesViewHolder, position: Int) {
        val item = getItem(position) ?: return
        holder.populate(item, clickListener)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): EpisodesViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val binding = EpisodeItemBinding.inflate(inflater, parent, false)
        return EpisodesViewHolder(binding)
    }
}