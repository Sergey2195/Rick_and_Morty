package com.aston.rickandmorty.presentation.fragments

import android.os.Bundle
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.aston.rickandmorty.R
import com.aston.rickandmorty.databinding.FragmentCharactersAllBinding
import com.aston.rickandmorty.presentation.App
import com.aston.rickandmorty.presentation.activities.MainActivity
import com.aston.rickandmorty.presentation.adapters.CharactersAdapter
import com.aston.rickandmorty.presentation.adapters.DefaultLoadStateAdapter
import com.aston.rickandmorty.presentation.viewModels.CharactersViewModel
import com.aston.rickandmorty.presentation.viewModels.MainViewModel
import com.aston.rickandmorty.toolbarManager.ToolbarManager
import kotlinx.coroutines.Job

class CharactersAllFragment : BaseFragment<FragmentCharactersAllBinding>(
    R.layout.fragment_characters_all,
    FragmentCharactersAllBinding::inflate
) {

    private val adapter = CharactersAdapter()
    private var gridLayoutManager: GridLayoutManager? = null
    private var arrayFilter: Array<String?> = Array(5) { null }
    private var observerJob: Job? = null
    private val mainViewModel: MainViewModel by viewModels({ activity as MainActivity }) { viewModelFactory }
    private val charactersViewModel: CharactersViewModel by viewModels({ activity as MainActivity }) {
        viewModelFactory
    }

    override fun setUI() {
        prepareRecyclerView()
    }

    override fun initArguments() {
        arguments?.let {
            arrayFilter = it.getStringArray(FILTER_ARRAY) as Array<String?>
        }
    }

    override fun setupObservers() {
        setupObservers(false)
        setupRefreshListener()
    }

    override fun injectDependencies() {
        App.getAppComponent().injectCharactersAllFragment(this)
    }

    private fun setupRefreshListener() {
        (requireActivity() as ToolbarManager).setRefreshClickListener {
            observerJob?.cancel()
            setupObservers(true)
        }
    }

    override fun onStart() {
        super.onStart()
        when (allFiltersIsNull()) {
            true -> standardMode()
            false -> filterMode()
        }
    }

    private fun standardMode() {
        mainViewModel.setIsOnParentLiveData(true)
        (requireActivity() as ToolbarManager).setToolbarText(
            requireContext().getString(R.string.bottom_navigation_menu_characters_title)
        )
    }

    private fun filterMode() {
        mainViewModel.setIsOnParentLiveData(false)
        (requireActivity() as ToolbarManager).setToolbarText(getTitleFiltering())
    }

    private fun getTitleFiltering(): String {
        return arrayFilter.filterNotNull().joinToString()
    }

    private fun allFiltersIsNull(): Boolean {
        return arrayFilter.all { it == null }
    }

    private fun setupObservers(forceUpdate: Boolean) {
        observerJob = lifecycleScope.launchWhenStarted {
            charactersViewModel.getFlowCharacters(arrayFilter, forceUpdate)
                .collect { pagingData ->
                    adapter.submitData(pagingData)
                }
        }
    }

    private fun prepareRecyclerView() {
        val footerAdapter = DefaultLoadStateAdapter {
            adapter.retry()
        }
        val adapterWithLoadFooter = adapter.withLoadStateFooter(footerAdapter)
        binding.charactersRecyclerView.adapter = adapterWithLoadFooter
        adapter.clickListener = { id -> startCharacterDetailsFragment(id) }
        gridLayoutManager = GridLayoutManager(requireContext(), 2, RecyclerView.VERTICAL, false)
        gridLayoutManager?.spanSizeLookup = object : GridLayoutManager.SpanSizeLookup() {
            override fun getSpanSize(position: Int): Int {
                return if (position == adapter.itemCount && footerAdapter.itemCount > 0) 2 else 1
            }
        }
        binding.charactersRecyclerView.layoutManager = gridLayoutManager
    }

    private fun startCharacterDetailsFragment(id: Int) {
        parentFragmentManager.beginTransaction()
            .replace(
                R.id.charactersFragmentContainerRoot,
                CharacterDetailsFragment.newInstance(id, R.id.charactersFragmentContainerRoot)
            )
            .addToBackStack(null)
            .commit()
    }

    companion object {
        private const val FILTER_ARRAY = "filter array"

        fun newInstance(
            nameFilter: String? = null,
            statusFilter: String? = null,
            speciesFilter: String? = null,
            typeFilter: String? = null,
            genderFilter: String? = null
        ): CharactersAllFragment {
            return CharactersAllFragment().apply {
                arguments = Bundle().apply {
                    val arrayFilter =
                        arrayOf(nameFilter, statusFilter, speciesFilter, typeFilter, genderFilter)
                    putStringArray(FILTER_ARRAY, arrayFilter)
                }
            }
        }
    }
}