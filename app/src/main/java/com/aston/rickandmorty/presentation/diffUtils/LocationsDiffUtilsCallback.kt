package com.aston.rickandmorty.presentation.diffUtils

import androidx.recyclerview.widget.DiffUtil
import com.aston.rickandmorty.domain.entity.LocationModel

class LocationsDiffUtilsCallback: DiffUtil.ItemCallback<LocationModel>() {
    override fun areItemsTheSame(oldItem: LocationModel, newItem: LocationModel): Boolean {
        return oldItem.id == newItem.id
    }

    override fun areContentsTheSame(oldItem: LocationModel, newItem: LocationModel): Boolean {
        return oldItem == newItem
    }
}