package com.aston.rickandmorty.presentation.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.widget.doOnTextChanged
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.ViewModelProvider
import com.aston.rickandmorty.databinding.FragmentLocationFilterBinding
import com.aston.rickandmorty.domain.entity.LocationFilterModel
import com.aston.rickandmorty.presentation.viewModels.LocationFilterViewModel
import com.aston.rickandmorty.presentation.viewModels.LocationsViewModel
import com.aston.rickandmorty.presentation.viewModels.MainViewModel

class LocationFilterFragment : Fragment() {

    private var mode = -1
    private var _binding: FragmentLocationFilterBinding? = null
    private val binding
        get() = _binding!!
    private val mainViewModel by lazy {
        ViewModelProvider(requireActivity())[MainViewModel::class.java]
    }
    private val locationViewModel by lazy {
        ViewModelProvider(requireActivity())[LocationsViewModel::class.java]
    }
    private val locationFilterViewModel: LocationFilterViewModel by viewModels()
    private val resultFilter = LocationFilterModel()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            mode = it.getInt(MODE)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentLocationFilterBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val filterGroupVisibility = when (mode){
            FILTER -> View.VISIBLE
            SEARCH -> View.GONE
            else -> throw RuntimeException("unknown mode")
        }
        binding.filterGroup.visibility = filterGroupVisibility
        setupClickListener()
    }

    private fun setupClickListener(){
        binding.saveButton.setOnClickListener {
            locationViewModel.setFilter(resultFilter)
        }
        binding.nameEditText.doOnTextChanged { text, _, _, _ ->
            resultFilter.nameFilter = textFilter(text)
        }
        binding.typeEditText.doOnTextChanged { text, _, _, _ ->
            resultFilter.typeFilter = textFilter(text)
        }
        binding.dimensionEditText.doOnTextChanged { text, _, _, _ ->
            resultFilter.dimensionFilter = textFilter(text)
        }
    }

    private fun textFilter(text: CharSequence?): String? {
        return if (text != null && text.isNotBlank()) {
            text.toString()
        } else null
    }

    override fun onStart() {
        super.onStart()
        mainViewModel.setIsOnParentLiveData(false)
    }

    companion object {

        private const val MODE = "mode"
        const val SEARCH = 1
        const val FILTER = 0

        fun newInstance(mode: Int) =
            LocationFilterFragment().apply {
                arguments = Bundle().apply {
                    putInt(MODE, mode)
                }
            }
    }
}