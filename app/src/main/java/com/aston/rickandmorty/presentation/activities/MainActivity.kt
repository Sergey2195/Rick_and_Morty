package com.aston.rickandmorty.presentation.activities

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.aston.rickandmorty.presentation.fragments.CharactersFragment
import com.aston.rickandmorty.R
import com.aston.rickandmorty.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setupSwipeRefreshLayout()
        setSupportActionBar(binding.mainToolBar)
        setBackButtonState(false)
        binding.collapsingToolBarLayout.setExpandedTitleColor(getColor(R.color.transparent))
        if (savedInstanceState == null){
            supportFragmentManager.beginTransaction()
                .add(R.id.mainFragmentContainer, CharactersFragment.newInstance())
                .commit()
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