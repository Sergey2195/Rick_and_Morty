package com.aston.rickandmorty.presentation.fragments

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.aston.rickandmorty.R
import com.aston.rickandmorty.databinding.FragmentEpisodesAllBinding
import com.aston.rickandmorty.presentation.App
import com.aston.rickandmorty.presentation.activities.MainActivity
import com.aston.rickandmorty.presentation.adapters.DefaultLoadStateAdapter
import com.aston.rickandmorty.presentation.adapters.EpisodesAdapter
import com.aston.rickandmorty.presentation.viewModels.EpisodeFilterViewModel
import com.aston.rickandmorty.presentation.viewModels.EpisodesViewModel
import com.aston.rickandmorty.presentation.viewModels.MainViewModel
import com.aston.rickandmorty.presentation.viewModelsFactory.ViewModelFactory
import com.aston.rickandmorty.toolbarManager.ToolbarManager
import javax.inject.Inject

class EpisodesAllFragment : Fragment() {

    private var _binding: FragmentEpisodesAllBinding? = null
    private val binding
        get() = _binding!!
    @Inject
    lateinit var viewModelFactory: ViewModelFactory
    private val mainViewModel: MainViewModel by viewModels({ activity as MainActivity }) {
        viewModelFactory
    }
    private val viewModel: EpisodesViewModel by viewModels({activity as MainActivity }) {
        viewModelFactory
    }
    private val component by lazy {
        ((requireActivity().application) as App).component
    }
    private val adapter = EpisodesAdapter()
    private var gridLayoutManager: GridLayoutManager? = null
    private var arrayFilter: Array<String?> = Array(2) { null }

    override fun onAttach(context: Context) {
        component.injectEpisodesAllFragment(this)
        super.onAttach(context)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            arrayFilter = it.getStringArray(FILTER_ARRAY) as Array<String?>
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentEpisodesAllBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        prepareRecyclerView()
        setupObservers()
    }

    private fun prepareRecyclerView() {
        val footerAdapter = DefaultLoadStateAdapter {
            //needtodo click listener
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
                .replace(
                    R.id.episodeFragmentContainerRoot,
                    EpisodeDetailsFragment.newInstance(it, R.id.episodeFragmentContainerRoot)
                )
                .addToBackStack(null)
                .commit()
        }
    }

    private fun setupObservers() {
        lifecycleScope.launchWhenStarted {
            viewModel.getEpisodesAllFlow(arrayFilter[0], arrayFilter[1]).collect { pagingData ->
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

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
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