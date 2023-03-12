package com.aston.rickandmorty.presentation.fragments

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
import com.aston.rickandmorty.databinding.FragmentCharactersAllBinding
import com.aston.rickandmorty.presentation.adapters.CharactersAdapter
import com.aston.rickandmorty.presentation.adapters.DefaultLoadStateAdapter
import com.aston.rickandmorty.presentation.viewModels.CharactersViewModel
import com.aston.rickandmorty.presentation.viewModels.MainViewModel
import com.aston.rickandmorty.toolbarManager.ToolbarManager

class CharactersAllFragment : Fragment() {
    private val viewModel: CharactersViewModel by lazy {
        ViewModelProvider(requireActivity())[CharactersViewModel::class.java]
    }
    private val mainViewModel by lazy {
        ViewModelProvider(requireActivity())[MainViewModel::class.java]
    }
    private val adapter = CharactersAdapter()
    private var _binding: FragmentCharactersAllBinding? = null
    private val binding
        get() = _binding!!
    private var gridLayoutManager: GridLayoutManager? = null
    private var arrayFilter: Array<String?> = Array(5) { null }

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
        _binding = FragmentCharactersAllBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        prepareRecyclerView()
        setupObservers()
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

    private fun setupObservers() = lifecycleScope.launchWhenStarted {
        viewModel.getFlowCharacters(
            arrayFilter[0],
            arrayFilter[1],
            arrayFilter[2],
            arrayFilter[3],
            arrayFilter[4]
        ).collect { pagingData ->
            adapter.submitData(pagingData)
        }
    }

    private fun prepareRecyclerView() {
        val footerAdapter = DefaultLoadStateAdapter {
            //todo click listener
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

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    companion object {
        const val FILTER_ARRAY = "filter array"

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