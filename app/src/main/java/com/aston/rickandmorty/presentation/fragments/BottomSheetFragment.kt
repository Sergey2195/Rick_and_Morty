package com.aston.rickandmorty.presentation.fragments

import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.ViewModelProvider
import com.aston.rickandmorty.R
import com.aston.rickandmorty.databinding.BottomSheetLayoutBinding
import com.aston.rickandmorty.presentation.BottomSheetInputData
import com.aston.rickandmorty.presentation.viewModels.MainViewModel
import com.google.android.material.bottomsheet.BottomSheetDialogFragment

class BottomSheetFragment : BottomSheetDialogFragment() {

    private val mainViewModel: MainViewModel by lazy {
        ViewModelProvider(requireActivity())[MainViewModel::class.java]
    }
    private var _binding: BottomSheetLayoutBinding? = null
    private val binding
        get() = _binding!!
    private var mode = 0
    private var inputData: BottomSheetInputData? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setStyle(STYLE_NORMAL, R.style.CustomBottomSheetDialogTheme)
        arguments?.let {
            mode = it.getInt(MODE_KEY)
            inputData = if (Build.VERSION.SDK_INT >= 33) {
                it.getParcelable(INPUT_DATA, BottomSheetInputData::class.java)
            }else{
                it.getParcelable(INPUT_DATA)
            }
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = BottomSheetLayoutBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupViews()
    }

    private fun setupViews() {
        when (mode) {
            MODE_FILTER -> filterMode()
            MODE_SEARCH -> searchMode()
        }
    }

    private fun filterMode() {
        binding.searchGroup.visibility = View.GONE
        binding.modeTextView.text = requireContext().getString(R.string.filter_title)
        binding.searchEditText.setText(inputData?.prevSearch ?: "")
    }

    private fun searchMode() {
        binding.filterGroup.visibility = View.GONE
        binding.modeTextView.text = requireContext().getString(R.string.search_title)
        binding.bottomSheetSaveButton.setOnClickListener {
            val search = binding.searchEditText.text.toString()
            mainViewModel.searchCharacterLiveData.postValue(search)
            this.dismiss()
        }
    }

    companion object {
        fun newInstance(
            mode: Int,
            inputData: BottomSheetInputData?
        ): BottomSheetFragment {
            return BottomSheetFragment().apply {
                arguments = Bundle().apply {
                    putInt(MODE_KEY, mode)
                    putParcelable(INPUT_DATA, inputData)
                }
            }
        }

        const val MODE_FILTER = 1
        const val MODE_SEARCH = 2
        private const val MODE_KEY = "mode key"
        private const val INPUT_DATA = "input data"
    }
}