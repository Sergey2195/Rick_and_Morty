package com.aston.rickandmorty.presentation.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import com.aston.rickandmorty.databinding.FragmentLocationDetailsBinding
import com.aston.rickandmorty.domain.entity.CharacterDetailsModel
import com.aston.rickandmorty.domain.entity.LocationDetailsModel
import com.aston.rickandmorty.presentation.adapters.DetailsAdapter
import com.aston.rickandmorty.presentation.viewModels.LocationsViewModel
import com.aston.rickandmorty.presentation.viewModels.MainViewModel
import com.aston.rickandmorty.toolbarAndSearchManager.ToolbarAndSearchManager
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers
import kotlinx.coroutines.*


class LocationDetailsFragment : Fragment() {

    private var id: Int? = null
    private var container: Int? = null
    private val mainViewModel by lazy {
        ViewModelProvider(requireActivity())[MainViewModel::class.java]
    }
    private val viewModel by lazy {
        ViewModelProvider(requireActivity())[LocationsViewModel::class.java]
    }
    private val detailsAdapter = DetailsAdapter()
    private var _binding: FragmentLocationDetailsBinding? = null
    private val binding
        get() = _binding!!
    private val compositeDisposable = CompositeDisposable()

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
        detailsAdapter.clickListener = {openCharacterDetailsFragment(it)}
    }

    private fun openCharacterDetailsFragment(id: Int){
        parentFragmentManager.beginTransaction()
            .replace(container!!, CharacterDetailsFragment.newInstance(id, container!!))
            .addToBackStack(null)
            .commit()
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

    private fun parsingData(data: LocationDetailsModel) = lifecycleScope.launch(Dispatchers.Default) {
        val dataForAdapter = viewModel.prepareDataForAdapter(data, requireContext())
        withContext(Dispatchers.Main){ detailsAdapter.submitList(dataForAdapter)}
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