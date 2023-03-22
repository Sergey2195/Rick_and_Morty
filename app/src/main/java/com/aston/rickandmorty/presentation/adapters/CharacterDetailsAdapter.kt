package com.aston.rickandmorty.presentation.adapters

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.aston.rickandmorty.R
import com.aston.rickandmorty.databinding.CharacterDetailsImageBinding
import com.aston.rickandmorty.databinding.DetailsTitleValueBinding
import com.aston.rickandmorty.databinding.EpisodeItemBinding
import com.aston.rickandmorty.databinding.LocationItemBinding
import com.aston.rickandmorty.presentation.adapterModels.CharacterDetailsModelAdapter
import com.aston.rickandmorty.presentation.viewHolders.*

class CharacterDetailsAdapter : RecyclerView.Adapter<CharacterDetailsViewHolder>() {
    private var data: List<CharacterDetailsModelAdapter> = emptyList()
    var locationClickListener: ((id: Int) -> Unit)? = null
    var episodeClickListener: ((id: Int) -> Unit)? = null

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
            R.layout.details_title_value -> {
                val binding = DetailsTitleValueBinding.inflate(inflater, parent, false)
                CharacterDetailsViewHolderTitleValue(binding)
            }
            R.layout.location_item -> {
                val binding = LocationItemBinding.inflate(inflater, parent, false)
                CharacterDetailsViewHolderLocation(binding)
            }
            R.layout.episode_item -> {
                val binding = EpisodeItemBinding.inflate(inflater, parent, false)
                CharacterDetailsViewHolderEpisodes(binding)
            }
            else -> throw java.lang.RuntimeException("onCreateViewHolder CharacterDetailsAdapter unknown type")
        }
    }

    override fun getItemCount(): Int {
        return data.size
    }

    override fun onBindViewHolder(holder: CharacterDetailsViewHolder, position: Int) {
        val itemData = data[position]
        val clickListener = when (holder.itemViewType) {
            R.layout.location_item -> locationClickListener
            R.layout.episode_item -> episodeClickListener
            else -> null
        }
        holder.populate(itemData, clickListener)
    }

    override fun getItemViewType(position: Int): Int {
        return data[position].viewType
    }
}