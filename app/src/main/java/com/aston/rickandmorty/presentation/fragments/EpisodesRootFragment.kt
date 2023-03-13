package com.aston.rickandmorty.presentation.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import com.aston.rickandmorty.R
import com.aston.rickandmorty.databinding.FragmentEpisodesRootBinding
import com.aston.rickandmorty.domain.entity.EpisodeFilterModel
import com.aston.rickandmorty.presentation.viewModels.EpisodesViewModel
import com.aston.rickandmorty.toolbarManager.ToolbarManager
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.filterNotNull

class EpisodesRootFragment : Fragment() {

    private var _binding: FragmentEpisodesRootBinding? = null
    private val binding
        get() = _binding!!
    private val episodeViewModel by lazy {
        ViewModelProvider(requireActivity())[EpisodesViewModel::class.java]
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentEpisodesRootBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        if (savedInstanceState == null) {
            childFragmentManager.beginTransaction()
                .add(R.id.episodeFragmentContainerRoot, EpisodesAllFragment.newInstance())
                .commit()
        }
        setupObservers()
    }

    private fun setupObservers() = lifecycleScope.launchWhenStarted {
        episodeViewModel.episodeFilterStateFlow.filterNotNull().collect{
            childFragmentManager.popBackStack()
            delay(500)
            startFragmentWithFiltering(it)
            episodeViewModel.clearFilter()
        }
    }

    private fun startFragmentWithFiltering(filter: EpisodeFilterModel){
        val fragment = EpisodesAllFragment.newInstance(filter.name, filter.episode)
        childFragmentManager.beginTransaction()
            .replace(R.id.episodeFragmentContainerRoot, fragment)
            .addToBackStack(null)
            .commit()
    }

    private fun setupBackButtonClickListener() {
        (requireActivity() as ToolbarManager).setBackButtonClickListener {
            childFragmentManager.popBackStack()
        }
    }

    override fun onStart() {
        super.onStart()
        setupBackButtonClickListener()
        setupFilterButtonClickListener()
        setupSearchButtonClickListener()
    }

    private fun setupFilterButtonClickListener() {
        (requireActivity() as ToolbarManager).setFilterButtonClickListener {
            startFilterOrSearchFragment(EpisodeFilterFragment.FILTER)
        }
    }

    private fun setupSearchButtonClickListener(){
        (requireActivity() as ToolbarManager).setSearchButtonClickListener{
            startFilterOrSearchFragment(EpisodeFilterFragment.SEARCH)
        }
    }

    private fun startFilterOrSearchFragment(mode: Int){
        childFragmentManager.beginTransaction()
            .replace(R.id.episodeFragmentContainerRoot, EpisodeFilterFragment.newInstance(mode))
            .addToBackStack(null)
            .commit()
    }

    companion object {
        fun newInstance() = EpisodesRootFragment()
    }
}