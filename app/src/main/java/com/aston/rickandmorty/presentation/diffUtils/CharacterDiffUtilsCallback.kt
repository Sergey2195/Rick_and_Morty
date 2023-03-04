package com.aston.rickandmorty.presentation.diffUtils

import androidx.recyclerview.widget.DiffUtil
import com.aston.rickandmorty.domain.entity.CharacterModel

class CharacterDiffUtilsCallback : DiffUtil.ItemCallback<CharacterModel>() {
    override fun areItemsTheSame(oldItem: CharacterModel, newItem: CharacterModel): Boolean {
        return oldItem.id == newItem.id
    }

    override fun areContentsTheSame(oldItem: CharacterModel, newItem: CharacterModel): Boolean {
        return oldItem == newItem
    }
}