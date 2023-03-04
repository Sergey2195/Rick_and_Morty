package com.aston.rickandmorty.presentation.fragments

import android.graphics.Rect
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.aston.rickandmorty.R
import com.aston.rickandmorty.databinding.FragmentCharactersBinding
import com.aston.rickandmorty.domain.entity.CharacterModel
import com.aston.rickandmorty.presentation.adapters.CharactersAdapter
import com.aston.rickandmorty.presentation.viewModels.CharactersViewModel
import com.aston.rickandmorty.toolbarManager.ToolbarManager
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class CharactersFragment : Fragment() {
    private val viewModel: CharactersViewModel by viewModels()
    private val adapter = CharactersAdapter()
    private var _binding: FragmentCharactersBinding? = null
    private val binding
        get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentCharactersBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        prepareRecyclerView()
        setupObservers()
        setupBackButtonClickListener()
    }

    private fun setupBackButtonClickListener() {
        (requireActivity() as ToolbarManager).setBackButtonClickLister{
            backFromCharacterDetailsFragment()
        }
    }

    private fun setupObservers() {
        lifecycleScope.launchWhenStarted {
            viewModel.charactersDataStateFlow.collect{
                adapter.submitList(it)
            }
        }
    }

    private fun prepareRecyclerView() {
        binding.charactersRecyclerView.adapter = adapter
        adapter.clickListener = { id ->
            startCharacterDetailsFragment(id)
        }
        binding.charactersRecyclerView.layoutManager =
            GridLayoutManager(requireContext(), 2, RecyclerView.VERTICAL, false)
        viewModel.updateData()
    }

    private fun startCharacterDetailsFragment(id: Int){
        binding.charactersRecyclerView.visibility = View.GONE
        childFragmentManager.beginTransaction()
            .add(R.id.characterFragmentContainer, CharacterDetailsFragment.newInstance(id))
            .addToBackStack(null)
            .commit()
        binding.characterFragmentContainer.visibility = View.VISIBLE
        (requireActivity() as ToolbarManager).onChildScreen()
    }

    private fun backFromCharacterDetailsFragment(){
        childFragmentManager.popBackStack()
        binding.charactersRecyclerView.visibility = View.VISIBLE
        binding.characterFragmentContainer.visibility = View.GONE
        (requireActivity() as ToolbarManager).onParentScreen()
    }


    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    companion object {
        fun newInstance() = CharactersFragment()
    }
}