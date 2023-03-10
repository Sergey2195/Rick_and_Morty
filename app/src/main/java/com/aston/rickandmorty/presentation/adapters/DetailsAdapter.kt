package com.aston.rickandmorty.presentation.adapters

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.aston.rickandmorty.R
import com.aston.rickandmorty.databinding.DetailsCharactersTypeBinding
import com.aston.rickandmorty.databinding.DetailsTitleValueBinding
import com.aston.rickandmorty.presentation.adapterModels.DetailsModelAdapter
import com.aston.rickandmorty.presentation.viewHolders.DetailsCharactersViewHolder
import com.aston.rickandmorty.presentation.viewHolders.DetailsTitleValueViewHolder
import com.aston.rickandmorty.presentation.viewHolders.DetailsViewHolder

class DetailsAdapter: RecyclerView.Adapter<DetailsViewHolder>() {

    private var data = emptyList<DetailsModelAdapter>()
    var internalCharactersAdapter: DetailsCharactersAdapter? = null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DetailsViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        return when (viewType){
            R.layout.details_title_value ->{
                val binding = DetailsTitleValueBinding.inflate(inflater, parent, false)
                DetailsTitleValueViewHolder(binding)
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
                DetailsCharactersViewHolder(binding)
            }
            else -> throw RuntimeException("DetailsAdapter unknown view type")
        }
    }

    override fun getItemCount(): Int {
        return data.size
    }

    @SuppressLint("NotifyDataSetChanged")
    fun submitList(list: List<DetailsModelAdapter>){
        data = list
        notifyDataSetChanged()
    }

    override fun onBindViewHolder(holder: DetailsViewHolder, position: Int) {
        holder.populate(data[position])
    }

    override fun getItemViewType(position: Int): Int {
        return data[position].viewType
    }
}