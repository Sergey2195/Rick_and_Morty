package com.aston.rickandmorty.presentation.viewHolders

import android.view.View
import androidx.recyclerview.widget.RecyclerView.ViewHolder
import com.aston.rickandmorty.databinding.DetailsCharactersTypeBinding
import com.aston.rickandmorty.databinding.DetailsTitleValueBinding
import com.aston.rickandmorty.presentation.adapterModels.EpisodeDetailsModelAdapter
import com.aston.rickandmorty.presentation.adapterModels.EpisodeDetailsModelCharacterList
import com.aston.rickandmorty.presentation.adapterModels.EpisodeDetailsModelTitleValue
import com.aston.rickandmorty.presentation.adapters.DetailsCharactersAdapter

abstract class EpisodeDetailsViewHolder(val view: View) : ViewHolder(view) {
    abstract fun populate(data: EpisodeDetailsModelAdapter)
}

class EpisodeDetailsTitleValueViewHolder(private val binding: DetailsTitleValueBinding) :
    EpisodeDetailsViewHolder(binding.root) {

    override fun populate(data: EpisodeDetailsModelAdapter) {
        val typedData = data as EpisodeDetailsModelTitleValue
        binding.titleTextView.text = typedData.title
        binding.valueTextView.text = typedData.value
    }
}

class EpisodeDetailsCharactersViewHolder(private val binding: DetailsCharactersTypeBinding) :
    EpisodeDetailsViewHolder(binding.root) {

    override fun populate(data: EpisodeDetailsModelAdapter) {
        (binding.detailsCharactersRecyclerView.adapter as DetailsCharactersAdapter)
            .submitData((data as EpisodeDetailsModelCharacterList).listCharacters)
    }
}

