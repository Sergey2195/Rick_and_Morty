package com.aston.rickandmorty.presentation.fragments

import android.os.Bundle
import android.view.View
import androidx.core.widget.doOnTextChanged
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import com.aston.rickandmorty.R
import com.aston.rickandmorty.databinding.FragmentCharacterFilterBinding
import com.aston.rickandmorty.domain.entity.CharacterFilterModel
import com.aston.rickandmorty.presentation.App
import com.aston.rickandmorty.presentation.activities.MainActivity
import com.aston.rickandmorty.presentation.viewModels.CharacterFilterViewModel
import com.aston.rickandmorty.presentation.viewModels.CharactersViewModel
import com.aston.rickandmorty.toolbarManager.ToolbarManager
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import java.util.concurrent.TimeUnit

class CharacterFilterFragment : BaseFilterFragment<FragmentCharacterFilterBinding>(
    R.layout.fragment_character_filter,
    FragmentCharacterFilterBinding::inflate
) {

    private val charactersViewModel: CharactersViewModel by viewModels({ activity as MainActivity }) {
        viewModelFactory
    }
    private val characterFilterViewModel: CharacterFilterViewModel by viewModels {
        viewModelFactory
    }
    private val resultFilter = CharacterFilterModel()

    override fun initArguments() {
        arguments?.let {
            mode = it.getInt(MODE)
        }
    }

    override fun setUI() {
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
        sendCheckCountOfCharactersWithInterval()
    }

    override fun setupObservers() {
        observeFlows()
    }

    override fun setRefreshLayoutListener() {
        (requireActivity() as ToolbarManager).setRefreshClickListener {
            characterFilterViewModel.sendFilters(resultFilter)
        }
    }

    private fun observeFlows() = lifecycleScope.launchWhenStarted {
        characterFilterViewModel.charactersCountStateFlow.collect {
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

    override fun injectDependencies() {
        App.getAppComponent().injectCharacterFilterFragment(this)
    }

    private fun sendCheckCountOfCharactersWithInterval() {
        val disposable = publishSubject.debounce(1, TimeUnit.SECONDS)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe {
                characterFilterViewModel.sendFilters(resultFilter)
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

    companion object {

        private const val MODE = "mode"
        const val SEARCH_MODE = 0
        const val FILTER_MODE = 1

        fun newInstance(mode: Int) =
            CharacterFilterFragment().apply {
                arguments = Bundle().apply {
                    putInt(MODE, mode)
                }
            }
    }
}