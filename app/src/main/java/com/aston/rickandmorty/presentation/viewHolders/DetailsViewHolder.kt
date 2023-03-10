package com.aston.rickandmorty.presentation.viewHolders

import android.view.View
import androidx.recyclerview.widget.RecyclerView.ViewHolder
import com.aston.rickandmorty.databinding.DetailsCharactersTypeBinding
import com.aston.rickandmorty.databinding.DetailsTitleValueBinding
import com.aston.rickandmorty.presentation.adapterModels.DetailsModelAdapter
import com.aston.rickandmorty.presentation.adapterModels.DetailsModelCharacterList
import com.aston.rickandmorty.presentation.adapterModels.DetailsModelTitleValue
import com.aston.rickandmorty.presentation.adapters.DetailsCharactersAdapter

abstract class DetailsViewHolder(val view: View) : ViewHolder(view) {
    abstract fun populate(data: DetailsModelAdapter)
}

class DetailsTitleValueViewHolder(private val binding: DetailsTitleValueBinding) :
    DetailsViewHolder(binding.root) {

    override fun populate(data: DetailsModelAdapter) {
        val typedData = data as DetailsModelTitleValue
        binding.titleTextView.text = typedData.title
        binding.valueTextView.text = typedData.value
    }
}

class DetailsCharactersViewHolder(private val binding: DetailsCharactersTypeBinding) :
    DetailsViewHolder(binding.root) {

    override fun populate(data: DetailsModelAdapter) {
        (binding.detailsCharactersRecyclerView.adapter as DetailsCharactersAdapter)
            .submitData((data as DetailsModelCharacterList).listCharacters)
    }
}

