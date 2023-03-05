package com.aston.rickandmorty.presentation.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.aston.rickandmorty.databinding.FragmentCharacterDetailsBinding
import com.aston.rickandmorty.domain.entity.CharacterDetailsModel
import com.aston.rickandmorty.mappers.Mapper
import com.aston.rickandmorty.presentation.adapterModels.CharacterDetailsModelAdapter
import com.aston.rickandmorty.presentation.adapters.CharacterDetailsAdapter
import com.aston.rickandmorty.presentation.viewModels.CharactersViewModel
import com.aston.rickandmorty.toolbarManager.ToolbarManager
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class CharacterDetailsFragment : Fragment() {

    private var id: Int? = null
    private var _binding: FragmentCharacterDetailsBinding? = null
    private val binding
        get() = _binding!!
    private val viewModel: CharactersViewModel by viewModels()
    private val adapter = CharacterDetailsAdapter()

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
        setToolBarText("loading")
        lifecycleScope.launch {
            val data = loadData() ?: return@launch
            setupViews(data)
        }
    }

    private suspend fun loadData() =
        withContext(lifecycleScope.coroutineContext + Dispatchers.IO) {
            if (id == null) null
            viewModel.getCharacterDetailsInfo(id!!)
        }

    private fun setupViews(data: CharacterDetailsModel) {
        setupTitle(data)
        val listAdapterData =
            Mapper.mapCharacterDetailsModelToListAdapterData(requireContext(), data)
        setupRecyclerView(listAdapterData)
    }

    private fun setupTitle(data: CharacterDetailsModel) {
        setToolBarText(data.characterName)
    }

    private fun setupRecyclerView(data: List<CharacterDetailsModelAdapter>) {
        binding.characterDetailsRecyclerView.adapter = adapter
        binding.characterDetailsRecyclerView.layoutManager = LinearLayoutManager(requireContext())
        adapter.submitList(data)
    }

    private fun setToolBarText(str: String) {
        (requireActivity() as ToolbarManager).setToolbarText(str)
    }

    override fun onDestroyView() {
        super.onDestroyView()
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