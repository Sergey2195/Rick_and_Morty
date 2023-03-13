package com.aston.rickandmorty.presentation.adapters

import android.annotation.SuppressLint
import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.aston.rickandmorty.R
import com.aston.rickandmorty.databinding.CharacterDetailsImageBinding
import com.aston.rickandmorty.databinding.DetailsTitleValueBinding
import com.aston.rickandmorty.databinding.LocationDetailsResidentsBinding
import com.aston.rickandmorty.presentation.adapterModels.CharacterDetailsModelAdapter
import com.aston.rickandmorty.presentation.adapterModels.LocationDetailsModelAdapter
import com.aston.rickandmorty.presentation.viewHolders.*

class LocationDetailsAdapter : RecyclerView.Adapter<LocationDetailsViewHolder>(){

    private var data: List<LocationDetailsModelAdapter> = emptyList()

    @SuppressLint("NotifyDataSetChanged")
    fun submitList(list: List<LocationDetailsModelAdapter>) {
        data = list
        Log.d("SSV_ADAPT", data.toString())
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): LocationDetailsViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        return when (viewType) {
            R.layout.details_title_value -> {
                val binding = DetailsTitleValueBinding.inflate(inflater, parent, false)
                LocationDetailsViewHolderTitleValue(binding)
            }
            R.layout.location_details_residents ->{
                val binding = LocationDetailsResidentsBinding.inflate(inflater, parent, false)
                LocationDetailsViewHolderResident(binding)
            }
            else -> throw java.lang.RuntimeException("onCreateViewHolder CharacterDetailsAdapter unknown type")
        }
    }

    override fun getItemCount(): Int {
        return data.size
    }

    override fun getItemViewType(position: Int): Int {
        return data[position].viewType
    }

    override fun onBindViewHolder(holder: LocationDetailsViewHolder, position: Int) {
        val itemData = data[position]
        holder.populate(itemData)
    }
}