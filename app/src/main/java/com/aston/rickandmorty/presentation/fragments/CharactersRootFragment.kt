package com.aston.rickandmorty.presentation.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.aston.rickandmorty.R
import com.aston.rickandmorty.databinding.FragmentCharactersRootBinding
import com.aston.rickandmorty.toolbarManager.ToolbarManager

class CharactersRootFragment : Fragment() {

    private var _binding: FragmentCharactersRootBinding? = null
    private val binding
        get() = _binding!!

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
            childFragmentManager.beginTransaction()
                .add(R.id.charactersFragmentContainerRoot, CharactersAllFragment.newInstance())
                .commit()
        }
        setupBackButtonClickListener()
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
                    SearchAndFilterFragment.newInstance(SearchAndFilterFragment.SEARCH_MODE)
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
                    SearchAndFilterFragment.newInstance(SearchAndFilterFragment.FILTER_MODE)
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

    companion object {
        fun newInstance() = CharactersRootFragment()
    }
}