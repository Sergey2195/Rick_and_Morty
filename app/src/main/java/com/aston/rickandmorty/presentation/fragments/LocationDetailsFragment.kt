package com.aston.rickandmorty.presentation.fragments

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.aston.rickandmorty.R
import com.aston.rickandmorty.databinding.FragmentLocationDetailsBinding
import com.aston.rickandmorty.domain.entity.CharacterDetailsModel
import com.aston.rickandmorty.domain.entity.LocationDetailsModel
import com.aston.rickandmorty.presentation.adapterModels.LocationDetailsModelAdapter
import com.aston.rickandmorty.presentation.adapters.DetailsCharactersAdapter
import com.aston.rickandmorty.presentation.adapters.LocationDetailsAdapter
import com.aston.rickandmorty.presentation.viewModels.LocationsViewModel
import com.aston.rickandmorty.presentation.viewModels.MainViewModel
import com.aston.rickandmorty.toolbarAndSearchManager.ToolbarAndSearchManager
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers
import kotlinx.coroutines.Job
import kotlinx.coroutines.joinAll
import kotlinx.coroutines.launch

class LocationDetailsFragment : Fragment() {

    private var id: Int? = null
    private val mainViewModel by lazy {
        ViewModelProvider(requireActivity())[MainViewModel::class.java]
    }
    private val viewModel by lazy {
        ViewModelProvider(requireActivity())[LocationsViewModel::class.java]
    }
    private val adapter = LocationDetailsAdapter()
    private var _binding: FragmentLocationDetailsBinding? = null
    private val binding
        get() = _binding!!
    private val compositeDisposable = CompositeDisposable()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            id = it.getInt(ID)
            Log.d("SSV", "got $id")
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
        loadData()
        prepareRecyclerView()
    }

    private fun prepareRecyclerView() {
        binding.locationDetailsRecyclerView.adapter = adapter
        binding.locationDetailsRecyclerView.layoutManager = LinearLayoutManager(requireContext())
    }

    private fun loadData() {
        val observable = viewModel.getLocationDetails(id ?: 1)
        val disposable = observable
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe({ data ->
                parsingData(data)
            }, {

            })
        compositeDisposable.add(disposable)
    }

    private fun parsingData(data: LocationDetailsModel) = lifecycleScope.launch {
        val residentsIds = viewModel.getIdsFromUrl(data.residents)
        setToolBarText(data.locationName)
        val list = mutableListOf(
            LocationDetailsModelAdapter(
                title = requireContext().getString(R.string.character_name_title),
                value = data.locationName
            ),
            LocationDetailsModelAdapter(
                title = requireContext().getString(R.string.character_type_title),
                value = data.locationType
            ),
            LocationDetailsModelAdapter(
                title = requireContext().getString(R.string.dimension_title),
                value = data.dimension
            )
        )
        val models = getCharacterModels(residentsIds)
        for (model in models) {
            list.add(
                LocationDetailsModelAdapter(
                    title = null,
                    url = model.characterImage,
                    value = model.characterName,
                    viewType = R.layout.location_details_residents
                )
            )
        }
        list.add(
            LocationDetailsModelAdapter("Created:", data.created)
        )
        adapter.submitList(list)
    }

    private suspend fun getCharacterModels(listId: List<Int>): List<CharacterDetailsModel> {
        val listDetails = mutableListOf<CharacterDetailsModel>()
        val listJob = mutableListOf<Job>()
        for (id in listId) {
            val job = lifecycleScope.launch {
                val data = viewModel.getCharacterDetails(id)
                if (data != null) listDetails.add(data)
            }
            listJob.add(job)
        }
        listJob.joinAll()
        return listDetails
    }

    private fun setToolBarText(str: String) {
        (requireActivity() as ToolbarAndSearchManager).setToolbarText(str)
    }

    override fun onDetach() {
        super.onDetach()
        compositeDisposable.dispose()
    }

    companion object {
        private const val ID = "id"

        fun newInstance(id: Int) =
            LocationDetailsFragment().apply {
                arguments = Bundle().apply {
                    putInt(ID, id)
                }
            }
    }
}