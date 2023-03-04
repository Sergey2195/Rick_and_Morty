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
            onParentScreen()
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

    override fun onParentScreen() {
        binding.appBarLayout.setExpanded(false, false)
        changeVisibilityToolBarElements(View.VISIBLE, View.GONE)
        viewModel.setIsOnParentFragment(true)
    }

    override fun onChildScreen() {
        changeVisibilityToolBarElements(View.GONE, View.VISIBLE)
        binding.appBarLayout.setExpanded(false, false)
        viewModel.setIsOnParentFragment(false)
    }

    override fun setBackButtonClickLister(clickListener: OnClickListener) {
        toolBarBackButtonClickListener = clickListener
        binding.backButtonOnToolbar.setOnClickListener(clickListener)
    }

    override fun setToolbarText(text: String) {
        binding.toolbarTextView.text = text
    }

    override fun setSearchClickListener(clickListener: OnClickListener?) {
        binding.searchButton.setOnClickListener(clickListener)
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
                !viewModel.isOnParentFragment() -> binding.backButtonOnToolbar.callOnClick()
                binding.bottomNavigation.selectedItemId == R.id.charactersBottomBtn -> hideApp()
                else -> binding.bottomNavigation.selectedItemId = R.id.charactersBottomBtn
            }
        }
    }

    private fun hideApp() {
        moveTaskToBack(true)
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

    private fun openEpisodesFragment() {
        viewModel.openEpisodesFragment()
        onParentScreen()
    }

    private fun openLocationFragment() {
        viewModel.openLocationFragment()
        onParentScreen()
    }

    private fun openCharactersFragment() {
        viewModel.openCharacterFragment()
        onParentScreen()
    }
}