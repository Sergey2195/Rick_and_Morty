package com.aston.rickandmorty.presentation.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.aston.rickandmorty.R
import com.aston.rickandmorty.databinding.FragmentEpisodesAllBinding
import com.aston.rickandmorty.presentation.adapters.DefaultLoadStateAdapter
import com.aston.rickandmorty.presentation.adapters.EpisodesAdapter
import com.aston.rickandmorty.presentation.viewModels.EpisodesViewModel
import com.aston.rickandmorty.presentation.viewModels.MainViewModel
import com.aston.rickandmorty.toolbarAndSearchManager.ToolbarAndSearchManager

class EpisodesAllFragment : Fragment() {

    private var _binding: FragmentEpisodesAllBinding? = null
    private val binding
        get() = _binding!!
    private val mainViewModel by lazy {
        ViewModelProvider(requireActivity())[MainViewModel::class.java]
    }
    private val viewModel by lazy {
        ViewModelProvider(requireActivity())[EpisodesViewModel::class.java]
    }
    private val adapter = EpisodesAdapter()
    private var gridLayoutManager: GridLayoutManager? = null

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

    private fun prepareRecyclerView(){
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
                .replace(R.id.episodeFragmentContainerRoot, EpisodeDetailsFragment.newInstance(it, R.id.episodeFragmentContainerRoot))
                .addToBackStack(null)
                .commit()
        }
    }

    private fun setupObservers() {
        lifecycleScope.launchWhenStarted {
            viewModel.episodesAllFlow.collect{pagingData->
                adapter.submitData(pagingData)
            }
        }
    }

    override fun onStart() {
        super.onStart()
        mainViewModel.setIsOnParentLiveData(true)
        (requireActivity() as ToolbarAndSearchManager).setToolbarText(
            requireContext().getString(R.string.bottom_navigation_menu_episodes_title)
        )
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }

    companion object {

        fun newInstance() = EpisodesAllFragment()
    }
}