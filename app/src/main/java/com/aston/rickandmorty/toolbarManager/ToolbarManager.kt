package com.aston.rickandmorty.toolbarManager

import android.view.View.OnClickListener
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout.OnRefreshListener

interface ToolbarManager {
    fun setToolbarText(text: String)
    fun setBackButtonClickListener(clickListener: OnClickListener?)
    fun setSearchButtonClickListener(clickListener: OnClickListener?)
    fun setFilterButtonClickListener(clickListener: OnClickListener?)
    fun setRefreshClickListener(swipeRefreshListener: OnRefreshListener?)
}