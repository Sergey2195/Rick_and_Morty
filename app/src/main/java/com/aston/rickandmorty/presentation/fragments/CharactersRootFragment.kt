package com.aston.rickandmorty.presentation.fragments

import android.os.Bundle
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import com.aston.rickandmorty.R
import com.aston.rickandmorty.databinding.FragmentCharactersRootBinding
import com.aston.rickandmorty.domain.entity.CharacterFilterModel
import com.aston.rickandmorty.presentation.App
import com.aston.rickandmorty.presentation.activities.MainActivity
import com.aston.rickandmorty.presentation.viewModels.CharactersViewModel
import com.aston.rickandmorty.toolbarManager.ToolbarManager
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.launch

class CharactersRootFragment : BaseFragment<FragmentCharactersRootBinding>(
    R.layout.fragment_characters_root,
    FragmentCharactersRootBinding::inflate
) {

    private val charactersViewModel: CharactersViewModel by viewModels({ activity as MainActivity }) {
        viewModelFactory
    }

    override fun injectDependencies() {
        App.getAppComponent().injectCharactersRootFragment(this)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        if (savedInstanceState == null) {
            startInitialFragment()
        }
        setupBackButtonClickListener()
    }

    override fun setupObservers() {
        lifecycleScope.launch {
            charactersViewModel.characterFilter.filterNotNull().collect {
                startFragmentWithFiltering(it)
                charactersViewModel.clearCharacterFilter()
            }
        }
    }

    private fun startFragmentWithFiltering(filter: CharacterFilterModel) {
        val fragment = CharactersAllFragment.newInstance(
            filter.nameFilter,
            filter.statusFilter,
            filter.speciesFilter,
            filter.typeFilter,
            filter.genderFilter
        )
        childFragmentManager.popBackStack()
        childFragmentManager.beginTransaction()
            .replace(R.id.charactersFragmentContainerRoot, fragment)
            .addToBackStack(null)
            .commit()
    }

    override fun onStart() {
        super.onStart()
        setupClickListeners()
    }

    private fun setupClickListeners() {
        setupBackButtonClickListener()
        setupSearchClickListener()
        setupFilterClickListener()
    }

    private fun setupSearchClickListener() {
        (requireActivity() as ToolbarManager).setSearchButtonClickListener {
            childFragmentManager.beginTransaction()
                .replace(
                    R.id.charactersFragmentContainerRoot,
                    CharacterFilterFragment.newInstance(CharacterFilterFragment.SEARCH_MODE)
                )
                .addToBackStack(null)
                .commit()
        }
    }

    private fun setupFilterClickListener() {
        (requireActivity() as ToolbarManager).setFilterButtonClickListener {
            childFragmentManager.beginTransaction()
                .replace(
                    R.id.charactersFragmentContainerRoot,
                    CharacterFilterFragment.newInstance(CharacterFilterFragment.FILTER_MODE)
                )
                .addToBackStack(null)
                .commit()
        }
    }

    private fun setupBackButtonClickListener() {
        (requireActivity() as ToolbarManager).setBackButtonClickListener {
            childFragmentManager.popBackStack()
        }
    }

    private fun startInitialFragment() = lifecycleScope.launchWhenStarted {
        delay(100)
        childFragmentManager.beginTransaction()
            .add(R.id.charactersFragmentContainerRoot, CharactersAllFragment.newInstance())
            .commit()
    }

    override fun initArguments() {
    }

    override fun setUI() {
    }

    companion object {
        fun newInstance() = CharactersRootFragment()
    }
}