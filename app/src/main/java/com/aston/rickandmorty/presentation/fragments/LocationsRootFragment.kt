package com.aston.rickandmorty.presentation.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import com.aston.rickandmorty.R
import com.aston.rickandmorty.databinding.FragmentLocationsRootBinding
import com.aston.rickandmorty.domain.entity.LocationFilterModel
import com.aston.rickandmorty.presentation.viewModels.LocationsViewModel
import com.aston.rickandmorty.toolbarManager.ToolbarManager
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.filterNotNull

class LocationsRootFragment : Fragment() {

    private var _binding: FragmentLocationsRootBinding? = null
    private val binding
        get() = _binding!!
    private val locationViewModel by lazy {
        ViewModelProvider(requireActivity())[LocationsViewModel::class.java]
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentLocationsRootBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        if (savedInstanceState == null) {
            childFragmentManager.beginTransaction()
                .add(R.id.locationFragmentContainerRoot, LocationAllFragment.newInstance())
                .commit()
        }
        setupBackButtonClickListener()
        setupObservers()
    }

    private fun setupObservers() = lifecycleScope.launchWhenCreated {
        locationViewModel.locationFilterStateFlow.filterNotNull().collect {
            childFragmentManager.popBackStack()
            delay(500)
            startFragmentWithFiltering(it)
            locationViewModel.clearFilter()
        }
    }

    private fun startFragmentWithFiltering(filter: LocationFilterModel) {
        val fragment = LocationAllFragment.newInstance(
            filter.nameFilter,
            filter.typeFilter,
            filter.dimensionFilter
        )
        childFragmentManager.beginTransaction()
            .replace(R.id.locationFragmentContainerRoot, fragment)
            .addToBackStack(null)
            .commit()
    }

    private fun setupBackButtonClickListener() {
        (requireActivity() as ToolbarManager).setBackButtonClickListener {
            childFragmentManager.popBackStack()
        }
    }

    override fun onStart() {
        super.onStart()
        setupClickListeners()
    }

    private fun setupClickListeners() {
        setupBackButtonClickListener()
        setupFilterButtonClickListener()
        setupSearchButtonClickListener()
    }

    private fun setupFilterButtonClickListener() {
        (requireActivity() as ToolbarManager).setFilterButtonClickListener {
            startFilterOrSearchFragment(LocationFilterFragment.FILTER)
        }
    }

    private fun setupSearchButtonClickListener(){
        (requireActivity() as ToolbarManager).setSearchButtonClickListener{
            startFilterOrSearchFragment(LocationFilterFragment.SEARCH)
        }
    }

    private fun startFilterOrSearchFragment(mode: Int){
        childFragmentManager.beginTransaction()
            .replace(
                R.id.locationFragmentContainerRoot,
                LocationFilterFragment.newInstance(mode)
            )
            .addToBackStack(null)
            .commit()
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }

    companion object {
        fun newInstance() = LocationsRootFragment()
    }
}