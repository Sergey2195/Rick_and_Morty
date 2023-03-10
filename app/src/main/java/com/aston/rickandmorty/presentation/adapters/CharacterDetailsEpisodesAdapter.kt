package com.aston.rickandmorty.presentation.adapters

import android.annotation.SuppressLint
import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.aston.rickandmorty.databinding.EpisodeItemBinding
import com.aston.rickandmorty.domain.entity.EpisodeModel
import com.aston.rickandmorty.presentation.viewHolders.EpisodesViewHolder

class CharacterDetailsEpisodesAdapter : RecyclerView.Adapter<EpisodesViewHolder>() {

    private var data: List<EpisodeModel> = emptyList()
    var clickListener: ((id: Int) -> Unit)? = null

    @SuppressLint("NotifyDataSetChanged")
    fun submitData(list: List<EpisodeModel>) {
        Log.d("SSV_ADAPT", "list $list")
        data = list
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): EpisodesViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val binding = EpisodeItemBinding.inflate(inflater, parent, false)
        return EpisodesViewHolder(binding)
    }

    override fun getItemCount(): Int {
        return data.size
    }

    override fun onBindViewHolder(holder: EpisodesViewHolder, position: Int) {
        val item = data[position]
        holder.populate(item, clickListener)
    }
}