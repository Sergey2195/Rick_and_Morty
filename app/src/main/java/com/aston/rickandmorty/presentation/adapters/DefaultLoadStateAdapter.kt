package com.aston.rickandmorty.presentation.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.paging.LoadState
import androidx.paging.LoadStateAdapter
import com.aston.rickandmorty.databinding.DefaultLoadStateBinding
import com.aston.rickandmorty.presentation.viewHolders.LoadStateViewHolder

class DefaultLoadStateAdapter(
    private val tryAgain: ()-> Unit
): LoadStateAdapter<LoadStateViewHolder>() {
    override fun onBindViewHolder(holder: LoadStateViewHolder, loadState: LoadState) {
        holder.populate(loadState, tryAgain)
    }

    override fun onCreateViewHolder(parent: ViewGroup, loadState: LoadState): LoadStateViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val binding = DefaultLoadStateBinding.inflate(inflater, parent, false)
        return LoadStateViewHolder(binding)
    }
}