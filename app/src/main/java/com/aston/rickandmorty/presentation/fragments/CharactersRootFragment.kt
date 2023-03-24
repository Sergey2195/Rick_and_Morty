package com.aston.rickandmorty.presentation.fragments

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import com.aston.rickandmorty.R
import com.aston.rickandmorty.databinding.FragmentCharactersRootBinding
import com.aston.rickandmorty.domain.entity.CharacterFilterModel
import com.aston.rickandmorty.presentation.App
import com.aston.rickandmorty.presentation.activities.MainActivity
import com.aston.rickandmorty.presentation.viewModels.CharactersViewModel
import com.aston.rickandmorty.presentation.viewModelsFactory.ViewModelFactory
import com.aston.rickandmorty.toolbarManager.ToolbarManager
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.launch
import javax.inject.Inject

class CharactersRootFragment : Fragment() {

    @Inject
    lateinit var viewModelFactory: ViewModelFactory
    private val charactersViewModel: CharactersViewModel by viewModels({activity as MainActivity }) {
        viewModelFactory
    }
    private var _binding: FragmentCharactersRootBinding? = null
    private val binding
        get() = _binding!!

    override fun onAttach(context: Context) {
        App.getAppComponent().injectCharactersRootFragment(this)
        super.onAttach(context)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentCharactersRootBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        if (savedInstanceState == null) {
            startInitialFragment()
        }
        setupBackButtonClickListener()
        setupObservers()
    }

    private fun setupObservers() = lifecycleScope.launch{
        charactersViewModel.characterFilter.filterNotNull().collect{
            startFragmentWithFiltering(it)
            charactersViewModel.clearCharacterFilter()
        }
    }

    private fun startFragmentWithFiltering(filter: CharacterFilterModel){
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

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }

    private fun startInitialFragment() = lifecycleScope.launchWhenStarted {
        delay(100)
        childFragmentManager.beginTransaction()
            .add(R.id.charactersFragmentContainerRoot, CharactersAllFragment.newInstance())
            .commit()
    }

    companion object {
        fun newInstance() = CharactersRootFragment()
    }
}