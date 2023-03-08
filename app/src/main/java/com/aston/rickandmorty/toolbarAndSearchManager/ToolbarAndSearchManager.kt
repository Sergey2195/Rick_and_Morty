package com.aston.rickandmorty.toolbarAndSearchManager

import android.view.View.OnClickListener

interface ToolbarAndSearchManager {
    fun setToolbarText(text: String)
    fun setSearchClickListener(clickListener: OnClickListener?)
    fun setBackButtonClickLister(clickListener: OnClickListener?)
    fun changeSearchVisibility(isVisible: Boolean)
    fun setSearchBackClickListener(clickListener: OnClickListener?)
    fun setSearchForwardClickListener(clickListener: OnClickListener?)
    fun setSearchPositionTextView(text: String)
    fun setSearchRequestTextView(text: String)
}