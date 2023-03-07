package com.aston.rickandmorty.presentation.activities

import android.os.Bundle
import android.view.View
import android.view.View.OnClickListener
import androidx.activity.addCallback
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.view.isVisible
import com.aston.rickandmorty.R
import com.aston.rickandmorty.databinding.ActivityMainBinding
import com.aston.rickandmorty.presentation.fragments.CharactersFragment
import com.aston.rickandmorty.presentation.viewModels.MainViewModel
import com.aston.rickandmorty.toolbarManager.ToolbarManager
import com.google.android.material.appbar.AppBarLayout
import kotlin.math.abs

class MainActivity : AppCompatActivity(), ToolbarManager {
    private lateinit var binding: ActivityMainBinding
    private val viewModel: MainViewModel by viewModels()
    private var isOnParentScreen = true
    private var toolBarBackButtonClickListener: OnClickListener? = null

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
        observeLiveData()
    }

    private fun observeLiveData(){
        viewModel.isOnParentLiveData.observe(this){ isOnParent->
            changeIsOnParentState(isOnParent)
            isOnParentScreen = isOnParent
        }
    }

    private fun changeIsOnParentState(isOnParent: Boolean){
        binding.appBarLayout.setExpanded(false)
        when (isOnParent){
            true -> changeVisibilityToolBarElements(View.VISIBLE, View.GONE)
            false -> changeVisibilityToolBarElements(View.GONE, View.VISIBLE)
        }
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
                isCollapsed(verticalOffset, appBarLayout) -> collapsedToolBarChangedState(true, isOnParentScreen)
                isExpanded(verticalOffset) -> collapsedToolBarChangedState(false, isOnParentScreen)
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

    private fun collapsedToolBarChangedState(isCollapsed: Boolean, isOnParent: Boolean) {
        if (isOnParent) {
            binding.searchButton.isVisible = isCollapsed
            binding.filterButton.isVisible = isCollapsed
        } else {
            binding.backButtonOnToolbar.isVisible = isCollapsed
            binding.toolbarTextView.isVisible = isCollapsed
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

    override fun setToolbarText(text: String) {
        binding.toolbarTextView.text = text
    }

    override fun setSearchClickListener(clickListener: OnClickListener?) {
        binding.searchButton.setOnClickListener(clickListener)
    }

    override fun setBackButtonClickLister(clickListener: OnClickListener?) {
        toolBarBackButtonClickListener = clickListener
        binding.backButtonOnToolbar.setOnClickListener(clickListener)
    }

    private fun changeVisibilityToolBarElements(
        parentElementsVisibility: Int,
        childElementsVisibility: Int
    ) {
        binding.searchButton.visibility = parentElementsVisibility
        binding.filterButton.visibility = parentElementsVisibility
        binding.backButtonOnToolbar.visibility = childElementsVisibility
        binding.toolbarTextView.visibility = childElementsVisibility
    }

    private fun onBackPressedHandling() {
        onBackPressedDispatcher.addCallback(this) {
            when {
                !isOnParentScreen -> binding.backButtonOnToolbar.callOnClick()
                binding.bottomNavigation.selectedItemId == R.id.charactersBottomBtn -> hideApp()
                else -> binding.bottomNavigation.selectedItemId = R.id.charactersBottomBtn
            }
        }
    }

    private fun hideApp() {
        moveTaskToBack(true)
    }

    private fun setBottomNavigationBarClickListeners() {
        if (!isOnParentScreen){
            onBackPressedDispatcher.onBackPressed()
            return
        }
        binding.bottomNavigation.setOnItemSelectedListener { item ->
            if (!viewModel.isOnParentFragment()) binding.backButtonOnToolbar.callOnClick()
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

    private fun openEpisodesFragment() {
        viewModel.openEpisodesFragment()
    }

    private fun openLocationFragment() {
        viewModel.openLocationFragment()
    }

    private fun openCharactersFragment() {
        viewModel.openCharacterFragment()
    }
}