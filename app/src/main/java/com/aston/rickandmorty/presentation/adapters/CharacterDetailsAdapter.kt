package com.aston.rickandmorty.presentation.adapters

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.aston.rickandmorty.R
import com.aston.rickandmorty.databinding.CharacterDetailsImageBinding
import com.aston.rickandmorty.databinding.CharacterDetailsItemBinding
import com.aston.rickandmorty.presentation.adapterModels.CharacterDetailsModelAdapter
import com.aston.rickandmorty.presentation.viewHolders.CharacterDetailsViewHolder
import com.aston.rickandmorty.presentation.viewHolders.CharacterDetailsViewHolderImage
import com.aston.rickandmorty.presentation.viewHolders.CharacterDetailsViewHolderTitleValue

class CharacterDetailsAdapter : RecyclerView.Adapter<CharacterDetailsViewHolder>() {
    private var data: List<CharacterDetailsModelAdapter> = emptyList()

    @SuppressLint("NotifyDataSetChanged")
    fun submitList(list: List<CharacterDetailsModelAdapter>) {
        data = list
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CharacterDetailsViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        return when (viewType) {
            R.layout.character_details_image -> {
                val binding = CharacterDetailsImageBinding.inflate(inflater, parent, false)
                CharacterDetailsViewHolderImage(binding)
            }
            R.layout.character_details_item -> {
                val binding = CharacterDetailsItemBinding.inflate(inflater, parent, false)
                CharacterDetailsViewHolderTitleValue(binding)
            }
            else -> throw java.lang.RuntimeException("onCreateViewHolder CharacterDetailsAdapter unknown type")
        }
    }

    override fun getItemCount(): Int {
        return data.size
    }

    override fun onBindViewHolder(holder: CharacterDetailsViewHolder, position: Int) {
        val itemData = data[position]
        holder.populate(itemData)
    }

    override fun getItemViewType(position: Int): Int {
        return data[position].viewType
    }
}