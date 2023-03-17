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
import com.aston.rickandmorty.databinding.FragmentEpisodeDetailsBinding
import com.aston.rickandmorty.presentation.App
import com.aston.rickandmorty.presentation.activities.MainActivity
import com.aston.rickandmorty.presentation.adapters.DetailsAdapter
import com.aston.rickandmorty.presentation.viewModels.CharactersViewModel
import com.aston.rickandmorty.presentation.viewModels.EpisodesViewModel
import com.aston.rickandmorty.presentation.viewModels.MainViewModel
import com.aston.rickandmorty.presentation.viewModelsFactory.ViewModelFactory
import com.aston.rickandmorty.toolbarManager.ToolbarManager
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject

class EpisodeDetailsFragment : Fragment() {

    private var id: Int? = null
    private var container: Int? = null
    private var _binding: FragmentEpisodeDetailsBinding? = null
    private val binding
        get() = _binding!!
    private val component by lazy {
        ((requireActivity().application) as App).component
    }
    @Inject
    lateinit var viewModelFactory: ViewModelFactory
    private val mainViewModel: MainViewModel by viewModels({ activity as MainActivity }) {
        viewModelFactory
    }
    private val viewModel: EpisodesViewModel by viewModels({activity as MainActivity }) {
        viewModelFactory
    }
    private val detailsAdapter = DetailsAdapter()

    override fun onAttach(context: Context) {
        component.injectEpisodeDetailsFragment(this)
        super.onAttach(context)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            id = it.getInt(ID)
            container = it.getInt(CONTAINER)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentEpisodeDetailsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        prepareRecyclerView()
        loadAndSubmitData()
    }

    private fun loadAndSubmitData() = lifecycleScope.launch {
        val data = loadData()
        detailsAdapter.submitList(data)
    }

    private fun prepareRecyclerView() {
        binding.episodeDetailsRecyclerView.layoutManager = GridLayoutManager(requireContext(), 2)
        binding.episodeDetailsRecyclerView.adapter = detailsAdapter
        detailsAdapter.clickListener = {
            openCharacterDetailsFragment(it)
        }
    }

    private fun openCharacterDetailsFragment(id: Int) {
        //todo contrainer
        if (container == null) throw RuntimeException("openCharacterDetailsFragment")
        parentFragmentManager.beginTransaction()
            .replace(container ?: throw RuntimeException("openCharacterDetailsFragment"), CharacterDetailsFragment.newInstance(id, container ?: throw RuntimeException("openCharacterDetailsFragment")))
            .addToBackStack(null)
            .commit()
    }

    private suspend fun loadData() = withContext(Dispatchers.IO) {
        val data = viewModel.getEpisodeDetailsInfo(id ?: 1)
        withContext(Dispatchers.Main){setToolBarTitleText(data?.name ?: "")}
        viewModel.getDataToAdapter(data, requireContext())
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }

    override fun onStart() {
        super.onStart()
        mainViewModel.setIsOnParentLiveData(false)
    }

    private fun setToolBarTitleText(text: String) {
        (requireActivity() as ToolbarManager).setToolbarText(text)
    }

    companion object {

        private const val ID = "id"
        private const val CONTAINER = "container"

        fun newInstance(id: Int, container: Int) = EpisodeDetailsFragment().apply {
            arguments = Bundle().apply {
                putInt(ID, id)
                putInt(CONTAINER, container)
            }
        }
    }
}