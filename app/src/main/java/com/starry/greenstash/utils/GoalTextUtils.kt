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
import com.starry.greenstash.R
import com.starry.greenstash.database.core.GoalWithTransactions
import com.starry.greenstash.database.goal.Goal
import com.starry.greenstash.ui.screens.settings.DateStyle
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.time.temporal.ChronoUnit

/**
 * Utility class to build text for goal items.
 */
object GoalTextUtils {

    data class CalculatedDays(
        val remainingDays: Long,
        val parsedEndDate: String
    )

    /**
     * Build primary text for the classic style goal item.
     *
     * @param context Context
     * @param progressPercent Int
     * @param goalItem GoalWithTransactions
     * @param currencyCode String
     * @return String
     */
    fun buildPrimaryText(
        context: Context,
        progressPercent: Int,
        goalItem: GoalWithTransactions,
        currencyCode: String,
    ): String {
        var text: String = when {
            progressPercent <= 25 -> {
                context.getString(R.string.progress_greet1)
            }

            progressPercent in 26..50 -> {
                context.getString(R.string.progress_greet2)
            }

            progressPercent in 51..75 -> {
                context.getString(R.string.progress_greet3)
            }

            progressPercent in 76..99 -> {
                context.getString(R.string.progress_greet4)
            }

            else -> {
                context.getString(R.string.progress_greet5)
            }
        }
        text += if (progressPercent < 100) {
            "\n" + context.getString(R.string.currently_saved_incomplete)
        } else {
            "\n" + context.getString(R.string.currently_saved_complete)
        }
        text = text.format(
            NumberUtils.formatCurrency(
                goalItem.getCurrentlySavedAmount(),
                currencyCode = currencyCode
            ),
            NumberUtils.formatCurrency(goalItem.goal.targetAmount, currencyCode = currencyCode)
        )
        return text
    }

    /**
     * Build secondary text for the classic style goal item.
     *
     * @param context Context
     * @param goalItem GoalWithTransactions
     * @param currencyCode String
     * @param datePattern String
     * @return String
     */
    fun buildSecondaryText(
        context: Context,
        goalItem: GoalWithTransactions,
        currencyCode: String,
        datePattern: String
    ): String {
        val remainingAmount = goalItem.goal.targetAmount - goalItem.getCurrentlySavedAmount()

        if (remainingAmount <= 0f || remainingAmount.isNaN()) {
            return context.getString(R.string.goal_achieved_desc)
        }

        val deadline = goalItem.goal.deadline
        if (deadline.isBlank()) {
            return context.getString(R.string.no_goal_deadline_set)
        }

        val calculatedDays = calcRemainingDays(goalItem.goal, datePattern)
        val remainingDays = calculatedDays.remainingDays

        val builder = StringBuilder()

        builder.append(
            context.getString(R.string.goal_days_left)
                .format(calculatedDays.parsedEndDate, remainingDays)
        ).append('\n')

        if (remainingDays > 2) {
            // Per-day saving
            val perDay = NumberUtils.formatCurrency(
                NumberUtils.roundDecimal(remainingAmount / remainingDays),
                currencyCode = currencyCode
            )

            builder.append(
                context.getString(R.string.goal_approx_saving).format(perDay)
            )
            builder.append(context.getString(R.string.goal_approx_saving_day))

            if (remainingDays > 14) {
                // Per-week saving
                val weeks = remainingDays / 7
                val perWeek = NumberUtils.formatCurrency(
                    NumberUtils.roundDecimal(remainingAmount / weeks),
                    currencyCode = currencyCode
                )

                builder.append(", ")
                    .append(perWeek)
                    .append("/")
                    .append(context.getString(R.string.goal_approx_saving_week))

                if (remainingDays > 60) {
                    // Per-month saving
                    val months = remainingDays / 30
                    val perMonth = NumberUtils.formatCurrency(
                        NumberUtils.roundDecimal(remainingAmount / months),
                        currencyCode = currencyCode
                    )

                    builder.append(", ")
                        .append(perMonth)
                        .append("/")
                        .append(context.getString(R.string.goal_approx_saving_month))
                }
            }
        }

        return builder.toString()
    }

    /**
     * Get the remaining days text for the goal item.
     *
     * @param context Context
     * @param goalItem GoalWithTransactions
     * @param datePattern String
     * @return String
     */
    fun getRemainingDaysText(
        context: Context,
        goalItem: GoalWithTransactions,
        datePattern: String
    ): String {
        return if (goalItem.getCurrentlySavedAmount() >= goalItem.goal.targetAmount) {
            context.getString(R.string.info_card_goal_achieved)
        } else {
            if (goalItem.goal.deadline.isNotEmpty() && goalItem.goal.deadline.isNotBlank()) {
                val calculatedDays = calcRemainingDays(goalItem.goal, datePattern)
                context.getString(R.string.info_card_remaining_days)
                    .format(calculatedDays.remainingDays)
            } else {
                context.getString(R.string.info_card_no_deadline_set)
            }
        }
    }


    /**
     * Calculate the remaining days between today and the goal's deadline.
     *
     * @param goal Goal
     * @param datePattern String
     * @return CalculatedDays
     */
    fun calcRemainingDays(goal: Goal, datePattern: String): CalculatedDays {
        // calculate remaining days between today and endDate (deadline).
        val dateFormatter: DateTimeFormatter =
            DateTimeFormatter.ofPattern(datePattern)
        val startDate = LocalDateTime.now().format(dateFormatter)

        /**
         * If date format is set as DD/MM/YYYY but date in database is saved
         * in YYYY/MM/DD format, then reverse the date string before parsing.
         */
        val reverseDate: (String) -> String = {
            goal.deadline.split("/").reversed().joinToString(separator = "/")
        }

        val endDate = when (goal.deadline.split("/").first().length) {
            2 if datePattern != DateStyle.DateMonthYear.pattern ->
                reverseDate(goal.deadline)

            4 if datePattern != DateStyle.YearMonthDate.pattern ->
                reverseDate(goal.deadline)

            else -> goal.deadline
        }

        val startDateValue: LocalDate = LocalDate.parse(startDate, dateFormatter)
        val endDateValue: LocalDate = LocalDate.parse(endDate, dateFormatter)
        val days = ChronoUnit.DAYS.between(startDateValue, endDateValue)
        return CalculatedDays(remainingDays = days, parsedEndDate = endDate)
    }

}