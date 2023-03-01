package com.aston.rickandmorty.presentation.activities

import android.os.Bundle
import android.view.View
import android.view.View.OnClickListener
import androidx.activity.addCallback
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.view.isVisible
import androidx.lifecycle.lifecycleScope
import com.aston.rickandmorty.R
import com.aston.rickandmorty.databinding.ActivityMainBinding
import com.aston.rickandmorty.presentation.fragments.CharactersFragment
import com.aston.rickandmorty.presentation.viewModels.MainViewModel
import com.aston.rickandmorty.toolbarManager.ToolbarManager
import com.google.android.material.appbar.AppBarLayout
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlin.math.abs

class MainActivity : AppCompatActivity(), ToolbarManager {
    private lateinit var binding: ActivityMainBinding
    private val viewModel: MainViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        connectToRouter()
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        if (savedInstanceState == null) {
            supportFragmentManager.beginTransaction()
                .add(R.id.mainFragmentContainer, CharactersFragment.newInstance())
                .commit()
        }
        setupSwipeRefreshLayout()
        setupToolbar()
        onBackPressedHandling()
        setBottomNavigationBarClickListeners()
    }

    private fun setupToolbar() {
        setSupportActionBar(binding.mainToolBar)
        binding.collapsingToolBarLayout.setExpandedTitleColor(getColor(R.color.transparent))
        setupToolbarListener()
    }

    private fun connectToRouter() {
        viewModel.attachRouter(this)
    }

    override fun onDestroy() {
        super.onDestroy()
        viewModel.detachRouter()
    }

    private fun setupToolbarListener() {
        binding.appBarLayout.addOnOffsetChangedListener { appBarLayout, verticalOffset ->
            when {
                isCollapsed(verticalOffset, appBarLayout) -> collapsedToolBarChangedState(true)
                isExpanded(verticalOffset) -> collapsedToolBarChangedState(false)
                else -> {}
            }
        }
    }

    private fun isCollapsed(verticalOffset: Int, appBarLayout: AppBarLayout): Boolean {
        return abs(verticalOffset) >= appBarLayout.totalScrollRange
    }

    private fun isExpanded(verticalOffset: Int): Boolean {
        return verticalOffset == 0
    }

    private fun collapsedToolBarChangedState(isCollapsed: Boolean) {
        if (viewModel.isOnParentFragment()) {
            binding.toolbarTextInputLayout.isVisible = isCollapsed
            binding.searchButton.isVisible = isCollapsed
            binding.filterButton.isVisible = isCollapsed
        } else {
            binding.backButtonOnToolbar.isVisible = isCollapsed
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

    override fun onParentScreen() {
        binding.appBarLayout.setExpanded(true, false)
        changeVisibilityToolBarElements(View.VISIBLE, View.GONE)
        viewModel.setIsOnParentFragment(true)
    }

    override fun onChildScreen() {
        changeVisibilityToolBarElements(View.GONE, View.VISIBLE)
        binding.appBarLayout.setExpanded(false, false)
        viewModel.setIsOnParentFragment(false)
    }

    override fun setBackButtonClickLister(clickListener: OnClickListener) {
        binding.backButtonOnToolbar.setOnClickListener(clickListener)
    }

    private fun changeVisibilityToolBarElements(
        parentElementsVisibility: Int,
        childElementsVisibility: Int
    ) {
        binding.toolbarEditText.visibility = parentElementsVisibility
        binding.searchButton.visibility = parentElementsVisibility
        binding.filterButton.visibility = parentElementsVisibility
        binding.backButtonOnToolbar.visibility = childElementsVisibility
    }

    private fun onBackPressedHandling() {
//        onBackPressedDispatcher.addCallback(this) {
//        }
    }

    private fun setBottomNavigationBarClickListeners() {
        binding.bottomNavigation.setOnItemSelectedListener { item ->
            when (item.itemId) {
                R.id.charactersBottomBtn -> {
                    openCharactersFragment()
                    true
                }
                R.id.locationBottomBtn -> {
                    openLocationFragment()
                    true
                }
                R.id.episodesBottomBtn -> {
                    openEpisodesFragment()
                    true
                }
                else -> {
                    false
                }
            }
        }
    }

    private fun openEpisodesFragment(){
        viewModel.openEpisodesFragment()
    }

    private fun openLocationFragment(){
        viewModel.openLocationFragment()
    }

    private fun openCharactersFragment() {
        viewModel.openCharacterFragment()
    }
}