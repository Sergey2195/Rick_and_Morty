package com.aston.rickandmorty.presentation.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
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
    private var gridLayoutManager: GridLayoutManager? = null
    private var searchLayoutIsVisible = false

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
            val snackBar = Snackbar.make(requireView(), R.string.snackbar_not_found, Snackbar.LENGTH_SHORT)
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
        scrollToPositionForward(positions[index])
        animateItem(positions[index])
        setPositionTextView(index + 1, positions.size)
        binding.searchNextButton.setOnClickListener {
            index = if (isValidPosition(index + 1, positions)) index + 1 else index
            scrollToPositionForward(positions[index])
            animateItem(positions[index])
            setPositionTextView(index + 1, positions.size)
        }
        binding.searchPrevButton.setOnClickListener {
            index = if (isValidPosition(index - 1, positions)) index - 1 else index
            scrollToPositionBack(positions[index])
            animateItem(positions[index])
            setPositionTextView(index + 1, positions.size)
        }
    }

    private fun setPositionTextView(current: Int, total: Int) {
        binding.searchPositionTextView.text =
            String.format(getString(R.string.search_position), current, total)
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
        mainViewModel.clearSearchCharacterLiveData()
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
            if (search.isEmpty()) return@observe
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
        saveStateSearchLayout()
        childFragmentManager.beginTransaction()
            .add(R.id.characterFragmentContainer, CharacterDetailsFragment.newInstance(id))
            .addToBackStack(null)
            .commit()
        binding.characterFragmentContainer.visibility = View.VISIBLE
        (requireActivity() as ToolbarManager).onChildScreen()
    }

    private fun saveStateSearchLayout(){
        searchLayoutIsVisible = binding.searchLayout.isVisible
        binding.searchLayout.isVisible = false
    }

    private fun backFromCharacterDetailsFragment() {
        childFragmentManager.popBackStack()
        binding.searchLayout.isVisible = searchLayoutIsVisible
        binding.charactersRecyclerView.visibility = View.VISIBLE
        binding.characterFragmentContainer.visibility = View.GONE
        (requireActivity() as ToolbarManager).onParentScreen()
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