package com.aston.rickandmorty.presentation.fragments

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.widget.doOnTextChanged
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import com.aston.rickandmorty.R
import com.aston.rickandmorty.databinding.FragmentLocationFilterBinding
import com.aston.rickandmorty.domain.entity.LocationFilterModel
import com.aston.rickandmorty.presentation.App
import com.aston.rickandmorty.presentation.activities.MainActivity
import com.aston.rickandmorty.presentation.viewModels.LocationFilterViewModel
import com.aston.rickandmorty.presentation.viewModels.LocationsViewModel
import com.aston.rickandmorty.presentation.viewModels.MainViewModel
import com.aston.rickandmorty.presentation.viewModelsFactory.ViewModelFactory
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers
import io.reactivex.subjects.PublishSubject
import java.util.concurrent.TimeUnit
import javax.inject.Inject

class LocationFilterFragment : Fragment() {

    private var mode = -1
    private var _binding: FragmentLocationFilterBinding? = null
    private val binding
        get() = _binding!!
    @Inject
    lateinit var viewModelFactory: ViewModelFactory
    private val mainViewModel: MainViewModel by viewModels({ activity as MainActivity }) {
        viewModelFactory
    }
    private val locationViewModel: LocationsViewModel by viewModels({ activity as MainActivity }) {
        viewModelFactory
    }
    private val locationFilterViewModel: LocationFilterViewModel by viewModels(){
        viewModelFactory
    }
    private val resultFilter = LocationFilterModel()
    private val publishSubject = PublishSubject.create<Unit>()
    private val compositeDisposable = CompositeDisposable()

    override fun onAttach(context: Context) {
        App.getAppComponent().injectLocationFilterFragment(this)
        super.onAttach(context)
    }

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
        val filterGroupVisibility = when (mode) {
            FILTER -> View.VISIBLE
            SEARCH -> View.GONE
            else -> throw RuntimeException("unknown mode")
        }
        binding.filterGroup.visibility = filterGroupVisibility
        setupClickListener()
        setupObservers()
        sendCheckCountOfLocationWithInterval()
    }

    private fun sendCheckCountOfLocationWithInterval() {
        val disposable = publishSubject.debounce(1, TimeUnit.SECONDS)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe{
                locationFilterViewModel.sendFilters(resultFilter)
            }
        compositeDisposable.add(disposable)
    }

    private fun setupClickListener() {
        binding.saveButton.setOnClickListener {
            locationViewModel.setFilter(resultFilter)
            publishSubject.onNext(Unit)
        }
        binding.nameEditText.doOnTextChanged { text, _, _, _ ->
            resultFilter.nameFilter = textFilter(text)
            publishSubject.onNext(Unit)
        }
        binding.typeEditText.doOnTextChanged { text, _, _, _ ->
            resultFilter.typeFilter = textFilter(text)
            publishSubject.onNext(Unit)
        }
        binding.dimensionEditText.doOnTextChanged { text, _, _, _ ->
            resultFilter.dimensionFilter = textFilter(text)
            publishSubject.onNext(Unit)
        }
    }

    private fun textFilter(text: CharSequence?): String? {
        return if (text != null && text.isNotBlank()) {
            text.toString()
        } else null
    }

    private fun setupObservers() = lifecycleScope.launchWhenStarted {
        locationFilterViewModel.locationCountStateFlow.collect { count ->
            val text = when (count) {
                0 -> if (mode == FILTER) requireContext().getString(R.string.filter_initial) else requireContext().getString(
                    R.string.search_initial
                )
                -1 -> requireContext().getString(R.string.search_not_found)
                else -> requireContext().getString(R.string.search_count, count)
            }
            binding.countResultTextView.text = text
        }
    }

    override fun onStart() {
        super.onStart()
        mainViewModel.setIsOnParentLiveData(false)
    }

    override fun onDestroy() {
        super.onDestroy()
        compositeDisposable.dispose()
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