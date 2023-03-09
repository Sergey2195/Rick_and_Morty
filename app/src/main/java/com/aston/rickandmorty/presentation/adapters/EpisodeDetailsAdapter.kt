package com.aston.rickandmorty.presentation.adapters

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.aston.rickandmorty.R
import com.aston.rickandmorty.databinding.DetailsCharactersTypeBinding
import com.aston.rickandmorty.databinding.DetailsTitleValueBinding
import com.aston.rickandmorty.presentation.adapterModels.EpisodeDetailsModelAdapter
import com.aston.rickandmorty.presentation.viewHolders.EpisodeDetailsCharactersViewHolder
import com.aston.rickandmorty.presentation.viewHolders.EpisodeDetailsTitleValueViewHolder
import com.aston.rickandmorty.presentation.viewHolders.EpisodeDetailsViewHolder

class EpisodeDetailsAdapter: RecyclerView.Adapter<EpisodeDetailsViewHolder>() {

    private var data = emptyList<EpisodeDetailsModelAdapter>()
    var internalCharactersAdapter: DetailsCharactersAdapter? = null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): EpisodeDetailsViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        return when (viewType){
            R.layout.details_title_value ->{
                val binding = DetailsTitleValueBinding.inflate(inflater, parent, false)
                EpisodeDetailsTitleValueViewHolder(binding)
            }
            R.layout.details_characters_type->{
                val binding = DetailsCharactersTypeBinding.inflate(inflater, parent, false)
                binding.detailsCharactersRecyclerView.adapter = internalCharactersAdapter
                binding.detailsCharactersRecyclerView.layoutManager = GridLayoutManager(
                    parent.context,
                    2
                )
                binding.detailsCharactersRecyclerView.hasFixedSize()
                binding.detailsCharactersRecyclerView.isNestedScrollingEnabled = false
                EpisodeDetailsCharactersViewHolder(binding)
            }
            else -> throw RuntimeException("EpisodeDetailsAdapter unknown view type")
        }
    }

    override fun getItemCount(): Int {
        return data.size
    }

    @SuppressLint("NotifyDataSetChanged")
    fun submitList(list: List<EpisodeDetailsModelAdapter>){
        data = list
        notifyDataSetChanged()
    }

    override fun onBindViewHolder(holder: EpisodeDetailsViewHolder, position: Int) {
        holder.populate(data[position])
    }

    override fun getItemViewType(position: Int): Int {
        return data[position].viewType
    }
}