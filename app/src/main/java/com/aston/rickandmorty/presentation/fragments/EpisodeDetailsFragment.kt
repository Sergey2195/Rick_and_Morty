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
import com.aston.rickandmorty.databinding.FragmentEpisodeDetailsBinding
import com.aston.rickandmorty.presentation.adapters.DetailsCharactersAdapter
import com.aston.rickandmorty.presentation.adapters.EpisodeDetailsAdapter
import com.aston.rickandmorty.presentation.viewModels.EpisodesViewModel
import com.aston.rickandmorty.presentation.viewModels.MainViewModel
import com.aston.rickandmorty.toolbarAndSearchManager.ToolbarAndSearchManager
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class EpisodeDetailsFragment : Fragment() {

    private var id: Int? = null
    private var _binding: FragmentEpisodeDetailsBinding? = null
    private val binding
        get() = _binding!!
    private val mainViewModel by lazy {
        ViewModelProvider(requireActivity())[MainViewModel::class.java]
    }
    private val viewModel by lazy {
        ViewModelProvider(requireActivity())[EpisodesViewModel::class.java]
    }
    private val charactersAdapter = DetailsCharactersAdapter()
    private val episodeDetailsAdapter = EpisodeDetailsAdapter()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            id = it.getInt(ID)
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
        prepareRecyclersView()
        loadAndSubmitData()
    }

    private fun loadAndSubmitData() = lifecycleScope.launch {
        val data = loadData()
        episodeDetailsAdapter.submitList(data)
    }

    private fun prepareRecyclersView() {
        binding.episodeDetailsRecyclerView.layoutManager = LinearLayoutManager(requireContext())
        binding.episodeDetailsRecyclerView.adapter = episodeDetailsAdapter
        episodeDetailsAdapter.internalCharactersAdapter = charactersAdapter
        charactersAdapter.clickListener = {openCharacterDetailsFragment(it)}
    }

    private fun openCharacterDetailsFragment(id: Int){
        parentFragmentManager.beginTransaction()
            .replace(R.id.episodeFragmentContainerRoot, CharacterDetailsFragment.newInstance(id))
            .addToBackStack(null)
            .commit()
    }

    private suspend fun loadData() = withContext(Dispatchers.IO) {
        val data = viewModel.getEpisodeDetailsInfo(id ?: 1)
        setToolBarTitleText(data?.name ?: "")
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

    private fun setToolBarTitleText(text: String){
        (requireActivity() as ToolbarAndSearchManager).setToolbarText(text)
    }

    companion object {

        private const val ID = "id"

        fun newInstance(id: Int) = EpisodeDetailsFragment().apply {
            arguments = Bundle().apply {
                putInt(ID, id)
            }
        }
    }
}