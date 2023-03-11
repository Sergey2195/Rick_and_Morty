package com.aston.rickandmorty.presentation.fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.ViewModelProvider
import com.aston.rickandmorty.R
import com.aston.rickandmorty.databinding.FragmentCharactersRootBinding
import com.aston.rickandmorty.databinding.FragmentSearchAndFilterBinding
import com.aston.rickandmorty.presentation.viewModels.MainViewModel

class SearchAndFilterFragment : Fragment() {

    private var mode = -1
    private var _binding: FragmentSearchAndFilterBinding? = null
    private val binding
        get() = _binding!!
    private val mainViewModel by lazy {
        ViewModelProvider(requireActivity())[MainViewModel::class.java]
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            mode = it.getInt(MODE)
        }
    }

    override fun onStart() {
        super.onStart()
        mainViewModel.setIsOnParentLiveData(false)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentSearchAndFilterBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        when (mode){
            SEARCH_MODE -> {}
            FILTER_MODE -> {}
            else -> throw RuntimeException("SearchAndFilterFragment unknown mode")
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }

    companion object {

        private const val MODE = "mode"
        const val SEARCH_MODE = 0
        const val FILTER_MODE = 1

        fun newInstance(mode: Int) =
            SearchAndFilterFragment().apply {
                arguments = Bundle().apply {
                    putInt(MODE, mode)
                }
            }
    }
}