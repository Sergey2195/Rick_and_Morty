package com.aston.rickandmorty.toolbarManager

import android.view.View

interface ToolbarManager {
    fun onParentScreen()
    fun onChildScreen()
    fun setBackButtonClickLister(clickListener: View.OnClickListener)
    fun setToolbarText(text: String)
}