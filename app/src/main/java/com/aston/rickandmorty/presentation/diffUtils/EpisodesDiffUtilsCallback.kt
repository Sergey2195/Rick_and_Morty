package com.aston.rickandmorty.presentation.diffUtils

import androidx.recyclerview.widget.DiffUtil
import com.aston.rickandmorty.domain.entity.EpisodeModel

class EpisodesDiffUtilsCallback: DiffUtil.ItemCallback<EpisodeModel>() {
    override fun areItemsTheSame(oldItem: EpisodeModel, newItem: EpisodeModel): Boolean {
        return oldItem.id == newItem.id
    }

    override fun areContentsTheSame(oldItem: EpisodeModel, newItem: EpisodeModel): Boolean {
        return oldItem == newItem
    }
}