package com.aston.rickandmorty.presentation.fragments

import android.os.Bundle
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.GridLayoutManager
import com.aston.rickandmorty.R
import com.aston.rickandmorty.databinding.FragmentLocationDetailsBinding
import com.aston.rickandmorty.presentation.App
import com.aston.rickandmorty.presentation.activities.MainActivity
import com.aston.rickandmorty.presentation.adapterModels.DetailsModelText
import com.aston.rickandmorty.presentation.adapters.DetailsAdapter
import com.aston.rickandmorty.presentation.viewModels.LocationsViewModel
import com.aston.rickandmorty.toolbarManager.ToolbarManager
import kotlinx.coroutines.flow.filterNotNull

class LocationDetailsFragment : BaseFragment<FragmentLocationDetailsBinding>(
    R.layout.fragment_location_details,
    FragmentLocationDetailsBinding::inflate
) {

    private var id: Int? = null
    private var container: Int? = null
    private val detailsAdapter = DetailsAdapter()
    private var titleText: String? = null
    private val viewModel: LocationsViewModel by viewModels({ activity as MainActivity }) {
        viewModelFactory
    }

    override fun initArguments() {
        arguments?.let {
            id = it.getInt(ID)
            container = it.getInt(CONTAINER)
        }
    }

    override fun injectDependencies() {
        App.getAppComponent().injectLocationDetailsFragment(this)
    }

    override fun setupObservers() {
        lifecycleScope.launchWhenStarted {
            viewModel.locationDetailsStateFlow.filterNotNull().collect { data ->
                detailsAdapter.submitList(data)
                titleText = (data[1] as? DetailsModelText)?.text
                setToolBarText(titleText)
            }
        }
    }

    override fun setUI() {
        prepareRecyclersView()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        loadData(false)
        setupRefreshListener()
    }

    override fun onStart() {
        super.onStart()
        mainViewModel.setIsOnParentLiveData(false)
        setToolBarText(titleText)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        viewModel.clearDataLocationDetailsAdapter()
    }

    private fun setupRefreshListener() {
        (requireActivity() as ToolbarManager).setRefreshClickListener {
            loadData(true)
        }
    }

    private fun prepareRecyclersView() {
        binding.locationDetailsRecyclerView.layoutManager = GridLayoutManager(requireContext(), 2)
        binding.locationDetailsRecyclerView.adapter = detailsAdapter
        detailsAdapter.clickListener = { openCharacterDetailsFragment(it) }
    }

    private fun openCharacterDetailsFragment(id: Int) {
        parentFragmentManager.beginTransaction()
            .replace(container!!, CharacterDetailsFragment.newInstance(id, container!!))
            .addToBackStack(null)
            .commit()
    }

    private fun loadData(forceUpdate: Boolean) {
        viewModel.sendIdToGetDetails(id ?: throw RuntimeException("load data"), forceUpdate)
    }

    private fun setToolBarText(str: String?) {
        if (str == null) return
        (requireActivity() as ToolbarManager).setToolbarText(str)
    }

    companion object {
        private const val ID = "id"
        private const val CONTAINER = "container"

        fun newInstance(id: Int, container: Int) =
            LocationDetailsFragment().apply {
                arguments = Bundle().apply {
                    putInt(ID, id)
                    putInt(CONTAINER, container)
                }
            }
    }
}