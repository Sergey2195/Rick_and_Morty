package com.aston.rickandmorty.presentation.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.paging.PagingDataAdapter
import com.aston.rickandmorty.databinding.LocationItemBinding
import com.aston.rickandmorty.domain.entity.LocationModel
import com.aston.rickandmorty.presentation.diffUtils.LocationsDiffUtilsCallback
import com.aston.rickandmorty.presentation.viewHolders.LocationsViewHolder

class LocationsAdapter :
    PagingDataAdapter<LocationModel, LocationsViewHolder>(LocationsDiffUtilsCallback()) {

    var clickListener: ((id: Int) -> Unit)? = null

    override fun onBindViewHolder(holder: LocationsViewHolder, position: Int) {
        val item = getItem(position) ?: return
        holder.populate(item, clickListener)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): LocationsViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val binding = LocationItemBinding.inflate(inflater, parent, false)
        return LocationsViewHolder(binding)
    }
}