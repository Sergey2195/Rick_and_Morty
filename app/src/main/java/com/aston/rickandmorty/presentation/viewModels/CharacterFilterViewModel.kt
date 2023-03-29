package com.aston.rickandmorty.presentation.viewModels

import androidx.lifecycle.ViewModel
import com.aston.rickandmorty.domain.entity.CharacterFilterModel
import com.aston.rickandmorty.domain.useCases.CountOfCharactersUseCase
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import javax.inject.Inject

class CharacterFilterViewModel @Inject constructor(
    private val countOfCharactersUseCase: CountOfCharactersUseCase
) : ViewModel() {

    private val compositeDisposable = CompositeDisposable()
    private val _charactersCountStateFlow = MutableStateFlow(0)
    val charactersCountStateFlow = _charactersCountStateFlow.asStateFlow()

    fun sendFilters(filterModel: CharacterFilterModel) {
        val disposable = countOfCharactersUseCase.invoke(
            filterModel.nameFilter,
            filterModel.statusFilter,
            filterModel.speciesFilter,
            filterModel.typeFilter,
            filterModel.genderFilter
        ).subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe({ result ->
                _charactersCountStateFlow.value = result
            }, {
                _charactersCountStateFlow.value = -1
            })
        compositeDisposable.add(disposable)
    }

    override fun onCleared() {
        super.onCleared()
        compositeDisposable.dispose()
    }
}