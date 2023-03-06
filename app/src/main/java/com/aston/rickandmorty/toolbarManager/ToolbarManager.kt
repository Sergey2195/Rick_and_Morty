package com.aston.rickandmorty.toolbarManager

import android.view.View.OnClickListener

interface ToolbarManager {
    fun setBackButtonClickLister(clickListener: OnClickListener)
    fun setToolbarText(text: String)
    fun setSearchClickListener(clickListener: OnClickListener?)
}