package com.aston.rickandmorty.presentation.viewHolders

import android.view.View
import androidx.recyclerview.widget.RecyclerView.ViewHolder
import com.aston.rickandmorty.R
import com.aston.rickandmorty.databinding.DetailsTitleValueBinding
import com.aston.rickandmorty.databinding.LocationDetailsResidentsBinding
import com.aston.rickandmorty.presentation.adapterModels.LocationDetailsModelAdapter
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.RoundedCorners

abstract class LocationDetailsViewHolder(view: View): ViewHolder(view) {
    abstract fun populate(itemData: LocationDetailsModelAdapter)
}

class LocationDetailsViewHolderTitleValue(private val binding: DetailsTitleValueBinding): LocationDetailsViewHolder(binding.root){
    override fun populate(itemData: LocationDetailsModelAdapter) {
        binding.titleTextView.text = itemData.title
        binding.valueTextView.text = itemData.value
    }
}

class LocationDetailsViewHolderResident(private val binding: LocationDetailsResidentsBinding): LocationDetailsViewHolder(binding.root){
    override fun populate(itemData: LocationDetailsModelAdapter) {
        Glide.with(binding.root)
            .load(itemData.url)
            .placeholder(R.drawable.toolbar_image)
            .transform(RoundedCorners(20))
            .into(binding.residentImageView)
        binding.residentCharacterName.text = itemData.value
    }

}