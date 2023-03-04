package com.aston.rickandmorty.presentation.fragments

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.aston.rickandmorty.R
import com.aston.rickandmorty.databinding.FragmentEpisodesBinding
import com.aston.rickandmorty.toolbarManager.ToolbarManager

class EpisodesFragment : Fragment() {

    private var _binding: FragmentEpisodesBinding? = null
    private val binding
        get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentEpisodesBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupBackButtonClickListener()
        binding.episodesOpenChild.setOnClickListener {
            startEpisodesDetailsFragment()
        }
    }

    private fun setupBackButtonClickListener() {
        (requireActivity() as ToolbarManager).setBackButtonClickLister {
            backFromEpisodesDetailsFragment()
        }
    }

    private fun backFromEpisodesDetailsFragment() {
        childFragmentManager.popBackStack()
        binding.episodesFragmentContainer.visibility = View.GONE
        binding.episodesContentLayout.visibility = View.VISIBLE
        (requireActivity() as ToolbarManager).onParentScreen()
    }

    private fun startEpisodesDetailsFragment() {
        binding.episodesContentLayout.visibility = View.GONE
        childFragmentManager.beginTransaction()
            .add(R.id.episodesFragmentContainer, EpisodeDetailsFragment.newInstance())
            .addToBackStack(null)
            .commit()
        binding.episodesFragmentContainer.visibility = View.VISIBLE
        (requireActivity() as ToolbarManager).onChildScreen()
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Log.d("SSV", "EpisodesFragment onCreate")
    }

    companion object {
        fun newInstance() = EpisodesFragment()
    }
}