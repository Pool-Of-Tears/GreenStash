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

import android.content.ActivityNotFoundException
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Build
import androidx.biometric.BiometricManager.Authenticators.BIOMETRIC_STRONG
import androidx.biometric.BiometricManager.Authenticators.BIOMETRIC_WEAK
import androidx.biometric.BiometricManager.Authenticators.DEVICE_CREDENTIAL
import androidx.core.net.toUri
import java.io.BufferedReader
import java.io.IOException
import java.io.InputStreamReader
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.ZoneId
import java.util.TimeZone


/**
 * A collection of utility functions.
 */
object Utils {

    // Regular expression to match URLs in text.
    private val urlRegex = Regex(
        pattern = """(?i)\b((https?://|www\.)\S+|[a-z0-9.-]+\.[a-z]{2,}\b\S*)"""
    )

    /**
     * Retrieves the appropriate authenticators based on the Android version.
     *
     * - For Android 9 (Pie) and Android 10 (Q), the authenticators are `BIOMETRIC_WEAK` and `DEVICE_CREDENTIAL`.
     * - For Android 11 (R) and above, the authenticators are `BIOMETRIC_STRONG` and `DEVICE_CREDENTIAL`.
     * - For Android versions below 9, while the authenticators `BIOMETRIC_STRONG` and `DEVICE_CREDENTIAL` are not officially supported,
     *   using them does not result in an error unlike in Android 9 and 10.
     *
     * More details can be found in the
     * [official documentation](https://developer.android.com/reference/androidx/biometric/BiometricPrompt.PromptInfo.Builder#setAllowedAuthenticators(int)).
     *
     * @return The authenticators suitable for the current Android version.
     */
    fun getAuthenticators() = if (Build.VERSION.SDK_INT in 28..29) {
        BIOMETRIC_WEAK or DEVICE_CREDENTIAL
    } else {
        BIOMETRIC_STRONG or DEVICE_CREDENTIAL
    }


    /**
     * Get the epoch time from the LocalDateTime.
     *
     * @param dateTime The LocalDateTime object
     * @return The epoch time
     */
    fun getEpochTime(dateTime: LocalDateTime): Long {
        val timeZone = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            ZoneId.systemDefault()
        } else {
            TimeZone.getDefault().toZoneId()
        }
        return dateTime.atZone(timeZone).toInstant().toEpochMilli()
    }

    /**
     * Convert epoch time to LocalDate.
     *
     * @param epochTime The epoch time
     * @return The LocalDate object
     */
    fun convertEpochToLocalDate(epochTime: Long): LocalDate {
        val timeZone = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            ZoneId.systemDefault()
        } else {
            TimeZone.getDefault().toZoneId()
        }
        return LocalDateTime.ofInstant(
            java.time.Instant.ofEpochMilli(epochTime),
            timeZone
        ).toLocalDate()
    }

    /**
     * Convert LocalDate to epoch time.
     *
     * @param date The LocalDate object
     * @param endOfDay Whether to set the time to the end of the day
     * @return The epoch time
     */
    fun convertLocalDateToEpoch(date: LocalDate, endOfDay: Boolean = false): Long {
        val timeZone = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            ZoneId.systemDefault()
        } else {
            TimeZone.getDefault().toZoneId()
        }
        val dateTime = if (endOfDay) {
            date.atTime(23, 59, 59)
        } else {
            date.atStartOfDay()
        }
        return dateTime.atZone(timeZone).toInstant().toEpochMilli()
    }

    /**
     * Open the web link in the browser.
     *
     * @param context The context
     * @param url The URL to open
     */
    fun openWebLink(context: Context, url: String) {
        val uri: Uri = url.toUri()
        val intent = Intent(Intent.ACTION_VIEW, uri)
        try {
            context.startActivity(intent)
        } catch (exc: ActivityNotFoundException) {
            exc.printStackTrace()
        }
    }

    /**
     * Extract the first URL from the given text.
     *
     * @param text The input text
     * @return The first URL if found, null otherwise
     */
    fun extractFirstUrl(text: String): String? {
        return urlRegex.find(text)?.value
    }

    /**
     * Check if the device is running on MIUI.
     *
     * By default, HyperOS is excluded from the check.
     * If you want to include HyperOS in the check, set excludeHyperOS to false.
     *
     * @param excludeHyperOS Whether to exclude HyperOS
     * @return True if the device is running on MIUI, false otherwise
     */
    fun isMiui(excludeHyperOS: Boolean = true): Boolean {
        // Check if the device is manufactured by Xiaomi, Redmi, or POCO.
        val brand = Build.BRAND.lowercase()
        if (!setOf("xiaomi", "redmi", "poco").contains(brand)) return false
        // Check if the device is running on MIUI.
        val isMiui = !getProperty("ro.miui.ui.version.name").isNullOrBlank()
        val isHyperOS = !getProperty("ro.mi.os.version.name").isNullOrBlank()
        return isMiui && (!excludeHyperOS || !isHyperOS)
    }

    // Private function to get the property value from build.prop.
    private fun getProperty(property: String): String? {
        return try {
            Runtime.getRuntime().exec("getprop $property").inputStream.use { input ->
                BufferedReader(InputStreamReader(input), 1024).readLine()
            }
        } catch (e: IOException) {
            e.printStackTrace()
            null
        }
    }
}