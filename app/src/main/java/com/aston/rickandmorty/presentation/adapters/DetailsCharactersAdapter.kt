package com.aston.rickandmorty.presentation.adapters

import android.annotation.SuppressLint
import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView.Adapter
import com.aston.rickandmorty.databinding.CharacterItemBinding
import com.aston.rickandmorty.domain.entity.CharacterModel
import com.aston.rickandmorty.presentation.viewHolders.CharacterViewHolder

class DetailsCharactersAdapter: Adapter<CharacterViewHolder>() {

    private var data = emptyList<CharacterModel>()
    var clickListener: ((id: Int) -> Unit)? = null

    @SuppressLint("NotifyDataSetChanged")
    fun submitData(list: List<CharacterModel>) {
        Log.d("SSV_REC", "submitData")
        data = list
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CharacterViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val binding = CharacterItemBinding.inflate(inflater, parent, false)
        return CharacterViewHolder(binding)
    }

    override fun getItemCount(): Int {
        return data.size
    }

    override fun onBindViewHolder(holder: CharacterViewHolder, position: Int) {
        Log.d("SSV_REC", "onBindViewHolder")
        holder.populate(data[position], clickListener)
    }
}