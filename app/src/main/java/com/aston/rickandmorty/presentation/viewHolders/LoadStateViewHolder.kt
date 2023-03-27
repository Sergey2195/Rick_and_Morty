package com.aston.rickandmorty.presentation.viewHolders

import androidx.core.view.isVisible
import androidx.paging.LoadState
import androidx.recyclerview.widget.RecyclerView.ViewHolder
import com.aston.rickandmorty.databinding.DefaultLoadStateBinding

class LoadStateViewHolder(private val binding: DefaultLoadStateBinding) : ViewHolder(binding.root) {

    fun populate(loadState: LoadState, clickListener: () -> Unit) {
        with(binding) {
            tryAgainButton.setOnClickListener { clickListener.invoke() }
            loadStateProgressBar.isVisible = loadState is LoadState.Loading
            errorTextView.isVisible = loadState is LoadState.Error
            tryAgainButton.isVisible = loadState is LoadState.Error
        }
    }
}