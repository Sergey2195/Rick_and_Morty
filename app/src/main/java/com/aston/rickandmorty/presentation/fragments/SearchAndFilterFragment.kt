package com.aston.rickandmorty.presentation.fragments

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.widget.doOnTextChanged
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import com.aston.rickandmorty.R
import com.aston.rickandmorty.databinding.FragmentSearchAndFilterBinding
import com.aston.rickandmorty.domain.entity.CharacterFilterModel
import com.aston.rickandmorty.presentation.viewModels.CharactersViewModel
import com.aston.rickandmorty.presentation.viewModels.MainViewModel
import com.aston.rickandmorty.presentation.viewModels.SearchAndFilterViewModel
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers
import io.reactivex.subjects.PublishSubject
import java.util.concurrent.TimeUnit

class SearchAndFilterFragment : Fragment() {

    private var mode = -1
    private var _binding: FragmentSearchAndFilterBinding? = null
    private val binding
        get() = _binding!!
    private val mainViewModel by lazy {
        ViewModelProvider(requireActivity())[MainViewModel::class.java]
    }
    private val charactersViewModel: CharactersViewModel by lazy {
        ViewModelProvider(requireActivity())[CharactersViewModel::class.java]
    }
    private val searchAndFilterViewModel: SearchAndFilterViewModel by viewModels()
    private val resultFilter = CharacterFilterModel()
    private val publishSubject = PublishSubject.create<Unit>()
    private val compositeDisposable = CompositeDisposable()

    override fun onDetach() {
        super.onDetach()
        compositeDisposable.dispose()
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
        when (mode) {
            SEARCH_MODE -> {
                binding.filterGroup.visibility = View.GONE
            }
            FILTER_MODE -> {
                binding.filterGroup.visibility = View.VISIBLE
            }
            else -> throw RuntimeException("SearchAndFilterFragment unknown mode")
        }
        setupClickListeners()
        setupObservers()
        sendCheckCountOfCharactersWithInterval()
    }

    private fun setupObservers() = lifecycleScope.launchWhenCreated {
        searchAndFilterViewModel.charactersCountStateFlow.collect {
            val text = when (it) {
                -1 -> requireContext().getString(R.string.search_not_found)
                0 -> requireContext().getString(
                    if (mode == FILTER_MODE) R.string.filter_initial else R.string.search_initial
                )
                else -> requireContext().getString(R.string.search_count, it)
            }
            binding.countResultTextView.text = text
        }
    }

    private fun sendCheckCountOfCharactersWithInterval() {
        val disposable = publishSubject.debounce(1, TimeUnit.SECONDS)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe {
                searchAndFilterViewModel.sendFilters(resultFilter)
            }
        compositeDisposable.add(disposable)
    }

    private fun setupClickListeners() {
        binding.saveButton.setOnClickListener { saveResult() }
        setupNameClickListener()
        setupStatusRadioGroupListener()
        setupSpeciesClickListener()
        setupTypeClickListener()
        setupGenderRadioGroupListener()
    }

    private fun setupGenderRadioGroupListener() {
        binding.genderRadioGroup.setOnCheckedChangeListener { _, checkedId ->
            val newGender = when (checkedId) {
                R.id.femaleGenderRadioButton -> "female"
                R.id.maleGenderRadioButton -> "male"
                R.id.genderlessGenderRadioButton -> "genderless"
                R.id.genderUnknownRadioButton -> "unknown"
                else -> null
            }
            resultFilter.genderFilter = newGender
            publishSubject.onNext(Unit)
        }
    }

    private fun setupStatusRadioGroupListener() {
        binding.statusRadioGroup.setOnCheckedChangeListener { _, checkedId ->
            val newStatus = when (checkedId) {
                R.id.aliveStatusRadioButton -> "alive"
                R.id.deadStatusRadioButton -> "dead"
                R.id.unknownStatusRadioButton -> "unknown"
                else -> null
            }
            resultFilter.statusFilter = newStatus
            publishSubject.onNext(Unit)
        }
    }

    private fun setupTypeClickListener() {
        binding.typeEditText.doOnTextChanged { text, _, _, _ ->
            resultFilter.typeFilter = textFilter(text)
            publishSubject.onNext(Unit)
        }
    }

    private fun setupSpeciesClickListener() {
        binding.speciesEditText.doOnTextChanged { text, _, _, _ ->
            resultFilter.speciesFilter = textFilter(text)
            publishSubject.onNext(Unit)
        }
    }

    private fun setupNameClickListener() {
        binding.nameEditText.doOnTextChanged { text, _, _, _ ->
            resultFilter.nameFilter = textFilter(text)
            publishSubject.onNext(Unit)
        }
    }

    private fun textFilter(text: CharSequence?): String? {
        return if (text != null && text.isNotBlank()) {
            text.toString()
        } else null
    }

    private fun saveResult() {
        charactersViewModel.setCharacterFilter(resultFilter)
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