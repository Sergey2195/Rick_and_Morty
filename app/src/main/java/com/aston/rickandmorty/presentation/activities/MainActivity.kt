package com.aston.rickandmorty.presentation.activities

import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.view.isVisible
import com.aston.rickandmorty.presentation.fragments.CharactersFragment
import com.aston.rickandmorty.R
import com.aston.rickandmorty.databinding.ActivityMainBinding
import com.google.android.material.appbar.AppBarLayout

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setupSwipeRefreshLayout()
        setSupportActionBar(binding.mainToolBar)
        setBackButtonState(false)
        setupToolbar()
        binding.collapsingToolBarLayout.setExpandedTitleColor(getColor(R.color.transparent))
        if (savedInstanceState == null){
            supportFragmentManager.beginTransaction()
                .add(R.id.mainFragmentContainer, CharactersFragment.newInstance())
                .commit()
        }
    }

    private fun setupToolbar() {
        binding.appBarLayout.addOnOffsetChangedListener { appBarLayout, verticalOffset ->
            if (verticalOffset == 0){
                binding.toolbarTextInputLayout.isVisible = false
                binding.searchButton.isVisible = false
                binding.filterButton.isVisible = false
                Log.d("SSV", "expanded")
            }else if (Math.abs(verticalOffset) >= appBarLayout.totalScrollRange){
                Log.d("SSV", "collapsed")
                binding.toolbarTextInputLayout.isVisible = true
                binding.searchButton.isVisible = true
                binding.filterButton.isVisible = true
            }else{
                Log.d("SSV", "idle")
            }
        }
    }

    private fun setupSwipeRefreshLayout() {
        binding.swipeRefreshLayout.setColorSchemeColors(
            ContextCompat.getColor(this, R.color.variantSecond),
            ContextCompat.getColor(this, R.color.variantSecond),
            ContextCompat.getColor(this, R.color.variantSecond),
        )
        binding.swipeRefreshLayout.setOnRefreshListener {
            //todo refresh logic
            binding.swipeRefreshLayout.isRefreshing = false
        }
    }

    private fun setBackButtonState(isEnabled: Boolean){
        supportActionBar?.setDisplayHomeAsUpEnabled(isEnabled)
    }
}