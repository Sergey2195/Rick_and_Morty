package com.aston.rickandmorty

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.aston.rickandmorty.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setSupportActionBar(binding.mainToolBar)
        setBackButtonState(false)
        binding.collapsingToolBarLayout.setExpandedTitleColor(getColor(R.color.transparent))
        supportFragmentManager.beginTransaction()
            .add(R.id.mainFragmentContainer, MainScreenFragment.newInstance("", ""))
            .commit()
    }

    private fun setBackButtonState(isEnabled: Boolean){
        supportActionBar?.setDisplayHomeAsUpEnabled(isEnabled)
    }
}