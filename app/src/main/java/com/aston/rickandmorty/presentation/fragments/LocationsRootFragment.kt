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
import com.aston.rickandmorty.R
import com.aston.rickandmorty.databinding.FragmentLocationsRootBinding
import com.aston.rickandmorty.domain.entity.LocationFilterModel
import com.aston.rickandmorty.presentation.App
import com.aston.rickandmorty.presentation.activities.MainActivity
import com.aston.rickandmorty.presentation.viewModels.LocationsViewModel
import com.aston.rickandmorty.presentation.viewModels.MainViewModel
import com.aston.rickandmorty.presentation.viewModelsFactory.ViewModelFactory
import com.aston.rickandmorty.toolbarManager.ToolbarManager
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.filterNotNull
import javax.inject.Inject

class LocationsRootFragment : Fragment() {

    private var _binding: FragmentLocationsRootBinding? = null
    private val binding
        get() = _binding!!
    @Inject
    lateinit var viewModelFactory: ViewModelFactory
    private val component by lazy {
        ((requireActivity().application) as App).component
    }
    private val locationViewModel: LocationsViewModel by viewModels({ activity as MainActivity }) {
        viewModelFactory
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentLocationsRootBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onAttach(context: Context) {
        component.injectLocationsRootFragment(this)
        super.onAttach(context)
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
        childFragmentManager.popBackStack()
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