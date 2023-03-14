package com.aston.rickandmorty.presentation.fragments

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.aston.rickandmorty.databinding.FragmentCharacterDetailsBinding
import com.aston.rickandmorty.presentation.App
import com.aston.rickandmorty.presentation.activities.MainActivity
import com.aston.rickandmorty.presentation.adapterModels.CharacterDetailsModelAdapter
import com.aston.rickandmorty.presentation.adapterModels.CharacterDetailsTitleValueModelAdapter
import com.aston.rickandmorty.presentation.adapters.CharacterDetailsAdapter
import com.aston.rickandmorty.presentation.viewModels.CharactersViewModel
import com.aston.rickandmorty.presentation.viewModels.MainViewModel
import com.aston.rickandmorty.presentation.viewModelsFactory.ViewModelFactory
import com.aston.rickandmorty.toolbarManager.ToolbarManager
import javax.inject.Inject

class CharacterDetailsFragment : Fragment() {

    private var id: Int? = null
    private var container: Int? = null
    private var _binding: FragmentCharacterDetailsBinding? = null
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
    private val viewModel: CharactersViewModel by viewModels({activity as MainActivity}) {
        viewModelFactory
    }
    private val adapter = CharacterDetailsAdapter()

    override fun onAttach(context: Context) {
        component.injectCharacterDetailsFragment(this)
        super.onAttach(context)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            id = it.getInt(ID_KEY)
            container = it.getInt(CONTAINER)
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

    private fun observeData() = lifecycleScope.launchWhenStarted {
        viewModel.dataForAdapter.collect { list ->
            adapter.submitList(list)
            setupName(list)
        }
    }

    private fun setupName(list: List<CharacterDetailsModelAdapter>) {
        if (list.isEmpty()) return
        val name = (list[1] as CharacterDetailsTitleValueModelAdapter).value
        setToolBarText(name)
    }

    override fun onStart() {
        super.onStart()
        mainViewModel.setIsOnParentLiveData(false)
    }

    private fun prepareRecyclerViews() {
        binding.characterDetailsRecyclerView.adapter = adapter
        binding.characterDetailsRecyclerView.layoutManager = LinearLayoutManager(requireContext())
        setupRecyclerClickListeners()
    }

    private fun setupRecyclerClickListeners() {
        adapter.locationClickListener = { openLocationDetails(it) }
        adapter.episodeClickListener = { openEpisodeDetails(it) }
    }

    private fun openLocationDetails(id: Int) {
        parentFragmentManager.beginTransaction()
            .replace(container!!, LocationDetailsFragment.newInstance(id, container!!))
            .addToBackStack(null)
            .commit()
    }

    private fun openEpisodeDetails(id: Int) {
        parentFragmentManager.beginTransaction()
            .replace(container!!, EpisodeDetailsFragment.newInstance(id, container!!))
            .addToBackStack(null)
            .commit()
    }

    private fun setToolBarText(str: String) {
        (requireActivity() as ToolbarManager).setToolbarText(str)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    companion object {
        fun newInstance(id: Int, container: Int) = CharacterDetailsFragment().apply {
            arguments = Bundle().apply {
                putInt(ID_KEY, id)
                putInt(CONTAINER, container)
            }
        }

        private const val ID_KEY = "id"
        private const val CONTAINER = "container"
    }
}