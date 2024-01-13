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
import com.starry.greenstash.ui.screens.settings.viewmodels.DateStyle
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.time.temporal.ChronoUnit

class GoalTextUtils(private val preferenceUtil: PreferenceUtil) {

    data class CalculatedDays(
        val remainingDays: Long,
        val parsedEndDate: String
    )

    fun buildPrimaryText(
        context: Context, progressPercent: Int, item: GoalWithTransactions
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
        val defCurrency = preferenceUtil.getString(PreferenceUtil.DEFAULT_CURRENCY_STR, "")
        text += if (progressPercent < 100) {
            "\n" + context.getString(R.string.currently_saved_incomplete)
        } else {
            "\n" + context.getString(R.string.currently_saved_complete)
        }
        text = text.format(
            Utils.formatCurrency(item.getCurrentlySavedAmount(), defCurrency!!),
            Utils.formatCurrency(item.goal.targetAmount, defCurrency)
        )
        return text
    }

    fun buildSecondaryText(context: Context, item: GoalWithTransactions): String {
        val remainingAmount = (item.goal.targetAmount - item.getCurrentlySavedAmount())
        if ((remainingAmount > 0f)) {
            if (item.goal.deadline.isNotEmpty() && item.goal.deadline.isNotBlank()) {
                val calculatedDays = calcRemainingDays(item.goal)
                val defCurrency =
                    preferenceUtil.getString(PreferenceUtil.DEFAULT_CURRENCY_STR, "")!!
                // build description string.
                var text = context.getString(R.string.goal_days_left)
                    .format(calculatedDays.parsedEndDate, calculatedDays.remainingDays) + "\n"
                if (calculatedDays.remainingDays > 2) {
                    text += context.getString(R.string.goal_approx_saving).format(
                        Utils.formatCurrency(
                            Utils.roundDecimal(
                                remainingAmount / calculatedDays.remainingDays
                            ), defCurrency
                        )
                    )
                    text += context.getString(R.string.goal_approx_saving_day)
                    if (calculatedDays.remainingDays > 14) {
                        val weeks = calculatedDays.remainingDays / 7
                        text = text.dropLast(1) // remove full stop
                        text += ", ${
                            Utils.formatCurrency(
                                Utils.roundDecimal(
                                    remainingAmount / weeks
                                ), defCurrency
                            )
                        }/${
                            context.getString(
                                R.string.goal_approx_saving_week
                            )
                        }"
                        if (calculatedDays.remainingDays > 60) {
                            val months = calculatedDays.remainingDays / 30
                            text = text.dropLast(1) // remove full stop
                            text += ", ${
                                Utils.formatCurrency(
                                    Utils.roundDecimal(
                                        remainingAmount / months
                                    ), defCurrency
                                )
                            }/${
                                context.getString(
                                    R.string.goal_approx_saving_month
                                )
                            }"
                        }
                    }
                }
                return text
            } else {
                return context.getString(R.string.no_goal_deadline_set)
            }
        } else {
            return context.getString(R.string.goal_achieved_desc)
        }

    }

    fun getRemainingDaysText(context: Context, goalItem: GoalWithTransactions): String {
        return if (goalItem.getCurrentlySavedAmount() >= goalItem.goal.targetAmount) {
            context.getString(R.string.info_card_goal_achieved)
        } else {
            if (goalItem.goal.deadline.isNotEmpty() && goalItem.goal.deadline.isNotBlank()) {
                val calculatedDays = calcRemainingDays(goalItem.goal)
                context.getString(R.string.info_card_remaining_days)
                    .format(calculatedDays.remainingDays)
            } else {
                context.getString(R.string.info_card_no_deadline_set)
            }
        }
    }


    fun calcRemainingDays(goal: Goal): CalculatedDays {
        // calculate remaining days between today and endDate (deadline).
        val preferredDateFormat = preferenceUtil.getString(
            PreferenceUtil.DATE_FORMAT_STR, DateStyle.DateMonthYear.pattern
        )
        val dateFormatter: DateTimeFormatter =
            DateTimeFormatter.ofPattern(preferredDateFormat)
        val startDate = LocalDateTime.now().format(dateFormatter)

        /**
         * If date format is set as DD/MM/YYYY but date in database is saved
         * in YYYY/MM/DD format, then reverse the date string before parsing.
         */
        val reverseDate: (String) -> String = {
            goal.deadline.split("/").reversed().joinToString(separator = "/")
        }

        val endDate = when {
            goal.deadline.split("/").first().length == 2 &&
                    preferredDateFormat != DateStyle.DateMonthYear.pattern ->
                reverseDate(goal.deadline)

            goal.deadline.split("/").first().length == 4 &&
                    preferredDateFormat != DateStyle.YearMonthDate.pattern ->
                reverseDate(goal.deadline)

            else -> goal.deadline
        }

        val startDateValue: LocalDate = LocalDate.parse(startDate, dateFormatter)
        val endDateValue: LocalDate = LocalDate.parse(endDate, dateFormatter)
        val days = ChronoUnit.DAYS.between(startDateValue, endDateValue)
        return CalculatedDays(remainingDays = days, parsedEndDate = endDate)
    }

}