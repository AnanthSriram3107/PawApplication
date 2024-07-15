package com.example.paw.utils

import android.content.Context
import android.content.SharedPreferences

class SharedPreferenceManager {
    private val sharedPreferenceFileName = "dogbreed"

    fun getSharedPreferences(context: Context): SharedPreferences {
        return context.getSharedPreferences(
            sharedPreferenceFileName,
            Context.MODE_PRIVATE
        )
    }

    fun setStringValue(
        context: Context, key: String,
        value: String
    ) {
        val prefs: SharedPreferences.Editor =
            getSharedPreferences(context).edit()
        prefs.putString(key, value)
        prefs.apply()
    }

    fun getStringValue(context: Context, key: String, defaultValue: String): String? {
        return getSharedPreferences(context).getString(key, defaultValue)
    }
}