package com.aston.rickandmorty.presentation.fragments

import android.os.Bundle
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.aston.rickandmorty.R
import com.aston.rickandmorty.databinding.FragmentEpisodesAllBinding
import com.aston.rickandmorty.presentation.App
import com.aston.rickandmorty.presentation.activities.MainActivity
import com.aston.rickandmorty.presentation.adapters.DefaultLoadStateAdapter
import com.aston.rickandmorty.presentation.adapters.EpisodesAdapter
import com.aston.rickandmorty.presentation.viewModels.EpisodesViewModel
import com.aston.rickandmorty.toolbarManager.ToolbarManager
import kotlinx.coroutines.Job

class EpisodesAllFragment : BaseFragment<FragmentEpisodesAllBinding>(
    R.layout.fragment_episodes_all,
    FragmentEpisodesAllBinding::inflate
) {

    private val viewModel: EpisodesViewModel by viewModels({ activity as MainActivity }) {
        viewModelFactory
    }
    private val adapter = EpisodesAdapter()
    private var gridLayoutManager: GridLayoutManager? = null
    private var arrayFilter: Array<String?> = Array(2) { null }
    private var jobObserver: Job? = null

    override fun injectDependencies() {
        App.getAppComponent().injectEpisodesAllFragment(this)
    }

    override fun initArguments() {
        arguments?.let {
            arrayFilter = it.getStringArray(FILTER_ARRAY) as Array<String?>
        }
    }

    override fun setupObservers() {
        sendParametersAndObserve(false)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setupRefreshListener()
    }

    override fun setUI() {
        prepareRecyclerView()
    }

    private fun setupRefreshListener() {
        (requireActivity() as ToolbarManager).setRefreshClickListener {
            jobObserver?.cancel()
            sendParametersAndObserve(true)
        }
    }

    private fun prepareRecyclerView() {
        val footerAdapter = DefaultLoadStateAdapter {
            adapter.retry()
        }
        val adapterWithLoadFooter = adapter.withLoadStateFooter(footerAdapter)
        binding.episodesRecyclerView.adapter = adapterWithLoadFooter
        gridLayoutManager = GridLayoutManager(requireContext(), 2, RecyclerView.VERTICAL, false)
        gridLayoutManager?.spanSizeLookup = object : GridLayoutManager.SpanSizeLookup() {
            override fun getSpanSize(position: Int): Int {
                return if (position == adapter.itemCount && footerAdapter.itemCount > 0) 2 else 1
            }
        }
        binding.episodesRecyclerView.layoutManager = gridLayoutManager
        adapter.clickListener = {
            parentFragmentManager.beginTransaction()
                .setCustomAnimations(R.anim.from_right, R.anim.to_left, R.anim.from_left, R.anim.to_right)
                .replace(
                    R.id.episodeFragmentContainerRoot,
                    EpisodeDetailsFragment.newInstance(it, R.id.episodeFragmentContainerRoot)
                )
                .addToBackStack(null)
                .commit()
        }
    }

    private fun sendParametersAndObserve(forceUpdate: Boolean) {
        jobObserver = lifecycleScope.launchWhenStarted {
            viewModel.getEpisodesAllFlow(arrayFilter[0], arrayFilter[1], forceUpdate)
                .collect { pagingData ->
                    adapter.submitData(pagingData)
                }
        }
    }

    override fun onStart() {
        super.onStart()
        val filtersInNull = allFiltersIsNull()
        mainViewModel.setIsOnParentLiveData(filtersInNull)
        val title = if (filtersInNull) {
            requireContext().getString(R.string.bottom_navigation_menu_episodes_title)
        } else {
            getTitleFiltering()
        }
        (requireActivity() as ToolbarManager).setToolbarText(title)
    }

    private fun getTitleFiltering(): String {
        return arrayFilter.filterNotNull().joinToString()
    }

    private fun allFiltersIsNull(): Boolean {
        return arrayFilter.all { it == null }
    }

    companion object {

        private const val FILTER_ARRAY = "filter array"

        fun newInstance(
            nameFilter: String? = null,
            episodeFilter: String? = null
        ) = EpisodesAllFragment().apply {
            arguments = Bundle().apply {
                putStringArray(FILTER_ARRAY, arrayOf(nameFilter, episodeFilter))
            }
        }
    }
}