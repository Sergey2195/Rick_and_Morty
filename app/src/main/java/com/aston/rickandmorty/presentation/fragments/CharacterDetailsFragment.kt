package com.aston.rickandmorty.presentation.fragments

import android.os.Bundle
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.aston.rickandmorty.R
import com.aston.rickandmorty.databinding.FragmentCharacterDetailsBinding
import com.aston.rickandmorty.presentation.App
import com.aston.rickandmorty.presentation.activities.MainActivity
import com.aston.rickandmorty.presentation.adapterModels.CharacterDetailsModelAdapter
import com.aston.rickandmorty.presentation.adapterModels.CharacterDetailsTitleValueModelAdapter
import com.aston.rickandmorty.presentation.adapters.CharacterDetailsAdapter
import com.aston.rickandmorty.presentation.viewModels.CharactersViewModel
import com.aston.rickandmorty.toolbarManager.ToolbarManager
import kotlinx.coroutines.Job
import kotlinx.coroutines.cancel
import kotlinx.coroutines.flow.filterNotNull

class CharacterDetailsFragment : BaseFragment<FragmentCharacterDetailsBinding>(
    R.layout.fragment_character_details,
    FragmentCharacterDetailsBinding::inflate
) {

    private var id: Int? = null
    private var container: Int? = null
    private val viewModel: CharactersViewModel by viewModels({ activity as MainActivity }) {
        viewModelFactory
    }
    private val adapter = CharacterDetailsAdapter()
    private var observeJob: Job? = null
    private var titleText: String? = null

    override fun initArguments() {
        arguments?.let {
            id = it.getInt(ID_KEY)
            container = it.getInt(CONTAINER)
        }
        if (id == null) throw RuntimeException("unknown id onCreate CharacterDetailsFragment")
    }

    override fun setupObservers() {
        observeData()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        loadData(false)
    }

    override fun setUI() {
        prepareRecyclerViews()
    }

    override fun injectDependencies() {
        App.getAppComponent().injectCharacterDetailsFragment(this)
    }

    override fun setRefreshLayoutListener() {
        (requireActivity() as ToolbarManager).setRefreshClickListener {
            loadData(true)
            observeData()
        }
    }

    private fun loadData(forceUpdate: Boolean = false) {
        viewModel.loadInfoAboutCharacter(id!!, forceUpdate)
    }

    private fun observeData() {
        observeJob = lifecycleScope.launchWhenStarted {
            viewModel.dataForAdapter.filterNotNull().collect { list ->
                adapter.submitList(list)
                setupName(list)
                cancel()
            }
        }
    }

    private fun setupName(list: List<CharacterDetailsModelAdapter>) {
        if (list.isEmpty()) return
        val name = (list[1] as? CharacterDetailsTitleValueModelAdapter)?.value
        titleText = name
        setToolBarText(name)
    }

    override fun onStart() {
        super.onStart()
        mainViewModel.setIsOnParentLiveData(false)
        setToolBarText(titleText)
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
            .setCustomAnimations(
                R.anim.from_right,
                R.anim.to_left,
                R.anim.from_left,
                R.anim.to_right
            )
            .replace(container!!, LocationDetailsFragment.newInstance(id, container!!))
            .addToBackStack(null)
            .commit()
    }

    private fun openEpisodeDetails(id: Int) {
        parentFragmentManager.beginTransaction()
            .setCustomAnimations(
                R.anim.from_right,
                R.anim.to_left,
                R.anim.from_left,
                R.anim.to_right
            )
            .replace(container!!, EpisodeDetailsFragment.newInstance(id, container!!))
            .addToBackStack(null)
            .commit()
    }

    private fun setToolBarText(str: String?) {
        if (str == null) return
        (requireActivity() as ToolbarManager).setToolbarText(str)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        observeJob?.cancel()
        observeJob = null
        viewModel.clearCharacterDetails()
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