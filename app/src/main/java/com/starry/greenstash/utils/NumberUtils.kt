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

import java.math.RoundingMode
import java.text.DecimalFormat
import java.text.DecimalFormatSymbols
import java.text.NumberFormat
import java.util.Currency
import java.util.Locale
import kotlin.math.floor
import kotlin.math.log10
import kotlin.math.pow

/**
 * A collection of utility functions for numbers.
 */
object NumberUtils {

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
     * Get currency symbol based on the currency code.
     *
     * @param currencyCode The currency code
     * @return The currency symbol
     */
    fun getCurrencySymbol(currencyCode: String): String {
        return Currency.getInstance(currencyCode).symbol
    }

    /**
     * Formats a number into a more readable format with a suffix representing its magnitude.
     * For example, 1000 becomes "1k", 1000000 becomes "1M", etc.
     *
     * @param number The number to format.
     * @return A string representation of the number with a magnitude suffix.
     */
    fun prettyCount(number: Number): String {
        val suffix = charArrayOf(' ', 'K', 'M', 'B', 'T', 'P', 'E')
        val numValue = number.toLong()
        val value = floor(log10(numValue.toDouble())).toInt()
        val base = value / 3
        return if (value >= 3 && base < suffix.size) {
            DecimalFormat("#0.0").format(
                numValue / 10.0.pow((base * 3).toDouble())
            ) + suffix[base]
        } else {
            DecimalFormat("#,##0").format(numValue)
        }
    }
}