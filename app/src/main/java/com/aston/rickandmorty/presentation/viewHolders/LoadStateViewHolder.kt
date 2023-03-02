package com.aston.rickandmorty.presentation.viewHolders

import android.view.View
import androidx.core.view.isVisible
import androidx.paging.LoadState
import androidx.recyclerview.widget.RecyclerView.ViewHolder
import com.aston.rickandmorty.databinding.DefaultLoadStateBinding

class LoadStateViewHolder(private val binding: DefaultLoadStateBinding) : ViewHolder(binding.root) {

    fun populate(loadState: LoadState, clickListener: () -> Unit) {
        binding.tryAgainButton.setOnClickListener {
            clickListener.invoke()
        }
        binding.loadStateProgressBar.isVisible = loadState is LoadState.Loading
        binding.errorTextView.isVisible = loadState is LoadState.Error
        binding.tryAgainButton.isVisible = loadState is LoadState.Error
    }
}