package com.aston.rickandmorty.presentation.fragments

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.aston.rickandmorty.R
import com.aston.rickandmorty.databinding.FragmentLocationAllBinding
import com.aston.rickandmorty.presentation.App
import com.aston.rickandmorty.presentation.activities.MainActivity
import com.aston.rickandmorty.presentation.adapters.DefaultLoadStateAdapter
import com.aston.rickandmorty.presentation.adapters.LocationsAdapter
import com.aston.rickandmorty.presentation.viewModels.LocationsViewModel
import com.aston.rickandmorty.presentation.viewModels.MainViewModel
import com.aston.rickandmorty.presentation.viewModelsFactory.ViewModelFactory
import com.aston.rickandmorty.toolbarManager.ToolbarManager
import kotlinx.coroutines.Job
import javax.inject.Inject

class LocationAllFragment : Fragment() {

    private var _binding: FragmentLocationAllBinding? = null
    private val binding
        get() = _binding!!
    @Inject
    lateinit var viewModelFactory: ViewModelFactory
    private val component = App.getAppComponent()
    private val mainViewModel: MainViewModel by viewModels({ activity as MainActivity }) {
        viewModelFactory
    }
    private val viewModel: LocationsViewModel by viewModels({ activity as MainActivity }) {
        viewModelFactory
    }
    private val adapter = LocationsAdapter()
    private var gridLayoutManager: GridLayoutManager? = null
    private var arrayFilter: Array<String?> = Array(5) { null }
    private var jobObserve: Job? = null

    override fun onAttach(context: Context) {
        component.injectLocationAllFragment(this)
        super.onAttach(context)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        prepareRecyclerView()
        setupObserversWithSetForceUpdate(false)
        setupSwipeListener()
    }

    private fun setupSwipeListener(){
        (requireActivity() as ToolbarManager).setRefreshClickListener{
            jobObserve?.cancel()
            adapter.refresh()
            setupObserversWithSetForceUpdate(true)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            arrayFilter = it.getStringArray(FILTER_ARRAY) as Array<String?>
        }
    }

    private fun setupObserversWithSetForceUpdate(forceUpdate: Boolean = false) {
        jobObserve = lifecycleScope.launchWhenStarted {
            viewModel.getLocationAllFlow(
                arrayFilter[0],
                arrayFilter[1],
                arrayFilter[2],
                forceUpdate
            ).collect {
                adapter.submitData(it)
            }
        }
    }

    private fun prepareRecyclerView() {
        val footerAdapter = DefaultLoadStateAdapter {
            adapter.retry()
        }
        val adapterWithLoadFooter = adapter.withLoadStateFooter(footerAdapter)
        binding.locationsRecyclerView.adapter = adapterWithLoadFooter
        gridLayoutManager = GridLayoutManager(requireContext(), 2, RecyclerView.VERTICAL, false)
        gridLayoutManager?.spanSizeLookup = object : GridLayoutManager.SpanSizeLookup() {
            override fun getSpanSize(position: Int): Int {
                return if (position == adapter.itemCount && footerAdapter.itemCount > 0) 2 else 1
            }
        }
        binding.locationsRecyclerView.layoutManager = gridLayoutManager
        adapter.clickListener = {
            parentFragmentManager.beginTransaction()
                .replace(
                    R.id.locationFragmentContainerRoot,
                    LocationDetailsFragment.newInstance(it, R.id.locationFragmentContainerRoot)
                )
                .addToBackStack(null)
                .commit()
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentLocationAllBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }

    override fun onStart() {
        super.onStart()
        val filtersInNull = allFiltersIsNull()
        mainViewModel.setIsOnParentLiveData(filtersInNull)
        val title = if (filtersInNull) {
            requireContext().getString(R.string.bottom_navigation_menu_location_title)
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
            typeFilter: String? = null,
            dimensionFilter: String? = null
        ) = LocationAllFragment().apply {
            arguments = Bundle().apply {
                putStringArray(FILTER_ARRAY, arrayOf(nameFilter, typeFilter, dimensionFilter))
            }
        }
    }
}