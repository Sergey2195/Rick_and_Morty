package com.aston.rickandmorty.presentation.fragments

import android.os.Bundle
import android.util.Log
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
import com.aston.rickandmorty.databinding.FragmentCharactersBinding
import com.aston.rickandmorty.presentation.BottomSheetInputData
import com.aston.rickandmorty.presentation.adapters.CharactersAdapter
import com.aston.rickandmorty.presentation.adapters.DefaultLoadStateAdapter
import com.aston.rickandmorty.presentation.viewModels.CharactersViewModel
import com.aston.rickandmorty.presentation.viewModels.MainViewModel
import com.aston.rickandmorty.toolbarManager.ToolbarManager
import com.google.android.material.snackbar.Snackbar

class CharactersFragment : Fragment() {
    private val viewModel: CharactersViewModel by viewModels()
    private val mainViewModel by lazy {
        ViewModelProvider(requireActivity())[MainViewModel::class.java]
    }
    private val adapter = CharactersAdapter()
    private var _binding: FragmentCharactersBinding? = null
    private val binding
        get() = _binding!!
    private var prevSearch: String? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentCharactersBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        prepareRecyclerView()
        setupObservers()
        setupToolBarClickListener()
        mainViewModel.searchCharacterLiveData.observe(viewLifecycleOwner) { search ->
            prevSearch = search
            val positions = adapter.findPosition(search)
            handlingResultSearch(positions)
        }
    }

    private fun handlingResultSearch(positions: List<Int>){
        if (positions.isEmpty()){
            val snackbar = Snackbar.make(requireView(), "Not found", Snackbar.LENGTH_SHORT)
            snackbar.show()
        }
    }

    private fun setupToolBarClickListener() {
        setupBackButtonClickListener()
        setupSearchButtonClickListener()
    }

    private fun setupSearchButtonClickListener() {
        (requireActivity() as ToolbarManager).setSearchClickListener {
            val bottomSheetFragment = BottomSheetFragment.newInstance(
                BottomSheetFragment.MODE_SEARCH,
                BottomSheetInputData(prevSearch)
            )
            bottomSheetFragment.show(parentFragmentManager, null)
        }
    }

    override fun onStop() {
        super.onStop()
        (requireActivity() as ToolbarManager).setSearchClickListener(null)
    }

    private fun setupBackButtonClickListener() {
        (requireActivity() as ToolbarManager).setBackButtonClickLister {
            backFromCharacterDetailsFragment()
        }
    }

    private fun setupObservers() {
        lifecycleScope.launchWhenStarted {
            viewModel.charactersFlow.collect { pagingData ->
                adapter.submitData(pagingData)
            }
        }
    }

    private fun prepareRecyclerView() {
        val footerAdapter = DefaultLoadStateAdapter {
            //needtodo click listener
        }
        val adapterWithLoadFooter = adapter.withLoadStateFooter(footerAdapter)
        binding.charactersRecyclerView.adapter = adapterWithLoadFooter
        adapter.clickListener = { id -> startCharacterDetailsFragment(id) }
        val gridLayoutManager = GridLayoutManager(requireContext(), 2, RecyclerView.VERTICAL, false)
        gridLayoutManager.spanSizeLookup = object : GridLayoutManager.SpanSizeLookup() {
            override fun getSpanSize(position: Int): Int {
                return if (position == adapter.itemCount && footerAdapter.itemCount > 0) 2 else 1
            }
        }
        binding.charactersRecyclerView.layoutManager = gridLayoutManager
        viewModel.updateData()
    }

    private fun startCharacterDetailsFragment(id: Int) {
        binding.charactersRecyclerView.visibility = View.GONE
        childFragmentManager.beginTransaction()
            .add(R.id.characterFragmentContainer, CharacterDetailsFragment.newInstance(id))
            .addToBackStack(null)
            .commit()
        binding.characterFragmentContainer.visibility = View.VISIBLE
        (requireActivity() as ToolbarManager).onChildScreen()
    }

    private fun backFromCharacterDetailsFragment() {
        childFragmentManager.popBackStack()
        binding.charactersRecyclerView.visibility = View.VISIBLE
        binding.characterFragmentContainer.visibility = View.GONE
        (requireActivity() as ToolbarManager).onParentScreen()
    }


    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    companion object {
        fun newInstance() = CharactersFragment()
    }
}