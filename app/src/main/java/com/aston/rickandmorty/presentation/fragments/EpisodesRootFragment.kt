package com.aston.rickandmorty.presentation.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.aston.rickandmorty.R
import com.aston.rickandmorty.databinding.FragmentEpisodesRootBinding
import com.aston.rickandmorty.toolbarManager.ToolbarManager

class EpisodesRootFragment : Fragment() {

    private var _binding: FragmentEpisodesRootBinding? = null
    private val binding
        get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentEpisodesRootBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        if (savedInstanceState == null){
            childFragmentManager.beginTransaction()
                .add(R.id.episodeFragmentContainerRoot, EpisodesAllFragment.newInstance())
                .commit()
        }
    }

    private fun setupBackButtonClickListener() {
        (requireActivity() as ToolbarManager).setBackButtonClickListener {
            childFragmentManager.popBackStack()
        }
    }

    override fun onStart() {
        super.onStart()
        setupBackButtonClickListener()
    }

    companion object {
        fun newInstance() = EpisodesRootFragment()
    }
}