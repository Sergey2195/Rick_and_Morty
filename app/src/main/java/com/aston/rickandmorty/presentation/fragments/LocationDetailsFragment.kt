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
import com.aston.rickandmorty.databinding.FragmentLocationDetailsBinding
import com.aston.rickandmorty.domain.entity.LocationDetailsModel
import com.aston.rickandmorty.presentation.App
import com.aston.rickandmorty.presentation.activities.MainActivity
import com.aston.rickandmorty.presentation.adapters.DetailsAdapter
import com.aston.rickandmorty.presentation.viewModels.LocationsViewModel
import com.aston.rickandmorty.presentation.viewModels.MainViewModel
import com.aston.rickandmorty.presentation.viewModelsFactory.ViewModelFactory
import com.aston.rickandmorty.toolbarManager.ToolbarManager
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers
import kotlinx.coroutines.*
import javax.inject.Inject


class LocationDetailsFragment : Fragment() {

    private var id: Int? = null
    private var container: Int? = null
    @Inject
    lateinit var viewModelFactory: ViewModelFactory
    private val component by lazy {
        ((requireActivity().application) as App).component
    }
    private val mainViewModel: MainViewModel by viewModels({ activity as MainActivity }) {
        viewModelFactory
    }
    private val viewModel: LocationsViewModel by viewModels({ activity as MainActivity }) {
        viewModelFactory
    }
    private val detailsAdapter = DetailsAdapter()
    private var _binding: FragmentLocationDetailsBinding? = null
    private val binding
        get() = _binding!!

    override fun onAttach(context: Context) {
        component.injectLocationDetailsFragment(this)
        super.onAttach(context)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            id = it.getInt(ID)
            container = it.getInt(CONTAINER)
        }
    }

    override fun onStart() {
        super.onStart()
        mainViewModel.setIsOnParentLiveData(false)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentLocationDetailsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        prepareRecyclersView()
        loadData()
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

    private fun loadData() = lifecycleScope.launch(Dispatchers.IO){
        val response = viewModel.getLocationDetails(id ?: 1)
        val dataForAdapter = viewModel.prepareDataForAdapter(response, requireContext())
        withContext(Dispatchers.Main) {
            detailsAdapter.submitList(dataForAdapter)
            setToolBarText(response.locationName)
        }
    }

    private fun setToolBarText(str: String) {
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