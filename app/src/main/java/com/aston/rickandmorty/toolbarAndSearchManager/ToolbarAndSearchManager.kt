package com.aston.rickandmorty.toolbarAndSearchManager

import android.view.View.OnClickListener

interface ToolbarAndSearchManager {
    fun setToolbarText(text: String)
    fun setSearchClickListener(clickListener: OnClickListener?)
    fun setBackButtonClickLister(clickListener: OnClickListener?)
}