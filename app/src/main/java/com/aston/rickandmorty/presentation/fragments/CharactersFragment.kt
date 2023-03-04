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
import androidx.recyclerview.widget.LinearSmoothScroller
import androidx.recyclerview.widget.RecyclerView
import com.aston.rickandmorty.R
import com.aston.rickandmorty.databinding.FragmentCharactersBinding
import com.aston.rickandmorty.presentation.BottomSheetInputData
import com.aston.rickandmorty.presentation.adapters.CharactersAdapter
import com.aston.rickandmorty.presentation.adapters.DefaultLoadStateAdapter
import com.aston.rickandmorty.presentation.viewHolders.CharacterViewHolder
import com.aston.rickandmorty.presentation.viewModels.CharactersViewModel
import com.aston.rickandmorty.presentation.viewModels.MainViewModel
import com.aston.rickandmorty.toolbarManager.ToolbarManager
import com.google.android.material.snackbar.Snackbar
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlin.math.abs


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
    private var smoothScroller: RecyclerView.SmoothScroller? = null
    private var gridLayoutManager: GridLayoutManager? = null

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
    }

    private fun handlingResultSearch(positions: List<Int>) {
        if (positions.isEmpty()) {
            val snackBar = Snackbar.make(requireView(), "Not found", Snackbar.LENGTH_SHORT)
            snackBar.show()
        } else {
            setupSearchPanel(positions)
        }
    }

    private fun calcClosestItem(list: List<Int>): Int {
        val currentPosition = binding.charactersRecyclerView.getCurrentPosition()
        var value = Int.MAX_VALUE
        var minDiff = abs(value - currentPosition)
        for ((index, num) in list.withIndex()) {
            val calcDiff = abs(num - currentPosition)
            if (calcDiff <= minDiff) {
                value = index
                minDiff = calcDiff
            }
        }
        return value
    }

    private fun setupSearchPanel(positions: List<Int>) {
        binding.searchResultTextView.text = prevSearch
        binding.searchLayout.visibility = View.VISIBLE
        binding.closeSearchPanel.setOnClickListener {
            binding.searchLayout.visibility = View.GONE
        }
        var index = calcClosestItem(positions)
        scrollAndAnimate(positions[index])
        setPositionTextView(index+1, positions.size)
        binding.searchNextButton.setOnClickListener {
            index = if (isValidPosition(index + 1, positions)) index + 1 else index
            scrollAndAnimate(positions[index])
            setPositionTextView(index+1, positions.size)
        }
        binding.searchPrevButton.setOnClickListener {
            index = if (isValidPosition(index - 1, positions)) index - 1 else index
            scrollAndAnimate(positions[index])
            setPositionTextView(index+1, positions.size)
        }
    }

    private fun setPositionTextView(current: Int, total: Int) {
        binding.searchPositionTextView.text =
            String.format(getString(R.string.search_position), current, total)
    }

    private fun scrollAndAnimate(positionAtAdapter: Int) {
        scrollToPosition(positionAtAdapter)
        animateAtPosition(positionAtAdapter)
    }

    private fun isValidPosition(current: Int, list: List<Int>): Boolean {
        return current >= 0 && current < list.size
    }

    private fun scrollToPosition(position: Int) {
        smoothScroller = CenterSmoothScroller(binding.charactersRecyclerView.context)
        smoothScroller?.targetPosition = position
        gridLayoutManager?.startSmoothScroll(smoothScroller)
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
        mainViewModel.searchCharacterLiveData.observe(viewLifecycleOwner) { search ->
            prevSearch = search
            val positions = adapter.findPosition(search)
            handlingResultSearch(positions)
        }
    }

    private fun prepareRecyclerView() {
        val footerAdapter = DefaultLoadStateAdapter {
            //needtodo click listener
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

    class CenterSmoothScroller(context: Context?) : LinearSmoothScroller(context) {
        override fun calculateDtToFit(
            viewStart: Int,
            viewEnd: Int,
            boxStart: Int,
            boxEnd: Int,
            snapPreference: Int
        ): Int {
            return boxStart + (boxEnd - boxStart) / 2 - (viewStart + (viewEnd - viewStart) / 2)
        }
    }

    private fun animateAtPosition(position: Int) {
        lifecycleScope.launch {
            var viewHolder =
                binding.charactersRecyclerView.findViewHolderForAdapterPosition(position)
            while (viewHolder == null) {
                viewHolder =
                    binding.charactersRecyclerView.findViewHolderForAdapterPosition(position)
                delay(50)
            }
            (viewHolder as CharacterViewHolder).animate()
        }
    }

    private fun RecyclerView?.getCurrentPosition(): Int {
        return (this?.layoutManager as GridLayoutManager).findFirstVisibleItemPosition()
    }


    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    companion object {
        fun newInstance() = CharactersFragment()
    }
}