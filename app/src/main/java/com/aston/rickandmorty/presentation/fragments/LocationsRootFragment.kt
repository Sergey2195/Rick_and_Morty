package com.aston.rickandmorty.presentation.fragments

import android.os.Bundle
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import com.aston.rickandmorty.R
import com.aston.rickandmorty.databinding.FragmentLocationsRootBinding
import com.aston.rickandmorty.domain.entity.LocationFilterModel
import com.aston.rickandmorty.presentation.App
import com.aston.rickandmorty.presentation.activities.MainActivity
import com.aston.rickandmorty.presentation.viewModels.LocationsViewModel
import com.aston.rickandmorty.toolbarManager.ToolbarManager
import kotlinx.coroutines.flow.filterNotNull

class LocationsRootFragment : BaseFragment<FragmentLocationsRootBinding>(
    R.layout.fragment_locations_root,
    FragmentLocationsRootBinding::inflate
) {

    private val locationViewModel: LocationsViewModel by viewModels({ activity as MainActivity }) {
        viewModelFactory
    }

    override fun injectDependencies() {
        App.getAppComponent().injectLocationsRootFragment(this)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        if (savedInstanceState == null) {
            childFragmentManager.beginTransaction()
                .add(R.id.locationFragmentContainerRoot, LocationAllFragment.newInstance())
                .commit()
        }
        setupBackButtonClickListener()
    }

    override fun setupObservers() {
        lifecycleScope.launchWhenStarted {
            locationViewModel.locationFilterStateFlow.filterNotNull().collect {
                startFragmentWithFiltering(it)
                locationViewModel.clearFilter()
            }
        }
    }

    override fun setUI() {
        setupClickListeners()
    }

    private fun startFragmentWithFiltering(filter: LocationFilterModel) {
        val fragment = LocationAllFragment.newInstance(
            filter.nameFilter,
            filter.typeFilter,
            filter.dimensionFilter
        )
        childFragmentManager.popBackStack()
        childFragmentManager.beginTransaction()
            .setCustomAnimations(R.anim.from_bot, R.anim.to_top, R.anim.from_top, R.anim.to_bot)
            .replace(R.id.locationFragmentContainerRoot, fragment)
            .addToBackStack(null)
            .commit()
    }

    private fun setupBackButtonClickListener() {
        (requireActivity() as ToolbarManager).setBackButtonClickListener {
            childFragmentManager.popBackStack()
        }
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

    private fun setupSearchButtonClickListener() {
        (requireActivity() as ToolbarManager).setSearchButtonClickListener {
            startFilterOrSearchFragment(LocationFilterFragment.SEARCH)
        }
    }

    private fun startFilterOrSearchFragment(mode: Int) {
        childFragmentManager.beginTransaction()
            .setCustomAnimations(R.anim.from_bot, R.anim.to_top, R.anim.from_top, R.anim.to_bot)
            .replace(
                R.id.locationFragmentContainerRoot,
                LocationFilterFragment.newInstance(mode)
            )
            .addToBackStack(null)
            .commit()
    }

    override fun initArguments() {
    }

    companion object {
        fun newInstance() = LocationsRootFragment()
    }
}