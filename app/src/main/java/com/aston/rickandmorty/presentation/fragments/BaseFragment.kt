package com.aston.rickandmorty.presentation.fragments

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.LayoutRes
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.viewbinding.ViewBinding
import com.aston.rickandmorty.presentation.activities.MainActivity
import com.aston.rickandmorty.presentation.viewModels.MainViewModel
import com.aston.rickandmorty.presentation.viewModelsFactory.ViewModelFactory
import javax.inject.Inject

abstract class BaseFragment<VB : ViewBinding>(
    @LayoutRes private val layoutRes: Int,
    private val bindingInflater: (inflater: LayoutInflater) -> VB
) : Fragment(layoutRes) {

    private var _binding: VB? = null
    protected val binding: VB
        get() = _binding ?: throw RuntimeException("Binding cannot be null")
    protected val mainViewModel: MainViewModel by viewModels({ activity as MainActivity }) {
        viewModelFactory
    }

    protected abstract fun setUI()
    protected abstract fun setupObservers()
    protected abstract fun injectDependencies()
    protected abstract fun initArguments()

    @Inject
    lateinit var viewModelFactory: ViewModelFactory


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = bindingInflater.invoke(inflater)
        return binding.root
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        initArguments()
        setupObservers()
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setUI()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    override fun onAttach(context: Context) {
        injectDependencies()
        super.onAttach(context)
    }
}