package com.aston.rickandmorty.toolbarManager

import android.view.View.OnClickListener

interface ToolbarManager {
    fun setToolbarText(text: String)
    fun setBackButtonClickLister(clickListener: OnClickListener?)
}