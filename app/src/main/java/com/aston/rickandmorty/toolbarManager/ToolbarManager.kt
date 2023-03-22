package com.aston.rickandmorty.toolbarManager

import android.view.View.OnClickListener

interface ToolbarManager {
    fun setToolbarText(text: String)
    fun setBackButtonClickListener(clickListener: OnClickListener?)
    fun setSearchButtonClickListener(clickListener: OnClickListener?)
    fun setFilterButtonClickListener(clickListener: OnClickListener?)
}