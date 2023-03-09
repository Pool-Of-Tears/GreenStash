package com.starry.greenstash.utils

import java.math.RoundingMode
import java.text.DecimalFormat
import java.text.DecimalFormatSymbols
import java.util.*

object Utils {

    /** Validate number from text field. */
    fun getValidatedNumber(text: String): String {
        val filteredChars = text.filterIndexed { index, c ->
            c.isDigit()
                    || (c == '.' && index != 0 && text.indexOf('.') == index)
                    || (c == '.' && index != 0 && text.count { it == '.' } <= 1)
        }
        return if (filteredChars.count { it == '.' } == 1) {
            val beforeDecimal = filteredChars.substringBefore('.')
            val afterDecimal = filteredChars.substringAfter('.')
            "$beforeDecimal.$afterDecimal"
        } else {
            filteredChars
        }
    }


    /** Round decimal (double) to 2 digits */
    fun roundDecimal(number: Double): Double {
        val locale = DecimalFormatSymbols(Locale.US)
        val df = DecimalFormat("#.##", locale)
        df.roundingMode = RoundingMode.CEILING
        return df.format(number).toDouble()
    }

    /** Convert double into currency format */
    fun formatCurrency(number: Double): String {
        val df = DecimalFormat("#,###.00")
        return df.format(number)
    }
}