package com.aston.rickandmorty.presentation.fragments

import android.os.Bundle
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import com.aston.rickandmorty.R
import com.aston.rickandmorty.databinding.FragmentEpisodesRootBinding
import com.aston.rickandmorty.domain.entity.EpisodeFilterModel
import com.aston.rickandmorty.presentation.App
import com.aston.rickandmorty.presentation.activities.MainActivity
import com.aston.rickandmorty.presentation.viewModels.EpisodesViewModel
import com.aston.rickandmorty.toolbarManager.ToolbarManager
import kotlinx.coroutines.flow.filterNotNull

class EpisodesRootFragment : BaseFragment<FragmentEpisodesRootBinding>(
    R.layout.fragment_episodes_root,
    FragmentEpisodesRootBinding::inflate
) {

    private val episodeViewModel: EpisodesViewModel by viewModels({ activity as MainActivity }) {
        viewModelFactory
    }

    override fun injectDependencies() {
        App.getAppComponent().injectEpisodesRootFragment(this)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        if (savedInstanceState == null) {
            childFragmentManager.beginTransaction()
                .add(R.id.episodeFragmentContainerRoot, EpisodesAllFragment.newInstance())
                .commit()
        }
    }

    override fun setUI() {
        setupBackButtonClickListener()
        setupFilterButtonClickListener()
        setupSearchButtonClickListener()
    }

    override fun setupObservers() {
        lifecycleScope.launchWhenStarted {
            episodeViewModel.episodeFilterStateFlow.filterNotNull().collect {
                startFragmentWithFiltering(it)
                episodeViewModel.clearFilter()
            }
        }
    }

    private fun startFragmentWithFiltering(filter: EpisodeFilterModel) {
        val fragment = EpisodesAllFragment.newInstance(filter.name, filter.episode)
        childFragmentManager.popBackStack()
        childFragmentManager.beginTransaction()
            .setCustomAnimations(R.anim.from_bot, R.anim.to_top, R.anim.from_top, R.anim.to_bot)
            .replace(R.id.episodeFragmentContainerRoot, fragment)
            .addToBackStack(null)
            .commit()
    }

    private fun setupBackButtonClickListener() {
        (requireActivity() as ToolbarManager).setBackButtonClickListener {
            childFragmentManager.popBackStack()
        }
    }

    private fun setupFilterButtonClickListener() {
        (requireActivity() as ToolbarManager).setFilterButtonClickListener {
            startFilterOrSearchFragment(EpisodeFilterFragment.FILTER)
        }
    }

    private fun setupSearchButtonClickListener() {
        (requireActivity() as ToolbarManager).setSearchButtonClickListener {
            startFilterOrSearchFragment(EpisodeFilterFragment.SEARCH)
        }
    }

    private fun startFilterOrSearchFragment(mode: Int) {
        childFragmentManager.beginTransaction()
            .setCustomAnimations(R.anim.from_bot, R.anim.to_top, R.anim.from_top, R.anim.to_bot)
            .replace(R.id.episodeFragmentContainerRoot, EpisodeFilterFragment.newInstance(mode))
            .addToBackStack(null)
            .commit()
    }

    override fun initArguments() {
    }

    override fun setRefreshLayoutListener() {
    }

    companion object {
        fun newInstance() = EpisodesRootFragment()
    }
}