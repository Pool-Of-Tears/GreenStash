/**
 * MIT License
 *
 * Copyright (c) [2022 - Present] Stɑrry Shivɑm
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */


package com.starry.greenstash.utils

import android.content.Context
import android.content.SharedPreferences
import com.starry.greenstash.ui.screens.settings.DateStyle

class PreferenceUtil(context: Context) {

    companion object {
        private const val PREFS_NAME = "greenstash_settings"

        // Main preference keys
        const val APP_THEME_INT = "theme_settings"
        const val AMOLED_THEME_BOOL = "amoled_theme"
        const val MATERIAL_YOU_BOOL = "material_you"
        const val DEFAULT_CURRENCY_STR = "default_currency_code"
        const val DATE_FORMAT_STR = "date_format"
        const val APP_LOCK_BOOL = "app_lock"
        const val GOAL_CARD_STYLE_INT = "goal_card_style"

        // Goal filter preferences
        const val GOAL_FILTER_FIELD_INT = "goal_filter_field"
        const val GOAL_FILTER_SORT_TYPE_INT = "goal_filter_sort_type"

        // Onboarding preferences
        const val HOME_SCREEN_ONBOARDING_BOOL = "show_home_screen_onboarding"
        const val INPUT_SCREEN_ONBOARDING_BOOL = "show_input_onboarding"
        const val INPUT_REMOVE_DEADLINE_TIP_BOOL = "input_remove_deadline_tip"
        const val INFO_TRANSACTION_SWIPE_TIP_BOOL = "info_transaction_swipe_tip"
    }

    private var prefs: SharedPreferences

    init {
        prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        // Pre-populate some preference data with default values
        if (!keyExists(DEFAULT_CURRENCY_STR)) {
            putString(DEFAULT_CURRENCY_STR, "USD")
        }
        if (!keyExists(DATE_FORMAT_STR)) {
            putString(DATE_FORMAT_STR, DateStyle.DateMonthYear.pattern)
        }
    }

    private fun keyExists(key: String): Boolean {
        return prefs.contains(key)
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