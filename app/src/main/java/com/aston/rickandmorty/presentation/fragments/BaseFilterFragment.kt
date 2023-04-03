package com.aston.rickandmorty.presentation.fragments

import android.view.LayoutInflater
import androidx.annotation.LayoutRes
import androidx.viewbinding.ViewBinding
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.subjects.PublishSubject

abstract class BaseFilterFragment<VB : ViewBinding>(
    @LayoutRes private val layoutRes: Int,
    bindingInflater: (inflater: LayoutInflater) -> VB
) : BaseFragment<VB>(layoutRes, bindingInflater) {

    protected val publishSubject = PublishSubject.create<Unit>()
    protected val compositeDisposable = CompositeDisposable()
    protected var mode = -1

    override fun onDetach() {
        super.onDetach()
        compositeDisposable.dispose()
    }

    override fun onStart() {
        super.onStart()
        mainViewModel.setIsOnParentLiveData(false)
    }
}