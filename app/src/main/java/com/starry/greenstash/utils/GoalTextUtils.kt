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
import com.starry.greenstash.ui.screens.settings.DateStyle
import com.starry.greenstash.ui.screens.settings.dateStyleToDisplayFormat
import java.time.LocalDate
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
     * @param dateStyle DateStyle
     * @return String
     */
    fun buildSecondaryText(
        context: Context,
        goalItem: GoalWithTransactions,
        currencyCode: String,
        dateStyle: DateStyle
    ): String {
        val remainingAmount = goalItem.goal.targetAmount - goalItem.getCurrentlySavedAmount()

        if (remainingAmount <= 0f || remainingAmount.isNaN()) {
            return context.getString(R.string.goal_achieved_desc)
        }

        val deadline = goalItem.goal.deadline
        if (deadline == 0L) {
            return context.getString(R.string.no_goal_deadline_set)
        }

        val calculatedDays = calcRemainingDays(deadline, dateStyle)
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
     * @param dateStyle DateStyle
     * @return String
     */
    fun getRemainingDaysText(
        context: Context,
        goalItem: GoalWithTransactions,
        dateStyle: DateStyle
    ): String {
        return if (goalItem.getCurrentlySavedAmount() >= goalItem.goal.targetAmount) {
            context.getString(R.string.info_card_goal_achieved)
        } else {
            if (goalItem.goal.deadline != 0L) {
                val calculatedDays = calcRemainingDays(goalItem.goal.deadline, dateStyle)
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
     * @param deadline Long The deadline epoch time
     * @param dateStyle DateStyle The date style for formatting
     * @return CalculatedDays The calculated remaining days and formatted end date
     */
    fun calcRemainingDays(deadline: Long, dateStyle: DateStyle): CalculatedDays {
        val endDate = Utils.convertEpochToLocalDate(deadline)
        val today = LocalDate.now()
        val remainingDays = ChronoUnit.DAYS.between(today, endDate).coerceAtLeast(0)

        val pattern = dateStyleToDisplayFormat(dateStyle)
        val formatter = DateTimeFormatter.ofPattern(pattern, java.util.Locale.ENGLISH)
        val parsedEndDate = endDate.format(formatter)

        return CalculatedDays(
            remainingDays = remainingDays,
            parsedEndDate = parsedEndDate
        )
    }

}