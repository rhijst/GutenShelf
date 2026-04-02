package com.example.gutenshelf.cache

import android.content.Context
import android.content.SharedPreferences
import androidx.core.content.edit

class PreferenceStore(context: Context) {
    private val sharedPreferences: SharedPreferences =
        context.getSharedPreferences("gutenshelf_prefs", Context.MODE_PRIVATE)

    companion object {
        private const val KEY_SHOW_POPULAR = "show_popular_row"
    }

    fun isPopularRowEnabled(): Boolean {
        return sharedPreferences.getBoolean(KEY_SHOW_POPULAR, true)
    }

    fun setPopularRowEnabled(enabled: Boolean) {
        sharedPreferences.edit { putBoolean(KEY_SHOW_POPULAR, enabled) }
    }
}
