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


package com.starry.greenstash.ui.screens.settings

import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.runtime.Composable
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.starry.greenstash.ui.screens.home.GoalCardStyle
import com.starry.greenstash.utils.PreferenceUtil
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

/**
 * Enum class for the theme mode of the app.
 * [ThemeMode.Light] - Light theme
 * [ThemeMode.Dark] - Dark theme
 * [ThemeMode.Auto] - Follow system theme
 */
enum class ThemeMode {
    Light, Dark, Auto
}

/**
 * Sealed class for the date style of the app.
 * [DateStyle.DateMonthYear] - Date in the format dd/MM/yyyy
 * [DateStyle.YearMonthDate] - Date in the format yyyy/MM/dd
 */
sealed class DateStyle(val pattern: String) {
    data object DateMonthYear : DateStyle("dd/MM/yyyy")
    data object YearMonthDate : DateStyle("yyyy/MM/dd")
}

@HiltViewModel
class SettingsViewModel @Inject constructor(
    private val preferenceUtil: PreferenceUtil
) : ViewModel() {

    private val _theme = MutableLiveData(ThemeMode.Auto)
    private val _amoledTheme = MutableLiveData(false)
    private val _materialYou = MutableLiveData(Build.VERSION.SDK_INT >= Build.VERSION_CODES.S)
    private val _goalCardStyle = MutableLiveData(GoalCardStyle.Classic)

    val theme: LiveData<ThemeMode> = _theme
    val amoledTheme: LiveData<Boolean> = _amoledTheme
    val materialYou: LiveData<Boolean> = _materialYou
    val goalCardStyle: LiveData<GoalCardStyle> = _goalCardStyle

    // Initialize preferences --------------------------------------------
    init {
        _theme.value = ThemeMode.entries.toTypedArray()[getThemeValue()]
        _amoledTheme.value = getAmoledThemeValue()
        _materialYou.value = getMaterialYouValue()
        _goalCardStyle.value = GoalCardStyle.entries.toTypedArray()[getGoalCardStyleValue()]
    }

    // Setters for preferences --------------------------------------------
    fun setTheme(newTheme: ThemeMode) {
        _theme.postValue(newTheme)
        preferenceUtil.putInt(PreferenceUtil.APP_THEME_INT, newTheme.ordinal)
    }

    fun setAmoledTheme(newValue: Boolean) {
        _amoledTheme.postValue(newValue)
        preferenceUtil.putBoolean(PreferenceUtil.AMOLED_THEME_BOOL, newValue)
    }

    fun setMaterialYou(newValue: Boolean) {
        _materialYou.postValue(newValue)
        preferenceUtil.putBoolean(PreferenceUtil.MATERIAL_YOU_BOOL, newValue)
    }

    fun setGoalCardStyle(newValue: GoalCardStyle) {
        _goalCardStyle.postValue(newValue)
        preferenceUtil.putInt(PreferenceUtil.GOAL_CARD_STYLE_INT, newValue.ordinal)
    }

    fun setDateStyle(newValue: String) {
        preferenceUtil.putString(PreferenceUtil.DATE_FORMAT_STR, newValue)
    }

    fun setDefaultCurrency(newValue: String) {
        preferenceUtil.putString(PreferenceUtil.DEFAULT_CURRENCY_STR, newValue)
    }

    fun setAppLock(newValue: Boolean) {
        preferenceUtil.putBoolean(PreferenceUtil.APP_LOCK_BOOL, newValue)
    }

    // Getters for preferences --------------------------------------------
    fun getThemeValue() = preferenceUtil.getInt(
        PreferenceUtil.APP_THEME_INT, ThemeMode.Auto.ordinal
    )

    fun getAmoledThemeValue() = preferenceUtil.getBoolean(
        PreferenceUtil.AMOLED_THEME_BOOL, false
    )

    fun getMaterialYouValue() = preferenceUtil.getBoolean(
        PreferenceUtil.MATERIAL_YOU_BOOL, Build.VERSION.SDK_INT >= Build.VERSION_CODES.S
    )

    fun getGoalCardStyleValue() = preferenceUtil.getInt(
        PreferenceUtil.GOAL_CARD_STYLE_INT, GoalCardStyle.Classic.ordinal
    )

    fun getDateStyleValue() = preferenceUtil.getString(
        PreferenceUtil.DATE_FORMAT_STR, DateStyle.DateMonthYear.pattern
    )

    fun getDefaultCurrencyValue() = preferenceUtil.getString(
        PreferenceUtil.DEFAULT_CURRENCY_STR, "USD"
    )

    fun getAppLockValue() = preferenceUtil.getBoolean(
        PreferenceUtil.APP_LOCK_BOOL, false
    )

    /**
     * Get the current theme of the app, regardless of the system theme.
     * This will always return either [ThemeMode.Light] or [ThemeMode.Dark].
     * If user has set the theme to Auto it will return the system theme,
     * again Light or Dark instead of [ThemeMode.Auto].
     */
    @Composable
    fun getCurrentTheme(): ThemeMode {
        return if (theme.value == ThemeMode.Auto) {
            if (isSystemInDarkTheme()) ThemeMode.Dark else ThemeMode.Light
        } else theme.value!!
    }
}