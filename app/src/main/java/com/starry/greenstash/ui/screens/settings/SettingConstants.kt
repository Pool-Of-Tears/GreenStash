package com.starry.greenstash.ui.screens.settings

/**
 * Enum class for the theme mode of the app.
 * [ThemeMode.Light] - Light theme
 * [ThemeMode.Dark] - Dark theme
 * [ThemeMode.Auto] - Follow system theme
 */
enum class ThemeMode {
    Light, Dark, Auto
}

enum class DateStyle {
    // Day in beginning
    DD_MM_YYYY,
    DD_MM_YY,
    // Month in beginning
    MM_DD_YYYY,
    MMM_DD_YYYY,
    MMM_DD_YY,
    // Year in beginning
    YYYY_MM_DD
}

/**
 * Converts a [DateStyle] to its corresponding display format string.
 *
 * @param style The [DateStyle] to convert.
 * @return The display format string.
 */
fun dateStyleToDisplayFormat(style: DateStyle): String = when (style) {
    DateStyle.DD_MM_YYYY -> "dd/MM/yyyy"
    DateStyle.DD_MM_YY   -> "dd/MM/yy"
    DateStyle.MM_DD_YYYY -> "MM/dd/yyyy"
    DateStyle.MMM_DD_YYYY -> "MMM/dd/yyyy"
    DateStyle.MMM_DD_YY   -> "MMM/dd/yy"
    DateStyle.YYYY_MM_DD -> "yyyy/MM/dd"
}

