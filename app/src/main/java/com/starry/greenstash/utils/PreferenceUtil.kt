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
import androidx.core.content.edit
import com.starry.greenstash.ui.screens.settings.DateStyle

/**
 * Utility class to handle shared preferences for the app.
 * @param context The context of the app.
 */
class PreferenceUtil(context: Context) {

    companion object {
        // Shared preferences file name
        private const val PREFS_NAME = "greenstash_settings"

        // Main preference keys
        const val APP_THEME_INT = "theme_settings"
        const val AMOLED_THEME_BOOL = "amoled_theme"
        const val MATERIAL_YOU_BOOL = "material_you"
        const val DEFAULT_CURRENCY_STR = "default_currency_code"
        const val DATE_STYLE_INT = "date_style"
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

    // Shared preferences instance
    private var prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)

    init {
        // Pre-populate some preference data with default values
        if (!keyExists(DEFAULT_CURRENCY_STR)) {
            putString(DEFAULT_CURRENCY_STR, "USD")
        }
        if (!keyExists(DATE_STYLE_INT)) {
            putInt(DATE_STYLE_INT, DateStyle.DD_MM_YYYY.ordinal)
        }
    }

    /**
     * Check if a key exists in the shared preferences.
     * @param key The key to check.
     * @return true if the key exists, false otherwise.
     */
    private fun keyExists(key: String): Boolean {
        return prefs.contains(key)
    }

    /**
     * Put a string value in the shared preferences.
     * @param key The key to store the value under.
     * @param value The value to store.
     */
    fun putString(key: String, value: String) {
        prefs.edit { putString(key, value) }
    }

    /**
     * Put an integer value in the shared preferences.
     * @param key The key to store the value under.
     * @param value The value to store.
     */
    fun putInt(key: String, value: Int) {
        prefs.edit { putInt(key, value) }
    }

    /**
     * Put a boolean value in the shared preferences.
     * @param key The key to store the value under.
     * @param value The value to store.
     */
    fun putBoolean(key: String, value: Boolean) {
        prefs.edit { putBoolean(key, value) }
    }

    /**
     * Get a string value from the shared preferences.
     * @param key The key to get the value from.
     * @param defValue The default value to return if the key does not exist.
     * @return The value stored under the key, or the default value if the key does not exist.
     */
    fun getString(key: String, defValue: String): String? {
        return prefs.getString(key, defValue)
    }

    /**
     * Get an integer value from the shared preferences.
     * @param key The key to get the value from.
     * @param defValue The default value to return if the key does not exist.
     * @return The value stored under the key, or the default value if the key does not exist.
     */
    fun getInt(key: String, defValue: Int): Int {
        return prefs.getInt(key, defValue)
    }

    /**
     * Get a boolean value from the shared preferences.
     * @param key The key to get the value from.
     * @param defValue The default value to return if the key does not exist.
     * @return The value stored under the key, or the default value if the key does not exist.
     */
    fun getBoolean(key: String, defValue: Boolean): Boolean {
        return prefs.getBoolean(key, defValue)
    }
}