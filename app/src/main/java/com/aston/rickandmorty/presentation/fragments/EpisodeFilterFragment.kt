package com.aston.rickandmorty.presentation.fragments

import android.os.Bundle
import android.view.View
import androidx.core.widget.doOnTextChanged
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import com.aston.rickandmorty.R
import com.aston.rickandmorty.databinding.FragmentEpisodeFilterBinding
import com.aston.rickandmorty.domain.entity.EpisodeFilterModel
import com.aston.rickandmorty.presentation.App
import com.aston.rickandmorty.presentation.activities.MainActivity
import com.aston.rickandmorty.presentation.viewModels.EpisodeFilterViewModel
import com.aston.rickandmorty.presentation.viewModels.EpisodesViewModel
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import java.util.concurrent.TimeUnit

class EpisodeFilterFragment : BaseFilterFragment<FragmentEpisodeFilterBinding>(
    R.layout.fragment_episode_filter,
    FragmentEpisodeFilterBinding::inflate
) {

    private val viewModel: EpisodesViewModel by viewModels({ activity as MainActivity }) {
        viewModelFactory
    }
    private val filterViewModel: EpisodeFilterViewModel by viewModels {
        viewModelFactory
    }
    private val resultFilter = EpisodeFilterModel()

    override fun initArguments() {
        arguments?.let {
            mode = it.getInt(MODE)
        }
    }

    override fun setUI() {
        val filterGroupVisibility = when (mode) {
            FILTER -> View.VISIBLE
            SEARCH -> View.GONE
            else -> throw RuntimeException("unknown mode")
        }
        binding.filterGroup.visibility = filterGroupVisibility
        setupClickListener()
        sendCheckCountOfEpisodesWithInterval()
    }

    override fun setRefreshLayoutListener() {
    }

    private fun sendCheckCountOfEpisodesWithInterval() {
        val disposable = publishSubject.debounce(1, TimeUnit.SECONDS)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe { filterViewModel.sendFilters(resultFilter) }
        compositeDisposable.add(disposable)
    }

    override fun setupObservers() {
        lifecycleScope.launchWhenStarted {
            filterViewModel.countOfEpisodesStateFlow.collect { count ->
                val text = when (count) {
                    0 -> if (mode == FILTER) requireContext().getString(R.string.filter_initial) else requireContext().getString(
                        R.string.search_initial
                    )
                    -1 -> requireContext().getString(R.string.search_not_found)
                    else -> requireContext().getString(R.string.search_count, count)
                }
                binding.countResultTextView.text = text
            }
        }
    }

    override fun injectDependencies() {
        App.getAppComponent().injectEpisodeFilterFragment(this)
    }

    private fun setupClickListener() {
        binding.saveButton.setOnClickListener { viewModel.setFilter(resultFilter) }
        binding.nameEditText.doOnTextChanged { text, _, _, _ ->
            resultFilter.name = textFilter(text)
            publishSubject.onNext(Unit)
        }
        binding.episodeEditText.doOnTextChanged { text, _, _, _ ->
            resultFilter.episode = textFilter(text)
            publishSubject.onNext(Unit)
        }
    }

    private fun textFilter(text: CharSequence?): String? {
        return if (text != null && text.isNotBlank()) {
            text.toString()
        } else null
    }

    companion object {
        const val FILTER = 1
        const val SEARCH = 0
        private const val MODE = "mode"

        fun newInstance(mode: Int) =
            EpisodeFilterFragment().apply {
                arguments = Bundle().apply {
                    putInt(MODE, mode)
                }
            }
    }
}