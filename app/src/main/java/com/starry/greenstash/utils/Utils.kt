package com.starry.greenstash.utils

object Utils {

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
}