package com.starry.greenstash.utils

import android.content.Context
import com.starry.greenstash.R
import com.starry.greenstash.database.GoalWithTransactions
import com.starry.greenstash.ui.screens.settings.viewmodels.DateStyle
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.time.temporal.ChronoUnit

object GoalTextUtils {
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
        val defCurrency = PreferenceUtils.getString(PreferenceUtils.DEFAULT_CURRENCY, "")
        text += if (progressPercent < 100) {
            "\n" + context.getString(R.string.currently_saved_incomplete)
        } else {
            "\n" + context.getString(R.string.currently_saved_complete)
        }
        text = text.format(
            "$defCurrency${Utils.formatCurrency(item.getCurrentlySavedAmount())}",
            "$defCurrency${Utils.formatCurrency(item.goal.targetAmount)}"
        )
        return text
    }

    fun buildSecondaryText(context: Context, item: GoalWithTransactions): String {
        val remainingAmount = (item.goal.targetAmount - item.getCurrentlySavedAmount())
        if ((remainingAmount > 0f)) {
            if (item.goal.deadline.isNotEmpty() && item.goal.deadline.isNotBlank()) {
                // calculate remaining days between today and endDate (deadline).
                val preferredDateFormat = PreferenceUtils.getString(
                    PreferenceUtils.DATE_FORMAT, DateStyle.DateMonthYear.pattern
                )
                val dateFormatter: DateTimeFormatter = DateTimeFormatter.ofPattern(preferredDateFormat)
                val startDate = LocalDateTime.now().format(dateFormatter)

                /**
                 * If date format is set as DD/MM/YYYY but date in database is saved
                 * in YYYY/MM/DD format, then reverse the date string before parsing.
                 */
                val reverseDate: (String) -> String = {
                    item.goal.deadline.split("/").reversed().joinToString(separator = "/")
                }
                val endDate = if (item.goal.deadline.split("/")
                        .first().length == 2 && preferredDateFormat != DateStyle.DateMonthYear.pattern
                ) {
                    reverseDate(item.goal.deadline)
                } else if (item.goal.deadline.split("/")
                        .first().length == 4 && preferredDateFormat != DateStyle.YearMonthDate.pattern
                ) {
                    reverseDate(item.goal.deadline)
                } else {
                    item.goal.deadline
                }

                val startDateValue: LocalDate = LocalDate.parse(startDate, dateFormatter)
                val endDateValue: LocalDate = LocalDate.parse(endDate, dateFormatter)
                val days: Long = ChronoUnit.DAYS.between(startDateValue, endDateValue)
                val defCurrency = PreferenceUtils.getString(PreferenceUtils.DEFAULT_CURRENCY, "")
                // build description string.
                var text = context.getString(R.string.goal_days_left).format(endDate, days) + "\n"
                if (days > 2) {
                    text += context.getString(R.string.goal_approx_saving).format(
                        "$defCurrency${
                            Utils.formatCurrency(
                                Utils.roundDecimal(
                                    remainingAmount / days
                                )
                            )
                        }"
                    )
                    text += context.getString(R.string.goal_approx_saving_day)
                    if (days > 14) {
                        val weeks = days / 7
                        text = text.dropLast(1) // remove full stop
                        text += ", $defCurrency${Utils.formatCurrency(Utils.roundDecimal(remainingAmount / weeks))}/${
                            context.getString(
                                R.string.goal_approx_saving_week
                            )
                        }"
                        if (days > 60) {
                            val months = days / 30
                            text = text.dropLast(1) // remove full stop
                            text += ", $defCurrency${
                                Utils.formatCurrency(
                                    Utils.roundDecimal(
                                        remainingAmount / months
                                    )
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

}