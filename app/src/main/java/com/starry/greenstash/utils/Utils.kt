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
import java.io.BufferedReader
import java.io.IOException
import java.io.InputStreamReader
import java.math.RoundingMode
import java.text.DecimalFormat
import java.text.DecimalFormatSymbols
import java.text.NumberFormat
import java.time.LocalDateTime
import java.time.ZoneId
import java.util.Currency
import java.util.Locale
import java.util.TimeZone


/**
 * A collection of utility functions.
 */
object Utils {

    /**
     * Get validated number from the text.
     *
     * @param text The text to validate
     * @return The validated number
     */
    fun getValidatedNumber(text: String): String {
        val filteredChars = text.filterIndexed { index, c ->
            c.isDigit() || (c == '.' && index != 0
                    && text.indexOf('.') == index)
                    || (c == '.' && index != 0
                    && text.count { it == '.' } <= 1)
        }
        return if (filteredChars.count { it == '.' } == 1) {
            val beforeDecimal = filteredChars.substringBefore('.')
            val afterDecimal = filteredChars.substringAfter('.')
            "$beforeDecimal.$afterDecimal"
        } else {
            filteredChars
        }
    }

    /**
     * Round the decimal number to two decimal places.
     *
     * @param number The number to round
     * @return The rounded number
     */
    fun roundDecimal(number: Double): Double {
        val locale = DecimalFormatSymbols(Locale.US)
        val df = DecimalFormat("#.##", locale)
        df.roundingMode = RoundingMode.CEILING
        return df.format(number).toDouble()
    }

    /**
     * Format currency based on the currency code.
     *
     * @param amount The amount to format
     * @param currencyCode The currency code
     * @return The formatted currency
     */
    fun formatCurrency(amount: Double, currencyCode: String): String {
        val nf = NumberFormat.getCurrencyInstance().apply {
            currency = Currency.getInstance(currencyCode)
            maximumFractionDigits = if (currencyCode in setOf(
                    "JPY", "DJF", "GNF", "IDR", "KMF", "KRW", "LAK",
                    "PYG", "RWF", "VND", "VUV", "XAF", "XOF", "XPF"
                )
            ) 0 else 2
        }
        return nf.format(amount)
    }

    /**
     * Get the authenticators based on the Android version.
     *
     * For Android 9 and 10, the authenticators are BIOMETRIC_WEAK and DEVICE_CREDENTIAL.
     *
     * For Android 11 and above, the authenticators are BIOMETRIC_STRONG and DEVICE_CREDENTIAL.
     *
     * For Android versions below 9, the authenticators are BIOMETRIC_STRONG and DEVICE_CREDENTIAL although they are not supported,
     * they don't result in any error unlike in Android 9 and 10.
     *
     * See https://developer.android.com/reference/androidx/biometric/BiometricPrompt.PromptInfo.Builder#setAllowedAuthenticators(int)
     * for more information.
     *
     * @return The authenticators based on the Android version.
     */
    fun getAuthenticators() = if (Build.VERSION.SDK_INT == 28 || Build.VERSION.SDK_INT == 29) {
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
     * Open the web link in the browser.
     *
     * @param context The context
     * @param url The URL to open
     */
    fun openWebLink(context: Context, url: String) {
        val uri: Uri = Uri.parse(url)
        val intent = Intent(Intent.ACTION_VIEW, uri)
        try {
            context.startActivity(intent)
        } catch (exc: ActivityNotFoundException) {
            exc.printStackTrace()
        }
    }

    /**
     * heck if the device is running on MIUI.
     *
     * By default, HyperOS is excluded from the check.
     * If you want to include HyperOS in the check, set excludeHyperOS to false.
     *
     * @param excludeHyperOS Whether to exclude HyperOS
     * @return True if the device is running on MIUI, false otherwise
     */
    fun isMiui(excludeHyperOS: Boolean = true): Boolean {
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