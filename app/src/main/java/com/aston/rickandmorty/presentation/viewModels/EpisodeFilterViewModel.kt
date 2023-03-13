package com.aston.rickandmorty.presentation.viewModels

import androidx.lifecycle.ViewModel
import com.aston.rickandmorty.data.RepositoryImpl
import com.aston.rickandmorty.domain.entity.EpisodeFilterModel
import com.aston.rickandmorty.domain.useCases.CountOfEpisodesUseCase
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow

class EpisodeFilterViewModel : ViewModel() {
    private val repository = RepositoryImpl
    private val countOfEpisodesUseCase = CountOfEpisodesUseCase(repository)
    private val _countOfEpisodesStateFlow = MutableStateFlow(0)
    val countOfEpisodesStateFlow = _countOfEpisodesStateFlow.asStateFlow()
    private val compositeDisposable = CompositeDisposable()

    fun sendFilters(filter: EpisodeFilterModel) {
        val disposable = countOfEpisodesUseCase.invoke(filter.name, filter.episode)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe({
                _countOfEpisodesStateFlow.value = it
            }, { _countOfEpisodesStateFlow.value = -1 })
        compositeDisposable.add(disposable)
    }

    override fun onCleared() {
        super.onCleared()
        compositeDisposable.dispose()
    }
}