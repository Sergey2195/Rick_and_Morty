package com.aston.rickandmorty.presentation.activities

import android.os.Bundle
import android.view.View
import android.view.View.OnClickListener
import android.widget.Toast
import androidx.activity.addCallback
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.view.isVisible
import androidx.lifecycle.lifecycleScope
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.aston.rickandmorty.R
import com.aston.rickandmorty.databinding.ActivityMainBinding
import com.aston.rickandmorty.presentation.App
import com.aston.rickandmorty.presentation.viewModels.MainViewModel
import com.aston.rickandmorty.presentation.viewModelsFactory.ViewModelFactory
import com.aston.rickandmorty.toolbarManager.ToolbarManager
import com.google.android.material.appbar.AppBarLayout
import com.google.android.material.snackbar.Snackbar
import javax.inject.Inject
import kotlin.math.abs

class MainActivity : AppCompatActivity(), ToolbarManager {

    private val binding: ActivityMainBinding by lazy {
        ActivityMainBinding.inflate(layoutInflater)
    }

    @Inject
    lateinit var viewModelFactory: ViewModelFactory
    private val viewModel: MainViewModel by viewModels() {
        viewModelFactory
    }
    private val component = App.getAppComponent()
    private var isOnParentScreen = true
    private var toolBarBackButtonClickListener: OnClickListener? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        component.injectMainActivity(this)
        super.onCreate(savedInstanceState)
        setContentView(binding.root)
        connectToRouter()
        if (savedInstanceState == null) {
            viewModel.openCharacterFragment()
        }
        setupSwipeRefreshLayout()
        setupToolbar()
        onBackPressedHandling()
        setBottomNavigationBarClickListeners()
        observeLiveData()
        observeLoadingState()
        observeInternetConnection()
        observeErrorStateFlow()
    }

    private fun observeLoadingState() = lifecycleScope.launchWhenStarted {
        viewModel.getLoadingStateFlow().collect { isLoading ->
            binding.swipeRefreshLayout.isRefreshing = isLoading
        }
    }

    private fun observeLiveData() {
        viewModel.isOnParentLiveData.observe(this) { isOnParent ->
            changeIsOnParentState(isOnParent)
            isOnParentScreen = isOnParent
        }
    }

    private fun observeErrorStateFlow() {
        lifecycleScope.launchWhenStarted {
            viewModel.getErrorStateFlow().collect { isError ->
                if (isError) {
                    Toast.makeText(this@MainActivity, R.string.error_connection, Toast.LENGTH_SHORT)
                        .show()
                }
            }
        }
    }

    private fun observeInternetConnection() = lifecycleScope.launchWhenStarted {
        val noConnectionSnackBar = Snackbar.make(
            binding.toolbarTextView,
            getString(R.string.network_error),
            Snackbar.LENGTH_INDEFINITE
        )
        val connectedSnackBar = Snackbar.make(
            binding.toolbarTextView,
            getString(R.string.network_connected),
            Snackbar.LENGTH_SHORT
        )
        var firstShown = true
        viewModel.getNetworkStatusIsAvailableStateFlow().collect { connected ->
            if (!connected) {
                noConnectionSnackBar.show()
            } else if (!firstShown) {
                connectedSnackBar.show()
            }
            firstShown = false
        }
    }

    private fun changeIsOnParentState(isOnParent: Boolean) {
        binding.appBarLayout.setExpanded(false)
        when (isOnParent) {
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
                isCollapsed(verticalOffset, appBarLayout) -> collapsedToolBarChangedState(
                    true,
                    isOnParentScreen
                )
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
        }
        binding.toolbarTextView.isVisible = isCollapsed
    }

    private fun setupSwipeRefreshLayout() {
        binding.swipeRefreshLayout.setColorSchemeColors(
            ContextCompat.getColor(this, R.color.variantSecond),
            ContextCompat.getColor(this, R.color.variantSecond),
            ContextCompat.getColor(this, R.color.variantSecond),
        )
    }

    override fun setToolbarText(text: String) {
        binding.toolbarTextView.text = text
    }

    override fun setBackButtonClickListener(clickListener: OnClickListener?) {
        toolBarBackButtonClickListener = clickListener
        binding.backButtonOnToolbar.setOnClickListener(clickListener)
    }

    override fun setSearchButtonClickListener(clickListener: OnClickListener?) {
        binding.searchButton.setOnClickListener(clickListener)
    }

    override fun setFilterButtonClickListener(clickListener: OnClickListener?) {
        binding.filterButton.setOnClickListener(clickListener)
    }

    override fun setRefreshClickListener(swipeRefreshListener: SwipeRefreshLayout.OnRefreshListener?) {
        binding.swipeRefreshLayout.setOnRefreshListener(swipeRefreshListener)
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
        if (!isOnParentScreen) {
            onBackPressedDispatcher.onBackPressed()
            return
        }
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
    }

    private fun openLocationFragment() {
        viewModel.openLocationFragment()
    }

    private fun openCharactersFragment() {
        viewModel.openCharacterFragment()
    }
}