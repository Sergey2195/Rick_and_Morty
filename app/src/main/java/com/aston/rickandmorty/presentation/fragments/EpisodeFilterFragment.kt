package com.aston.rickandmorty.presentation.fragments

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.widget.doOnTextChanged
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import com.aston.rickandmorty.R
import com.aston.rickandmorty.databinding.FragmentEpisodeFilterBinding
import com.aston.rickandmorty.domain.entity.EpisodeFilterModel
import com.aston.rickandmorty.presentation.App
import com.aston.rickandmorty.presentation.activities.MainActivity
import com.aston.rickandmorty.presentation.viewModels.EpisodeFilterViewModel
import com.aston.rickandmorty.presentation.viewModels.EpisodesViewModel
import com.aston.rickandmorty.presentation.viewModels.MainViewModel
import com.aston.rickandmorty.presentation.viewModelsFactory.ViewModelFactory
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers
import io.reactivex.subjects.PublishSubject
import java.util.concurrent.TimeUnit
import javax.inject.Inject

class EpisodeFilterFragment : Fragment() {

    private var mode = -1
    private var _binding: FragmentEpisodeFilterBinding? = null
    private val binding
        get() = _binding!!
    @Inject
    lateinit var viewModelFactory: ViewModelFactory
    private val mainViewModel: MainViewModel by viewModels({ activity as MainActivity }) {
        viewModelFactory
    }
    private val viewModel: EpisodesViewModel by viewModels({activity as MainActivity }) {
        viewModelFactory
    }
    private val filterViewModel: EpisodeFilterViewModel by viewModels{
        viewModelFactory
    }
    private val component by lazy {
        ((requireActivity().application) as App).component
    }
    private val resultFilter = EpisodeFilterModel()
    private val publishSubject = PublishSubject.create<Unit>()
    private val compositeDisposable = CompositeDisposable()

    override fun onAttach(context: Context) {
        component.injectEpisodeFilterFragment(this)
        super.onAttach(context)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            mode = it.getInt(MODE)
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        mainViewModel.setIsOnParentLiveData(false)
        val filterGroupVisibility = when (mode) {
            FILTER -> View.VISIBLE
            SEARCH -> View.GONE
            else -> throw RuntimeException("unknown mode")
        }
        binding.filterGroup.visibility = filterGroupVisibility
        setupClickListener()
        setupObservers()
        sendCheckCountOfEpisodesWithInterval()
    }

    private fun sendCheckCountOfEpisodesWithInterval() {
        val disposable = publishSubject.debounce(1, TimeUnit.SECONDS)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe { filterViewModel.sendFilters(resultFilter) }
        compositeDisposable.add(disposable)
    }

    private fun setupObservers() = lifecycleScope.launchWhenStarted {
        filterViewModel.countOfEpisodesStateFlow.collect { count ->
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

    private fun setupClickListener() {
        binding.saveButton.setOnClickListener { viewModel.setFilter(resultFilter) }
        binding.nameEditText.doOnTextChanged { text, _, _, _ ->
            resultFilter.name = textFilter(text)
            publishSubject.onNext(Unit)
        }
        binding.episodeEditText.doOnTextChanged { text, _, _, _ ->
            resultFilter.episode = textFilter(text)
            publishSubject.onNext(Unit)
        }
    }

    private fun textFilter(text: CharSequence?): String? {
        return if (text != null && text.isNotBlank()) {
            text.toString()
        } else null
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentEpisodeFilterBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onDestroy() {
        super.onDestroy()
        compositeDisposable.dispose()
    }

    companion object {
        const val FILTER = 1
        const val SEARCH = 0
        private const val MODE = "mode"

        fun newInstance(mode: Int) =
            EpisodeFilterFragment().apply {
                arguments = Bundle().apply {
                    putInt(MODE, mode)
                }
            }
    }
}