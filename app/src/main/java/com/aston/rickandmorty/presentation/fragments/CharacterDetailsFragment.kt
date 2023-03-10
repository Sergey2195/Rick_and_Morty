package com.aston.rickandmorty.presentation.fragments

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.aston.rickandmorty.R
import com.aston.rickandmorty.databinding.FragmentCharacterDetailsBinding
import com.aston.rickandmorty.domain.entity.CharacterDetailsModel
import com.aston.rickandmorty.presentation.adapters.CharacterDetailsAdapter
import com.aston.rickandmorty.presentation.adapters.CharacterDetailsEpisodesAdapter
import com.aston.rickandmorty.presentation.viewModels.CharactersViewModel
import com.aston.rickandmorty.presentation.viewModels.MainViewModel
import com.aston.rickandmorty.toolbarAndSearchManager.ToolbarAndSearchManager
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class CharacterDetailsFragment : Fragment() {

    private var id: Int? = null
    private var _binding: FragmentCharacterDetailsBinding? = null
    private val binding
        get() = _binding!!
    private val viewModel: CharactersViewModel by viewModels()
    private val adapter = CharacterDetailsAdapter()
    private val internalEpisodeAdapter = CharacterDetailsEpisodesAdapter()
    private val mainViewModel by lazy {
        ViewModelProvider(requireActivity())[MainViewModel::class.java]
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            id = it.getInt(ID_KEY)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentCharacterDetailsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        prepareRecyclerViews()
        observeData()
        loadData()
    }

    private fun loadData() = viewModel.loadInfoAboutCharacter(id ?: 1, requireContext())

    private fun observeData() = lifecycleScope.launchWhenStarted{
        viewModel.dataForAdapter.collect{ list->
            adapter.submitList(list)
        }
    }

    override fun onStart() {
        super.onStart()
        mainViewModel.setIsOnParentLiveData(false)
        setupTitle("")
    }

    override fun onStop() {
        super.onStop()
    }

    override fun onDestroy() {
        super.onDestroy()
        Log.d("SSV", "destroyed details fragment")
    }

    private fun setupViews(data: CharacterDetailsModel) {
        setupTitle(data.characterName)
//        val listAdapterData =
//            Mapper.mapCharacterDetailsModelToListAdapterData(requireContext(), data)
//        setupRecyclerView(listAdapterData)
    }

    private fun setupTitle(name: String) {
        setToolBarText(name)
    }

    private fun prepareRecyclerViews() {
        binding.characterDetailsRecyclerView.adapter = adapter
        binding.characterDetailsRecyclerView.layoutManager = LinearLayoutManager(requireContext())
        adapter.internalEpisodesAdapter = internalEpisodeAdapter
        setupRecyclerClickListeners()
    }

    private fun setupRecyclerClickListeners(){
        adapter.locationClickListener = {openLocationDetails(it)}
        adapter.episodeClickListener = {openEpisodeDetails(it)}
    }

    private fun openLocationDetails(id: Int){
        parentFragmentManager.beginTransaction()
            .replace(R.id.charactersFragmentContainerRoot, LocationDetailsFragment.newInstance(id))
            .addToBackStack(null)
            .commit()
    }

    private fun openEpisodeDetails(id: Int){
        parentFragmentManager.beginTransaction()
            .replace(R.id.charactersFragmentContainerRoot, EpisodeDetailsFragment.newInstance(id))
            .addToBackStack(null)
            .commit()
    }

    private fun setToolBarText(str: String) {
        (requireActivity() as ToolbarAndSearchManager).setToolbarText(str)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        Log.d("SSV", "details onDestroyView")
        _binding = null
    }

    companion object {
        fun newInstance(id: Int) = CharacterDetailsFragment().apply {
            arguments = Bundle().apply {
                putInt(ID_KEY, id)
            }
        }

        private const val ID_KEY = "id"
    }
}