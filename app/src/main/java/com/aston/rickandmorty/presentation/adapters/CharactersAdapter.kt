package com.aston.rickandmorty.presentation.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.paging.PagingDataAdapter
import androidx.recyclerview.widget.ListAdapter
import com.aston.rickandmorty.databinding.CharacterItemBinding
import com.aston.rickandmorty.domain.entity.CharacterModel
import com.aston.rickandmorty.presentation.diffUtils.CharacterDiffUtilsCallback
import com.aston.rickandmorty.presentation.viewHolders.CharacterViewHolder

class CharactersAdapter: PagingDataAdapter<CharacterModel, CharacterViewHolder>(CharacterDiffUtilsCallback()) {

    var clickListener: ((id: Int)-> Unit)? = null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CharacterViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val binding = CharacterItemBinding.inflate(inflater, parent, false)
        return CharacterViewHolder(binding)
    }

    override fun onBindViewHolder(holder: CharacterViewHolder, position: Int) {
        val data = getItem(position) ?: return
        holder.populate(data, clickListener)
    }

    fun findPosition(request: String): List<Int>{
        val requestUppercase = request.uppercase()
        val listOfData = snapshot().items
        val resultIndices = arrayListOf<Int>()
        for ((index, element) in listOfData.withIndex()){
            if (validResult(requestUppercase, element)){
                resultIndices.add(index)
            }
        }
        return resultIndices
    }

    private fun validResult(str: String, model: CharacterModel): Boolean{
        return model.name.uppercase().contains(str) ||
                model.status.uppercase().contains(str) ||
                model.species.uppercase().contains(str) ||
                model.gender.uppercase().contains(str)
    }
}