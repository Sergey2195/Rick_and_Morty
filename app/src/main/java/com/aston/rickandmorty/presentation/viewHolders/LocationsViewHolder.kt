package com.aston.rickandmorty.presentation.viewHolders

import androidx.recyclerview.widget.RecyclerView
import com.aston.rickandmorty.databinding.LocationItemBinding
import com.aston.rickandmorty.domain.entity.LocationModel

class LocationsViewHolder(private val binding: LocationItemBinding) :
    RecyclerView.ViewHolder(binding.root) {

    fun populate(data: LocationModel, clickListener: ((id: Int) -> Unit)?) {
        with(binding) {
            locationNameValue.text = data.name
            locationTypeValue.text = data.type
            locationDimensionValue.text = data.dimension
            locationConstraintLayout.setOnClickListener {
                clickListener?.invoke(data.id)
            }
        }
    }
}