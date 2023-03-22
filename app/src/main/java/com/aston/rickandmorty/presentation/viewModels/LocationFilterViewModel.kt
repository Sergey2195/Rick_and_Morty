package com.aston.rickandmorty.presentation.viewModels

import androidx.lifecycle.ViewModel
import com.aston.rickandmorty.domain.entity.LocationFilterModel
import com.aston.rickandmorty.domain.useCases.CountOfLocationUseCase
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import javax.inject.Inject

class LocationFilterViewModel @Inject constructor(
    private val countOfLocationsUseCase: CountOfLocationUseCase
) : ViewModel() {

    private val _locationCountStateFlow = MutableStateFlow(0)
    val locationCountStateFlow = _locationCountStateFlow.asStateFlow()
    private val compositeDisposable = CompositeDisposable()

    fun sendFilters(filterModel: LocationFilterModel) {
        val disposable = countOfLocationsUseCase.invoke(
            filterModel.nameFilter,
            filterModel.typeFilter,
            filterModel.dimensionFilter
        ).subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe({ result ->
                _locationCountStateFlow.value = result
            }, {
                _locationCountStateFlow.value = -1
            })
        compositeDisposable.add(disposable)
    }

    override fun onCleared() {
        super.onCleared()
        compositeDisposable.dispose()
    }
}