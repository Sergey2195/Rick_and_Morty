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
import com.aston.rickandmorty.presentation.BottomSheetInputData
import com.aston.rickandmorty.presentation.adapters.CharactersAdapter
import com.aston.rickandmorty.presentation.adapters.DefaultLoadStateAdapter
import com.aston.rickandmorty.presentation.viewModels.CharactersViewModel
import com.aston.rickandmorty.presentation.viewModels.MainViewModel
import com.aston.rickandmorty.toolbarAndSearchManager.ToolbarAndSearchManager

class CharactersAllFragment : Fragment() {
    private val viewModel: CharactersViewModel by viewModels()
    private val mainViewModel by lazy {
        ViewModelProvider(requireActivity())[MainViewModel::class.java]
    }
    private val adapter = CharactersAdapter()
    private var _binding: FragmentCharactersAllBinding? = null
    private val binding
        get() = _binding!!
    private var prevSearch: String? = null
    private var gridLayoutManager: GridLayoutManager? = null

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
        setupToolBarClickListener()
    }

    private fun setupToolBarClickListener() {
        setupSearchButtonClickListener()
    }

    private fun setupSearchButtonClickListener() {
        (requireActivity() as ToolbarAndSearchManager).setSearchClickListener {
            val bottomSheetFragment = BottomSheetFragment.newInstance(
                BottomSheetFragment.MODE_SEARCH,
                BottomSheetInputData(prevSearch)
            )
            bottomSheetFragment.show(parentFragmentManager, null)
        }
    }

    override fun onStop() {
        super.onStop()
        (requireActivity() as ToolbarAndSearchManager).setSearchClickListener(null)
        mainViewModel.clearSearchCharacterLiveData()
    }

    override fun onStart() {
        super.onStart()
        mainViewModel.setIsOnParentLiveData(true)
    }

    private fun setupObservers() {
        lifecycleScope.launchWhenStarted {
            viewModel.charactersFlow.collect { pagingData ->
                adapter.submitData(pagingData)
            }
        }
        mainViewModel.searchCharacterLiveData.observe(viewLifecycleOwner) { search ->
            if (search.isEmpty()) return@observe
            //todo search
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
            .replace(R.id.charactersFragmentContainerRoot, CharacterDetailsFragment.newInstance(id))
            .addToBackStack(null)
            .commit()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    companion object {
        fun newInstance() = CharactersAllFragment()
    }
}