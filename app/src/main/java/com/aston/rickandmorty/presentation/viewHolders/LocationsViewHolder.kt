package com.aston.rickandmorty.presentation.viewHolders

import androidx.recyclerview.widget.RecyclerView
import com.aston.rickandmorty.databinding.LocationItemBinding
import com.aston.rickandmorty.domain.entity.LocationModel

class LocationsViewHolder(private val binding: LocationItemBinding) :
    RecyclerView.ViewHolder(binding.root) {
    fun populate(data: LocationModel, clickListener: ((id: Int) -> Unit)?) {
        binding.locationNameValue.text = data.name
        binding.locationTypeValue.text = data.type
        binding.locationDimensionValue.text = data.dimension
        binding.locationConstraintLayout.setOnClickListener {
            clickListener?.invoke(data.id)
        }
    }
}