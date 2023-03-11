package com.aston.rickandmorty.presentation.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.GridLayoutManager
import com.aston.rickandmorty.databinding.FragmentEpisodeDetailsBinding
import com.aston.rickandmorty.presentation.adapters.DetailsAdapter
import com.aston.rickandmorty.presentation.viewModels.EpisodesViewModel
import com.aston.rickandmorty.presentation.viewModels.MainViewModel
import com.aston.rickandmorty.toolbarManager.ToolbarManager
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class EpisodeDetailsFragment : Fragment() {

    private var id: Int? = null
    private var container: Int? = null
    private var _binding: FragmentEpisodeDetailsBinding? = null
    private val binding
        get() = _binding!!
    private val mainViewModel by lazy {
        ViewModelProvider(requireActivity())[MainViewModel::class.java]
    }
    private val viewModel by lazy {
        ViewModelProvider(requireActivity())[EpisodesViewModel::class.java]
    }
    private val detailsAdapter = DetailsAdapter()

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
        parentFragmentManager.beginTransaction()
            .replace(container!!, CharacterDetailsFragment.newInstance(id, container!!))
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