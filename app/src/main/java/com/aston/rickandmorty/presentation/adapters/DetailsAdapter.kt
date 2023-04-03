package com.aston.rickandmorty.presentation.adapters

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.aston.rickandmorty.R
import com.aston.rickandmorty.databinding.CharacterItemBinding
import com.aston.rickandmorty.databinding.DetailsTextBinding
import com.aston.rickandmorty.presentation.adapterModels.DetailsModelAdapter
import com.aston.rickandmorty.presentation.viewHolders.DetailsCharacterViewHolder
import com.aston.rickandmorty.presentation.viewHolders.DetailsTextViewHolder
import com.aston.rickandmorty.presentation.viewHolders.DetailsViewHolder

class DetailsAdapter : RecyclerView.Adapter<DetailsViewHolder>() {

    private var data = emptyList<DetailsModelAdapter>()
    var clickListener: ((id: Int) -> Unit)? = null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DetailsViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        return when (viewType) {
            R.layout.details_text -> {
                val binding = DetailsTextBinding.inflate(inflater, parent, false)
                DetailsTextViewHolder(binding)
            }
            R.layout.character_item -> {
                val binding = CharacterItemBinding.inflate(inflater, parent, false)
                DetailsCharacterViewHolder(binding)
            }
            else -> throw RuntimeException("DetailsAdapter unknown view type")
        }
    }

    override fun getItemCount(): Int {
        return data.size
    }

    @SuppressLint("NotifyDataSetChanged")
    fun submitList(list: List<DetailsModelAdapter>) {
        data = list
        notifyDataSetChanged()
    }

    override fun onBindViewHolder(holder: DetailsViewHolder, position: Int) {
        val typeClickListener =
            if (data[position].viewType == R.layout.character_item) clickListener else null
        holder.populate(data[position], typeClickListener)
    }

    override fun getItemViewType(position: Int): Int {
        return data[position].viewType
    }
}