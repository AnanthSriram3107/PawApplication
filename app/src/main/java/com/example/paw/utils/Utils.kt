package com.example.paw.utils

import android.content.Context
import android.view.Window
import androidx.core.content.ContextCompat
import androidx.core.view.WindowCompat

class Utils {
    companion object {
        fun setStatusBarColor(window: Window, color: Int, context: Context) {
            WindowCompat.setDecorFitsSystemWindows(window, false)
            window.statusBarColor = ContextCompat.getColor(context, color)
        }
    }
}