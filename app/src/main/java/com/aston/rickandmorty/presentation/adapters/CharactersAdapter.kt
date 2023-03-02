package com.aston.rickandmorty.presentation.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.paging.PagingDataAdapter
import androidx.recyclerview.widget.ListAdapter
import com.aston.rickandmorty.databinding.CharacterItemBinding
import com.aston.rickandmorty.domain.entity.CharacterModel
import com.aston.rickandmorty.presentation.diffUtils.CharacterDiffUtilsCallback
import com.aston.rickandmorty.presentation.viewHolders.CharacterViewHolder

class CharactersAdapter: PagingDataAdapter<CharacterModel, CharacterViewHolder>(CharacterDiffUtilsCallback()) {

    var clickListener: ((id: Int)-> Unit)? = null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CharacterViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val binding = CharacterItemBinding.inflate(inflater, parent, false)
        return CharacterViewHolder(binding)
    }

    override fun onBindViewHolder(holder: CharacterViewHolder, position: Int) {
        val data = getItem(position) ?: return
        holder.populate(data, clickListener)
    }
}