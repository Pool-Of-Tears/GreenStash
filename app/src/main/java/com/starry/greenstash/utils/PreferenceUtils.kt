package com.starry.greenstash.utils

import android.content.Context
import android.content.SharedPreferences

object PreferenceUtils {
    private lateinit var prefs: SharedPreferences
    private const val PREFS_NAME = "myne_settings"

    // Preference keys
    const val APP_THEME = "theme_settings"
    const val MATERIAL_YOU = "material_you"
    const val DEFAULT_CURRENCY = "default_currency"
    const val DATE_FORMAT = "date_format"

    fun initialize(context: Context) {
        prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
    }

    fun keyExists(key: String): Boolean {
        if (prefs.contains(key))
            return true
        return false
    }

    fun putString(key: String, value: String) {
        val prefsEditor = prefs.edit()
        prefsEditor.putString(key, value)
        prefsEditor.apply()
    }

    fun putInt(key: String, value: Int) {
        val prefsEditor = prefs.edit()
        prefsEditor.putInt(key, value)
        prefsEditor.apply()
    }

    fun putBoolean(key: String, value: Boolean) {
        val prefsEditor = prefs.edit()
        prefsEditor.putBoolean(key, value)
        prefsEditor.apply()
    }

    fun getString(key: String, defValue: String): String? {
        return prefs.getString(key, defValue)
    }

    fun getInt(key: String, defValue: Int): Int {
        return prefs.getInt(key, defValue)
    }

    fun getBoolean(key: String, defValue: Boolean): Boolean {
        return prefs.getBoolean(key, defValue)
    }
}