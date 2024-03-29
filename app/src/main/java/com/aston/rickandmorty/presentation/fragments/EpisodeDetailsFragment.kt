package com.aston.rickandmorty.presentation.fragments

import android.os.Bundle
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.GridLayoutManager
import com.aston.rickandmorty.R
import com.aston.rickandmorty.databinding.FragmentEpisodeDetailsBinding
import com.aston.rickandmorty.presentation.App
import com.aston.rickandmorty.presentation.activities.MainActivity
import com.aston.rickandmorty.presentation.adapterModels.DetailsModelText
import com.aston.rickandmorty.presentation.adapters.DetailsAdapter
import com.aston.rickandmorty.presentation.viewModels.EpisodesViewModel
import com.aston.rickandmorty.toolbarManager.ToolbarManager
import kotlinx.coroutines.Job
import kotlinx.coroutines.cancel
import kotlinx.coroutines.flow.filterNotNull

class EpisodeDetailsFragment : BaseFragment<FragmentEpisodeDetailsBinding>(
    R.layout.fragment_episode_details,
    FragmentEpisodeDetailsBinding::inflate
) {

    private var id: Int? = null
    private var container: Int? = null
    private val viewModel: EpisodesViewModel by viewModels({ activity as MainActivity }) {
        viewModelFactory
    }
    private val detailsAdapter = DetailsAdapter()
    private var titleText: String? = null
    private var observeJob: Job? = null

    override fun injectDependencies() {
        App.getAppComponent().injectEpisodeDetailsFragment(this)
    }

    override fun initArguments() {
        arguments?.let {
            id = it.getInt(ID)
            container = it.getInt(CONTAINER)
        }
    }

    override fun setupObservers() {
        observeJob = lifecycleScope.launchWhenStarted {
            viewModel.episodeDataForAdapter.filterNotNull().collect { data ->
                detailsAdapter.submitList(data)
                titleText = (data[1] as? DetailsModelText)?.text
                setToolBarTitleText(titleText)
                cancel()
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        sendIdEpisode(false)
    }

    override fun setUI() {
        prepareRecyclerView()
    }

    override fun setRefreshLayoutListener() {
        (requireActivity() as ToolbarManager).setRefreshClickListener {
            sendIdEpisode(true)
            setupObservers()
        }
    }

    private fun sendIdEpisode(forceUpdate: Boolean) {
        if (id == null) return
        viewModel.sendIdEpisode(id!!, forceUpdate)
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
            .setCustomAnimations(
                R.anim.from_right,
                R.anim.to_left,
                R.anim.from_left,
                R.anim.to_right
            )
            .replace(container!!, CharacterDetailsFragment.newInstance(id, container!!))
            .addToBackStack(null)
            .commit()
    }

    override fun onStart() {
        super.onStart()
        mainViewModel.setIsOnParentLiveData(false)
        setToolBarTitleText(titleText)
    }

    private fun setToolBarTitleText(text: String?) {
        if (text == null) return
        (requireActivity() as ToolbarManager).setToolbarText(text)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        observeJob?.cancel()
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