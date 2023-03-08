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
import androidx.recyclerview.widget.LinearSmoothScroller
import androidx.recyclerview.widget.RecyclerView
import com.aston.rickandmorty.R
import com.aston.rickandmorty.databinding.FragmentCharactersAllBinding
import com.aston.rickandmorty.presentation.BottomSheetInputData
import com.aston.rickandmorty.presentation.adapters.CharactersAdapter
import com.aston.rickandmorty.presentation.adapters.DefaultLoadStateAdapter
import com.aston.rickandmorty.presentation.viewHolders.CharacterViewHolder
import com.aston.rickandmorty.presentation.viewModels.CharactersViewModel
import com.aston.rickandmorty.presentation.viewModels.MainViewModel
import com.aston.rickandmorty.toolbarAndSearchManager.ToolbarAndSearchManager
import com.google.android.material.snackbar.Snackbar
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlin.math.abs

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
    private var searchLayoutIsVisible = false

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

    private fun handlingResultSearch(positions: List<Int>) {
        if (positions.isEmpty()) {
            val snackBar =
                Snackbar.make(requireView(), R.string.snackbar_not_found, Snackbar.LENGTH_SHORT)
            snackBar.show()
            (requireActivity() as ToolbarAndSearchManager).changeSearchVisibility(false)
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
        var index = calcClosestItem(positions)
        scrollToPositionForward(positions[index])
        animateItem(positions[index])
        setPositionTextView(index + 1, positions.size)
        (requireActivity() as ToolbarAndSearchManager).setSearchForwardClickListener {
            index = if (isValidPosition(index + 1, positions)) index + 1 else index
            scrollToPositionForward(positions[index])
            animateItem(positions[index])
            setPositionTextView(index + 1, positions.size)
        }
        (requireActivity() as ToolbarAndSearchManager).setSearchBackClickListener {
            index = if (isValidPosition(index - 1, positions)) index - 1 else index
            scrollToPositionBack(positions[index])
            animateItem(positions[index])
            setPositionTextView(index + 1, positions.size)
        }
    }

    private fun setPositionTextView(current: Int, total: Int) {
        (requireActivity() as ToolbarAndSearchManager).setSearchPositionTextView(
            String.format(getString(R.string.search_position), current, total)
        )
    }

    private fun animateItem(positionAtAdapter: Int) {
        animateAtPosition(positionAtAdapter)
    }

    private fun isValidPosition(current: Int, list: List<Int>): Boolean {
        return current >= 0 && current < list.size
    }

    private fun scrollToPositionBack(position: Int) {
        val smoothScroller: LinearSmoothScroller = object : LinearSmoothScroller(activity) {
            override fun getVerticalSnapPreference(): Int {
                return SNAP_TO_START
            }
        }
        val target = if (position <= 2) 0 else position - 2
        smoothScroller.targetPosition = target
        binding.charactersRecyclerView.layoutManager?.startSmoothScroll(smoothScroller)
    }

    private fun scrollToPositionForward(position: Int) {
        val smoothScroller: LinearSmoothScroller = object : LinearSmoothScroller(activity) {
            override fun getVerticalSnapPreference(): Int {
                return SNAP_TO_END
            }
        }
        smoothScroller.targetPosition = position + 2
        binding.charactersRecyclerView.layoutManager?.startSmoothScroll(smoothScroller)
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
            (requireActivity() as ToolbarAndSearchManager).changeSearchVisibility(true)
        }
    }

    override fun onStop() {
        super.onStop()
        (requireActivity() as ToolbarAndSearchManager).setSearchClickListener(null)
        mainViewModel.clearSearchCharacterLiveData()
        (requireActivity() as ToolbarAndSearchManager).changeSearchVisibility(false)
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
            prevSearch = search
            val positions = adapter.findPosition(search)
            handlingResultSearch(positions)
            (requireActivity() as ToolbarAndSearchManager).setSearchRequestTextView(search)
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
    }

    private fun startCharacterDetailsFragment(id: Int) {
        parentFragmentManager.beginTransaction()
            .replace(R.id.charactersFragmentContainerRoot, CharacterDetailsFragment.newInstance(id))
            .addToBackStack(null)
            .commit()
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
        fun newInstance() = CharactersAllFragment()
    }
}