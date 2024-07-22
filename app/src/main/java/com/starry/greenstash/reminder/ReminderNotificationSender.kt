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


package com.starry.greenstash.reminder

import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import androidx.core.app.NotificationCompat
import com.starry.greenstash.MainActivity
import com.starry.greenstash.R
import com.starry.greenstash.database.core.GoalWithTransactions
import com.starry.greenstash.database.goal.GoalPriority
import com.starry.greenstash.reminder.receivers.ReminderDepositReceiver
import com.starry.greenstash.reminder.receivers.ReminderDismissReceiver
import com.starry.greenstash.utils.GoalTextUtils
import com.starry.greenstash.utils.NumberUtils
import com.starry.greenstash.utils.PreferenceUtil


/**
 * Handles the sending of notifications for goal reminders.
 * @param context The context of the application.
 * @param preferenceUtil The preference utility to access the user preferences.
 */
class ReminderNotificationSender(
    private val context: Context,
    private val preferenceUtil: PreferenceUtil
) {
    companion object {
        const val REMINDER_CHANNEL_ID = "reminder_notification_channel"
        const val REMINDER_CHANNEL_NAME = "Goal Reminders"
        private const val INTENT_UNIQUE_CODE = 7546
    }

    private val notificationManager =
        context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

    /**
     * Sends a notification to the user for the goal reminder.
     * The notification contains the title of the goal, a description, and two actions:
     * 1. Deposit: To deposit the calculated amount for the goal.
     * 2. Dismiss: To dismiss the notification.
     * @param goalItem The goal with transactions for which the notification is to be sent.
     */
    fun sendNotification(goalItem: GoalWithTransactions) {
        val goal = goalItem.goal

        val titlePrefix = when (goal.priority) {
            GoalPriority.High -> "Daily"
            GoalPriority.Normal -> "SemiWeekly"
            GoalPriority.Low -> "Weekly"
        }

        val notification = NotificationCompat.Builder(context, REMINDER_CHANNEL_ID)
            .setSmallIcon(R.drawable.ic_reminder_notification)
            .setContentTitle("$titlePrefix reminder for ${goal.title}")
            .setContentText(context.getString(R.string.reminder_notification_desc))
            .setStyle(NotificationCompat.BigTextStyle())
            .setContentIntent(createActivityIntent())

        val remainingAmount = (goal.targetAmount - goalItem.getCurrentlySavedAmount())
        val defCurrency = preferenceUtil.getString(PreferenceUtil.DEFAULT_CURRENCY_STR, "")!!
        val datePattern = preferenceUtil.getString(PreferenceUtil.DATE_FORMAT_STR, "")!!

        if (goal.deadline.isNotEmpty() && goal.deadline.isNotBlank()) {
            val calculatedDays = GoalTextUtils.calcRemainingDays(goal, datePattern)
            when (goal.priority) {
                GoalPriority.High -> {
                    val amountDay = remainingAmount / calculatedDays.remainingDays
                    notification.addAction(
                        R.drawable.ic_notification_deposit,
                        "${context.getString(R.string.deposit_button)} ${
                            NumberUtils.formatCurrency(
                                amount = NumberUtils.roundDecimal(amountDay),
                                currencyCode = defCurrency
                            )
                        }",
                        createDepositIntent(goal.goalId, amountDay)
                    )
                }

                GoalPriority.Normal -> {
                    val amountSemiWeek = remainingAmount / (calculatedDays.remainingDays / 4)
                    notification.addAction(
                        R.drawable.ic_notification_deposit,
                        "${context.getString(R.string.deposit_button)} ${
                            NumberUtils.formatCurrency(
                                amount = NumberUtils.roundDecimal(amountSemiWeek),
                                currencyCode = defCurrency
                            )
                        }",
                        createDepositIntent(goal.goalId, amountSemiWeek)
                    )
                }

                GoalPriority.Low -> {
                    val amountWeek = remainingAmount / (calculatedDays.remainingDays / 7)
                    notification.addAction(
                        R.drawable.ic_notification_deposit,
                        "${context.getString(R.string.deposit_button)} ${
                            NumberUtils.formatCurrency(
                                amount = NumberUtils.roundDecimal(amountWeek),
                                currencyCode = defCurrency
                            )
                        }",
                        createDepositIntent(goal.goalId, amountWeek)
                    )
                }
            }
        }

        notification.addAction(
            R.drawable.ic_notification_dismiss,
            context.getString(R.string.dismiss_notification_button),
            createDismissIntent(goal.goalId)
        )
        notificationManager.notify(goal.goalId.toInt(), notification.build())
    }

    /**
     * Updates the notification with the deposited message.
     * @param goalId The goal id for which the notification is to be updated.
     * @param amount The amount deposited.
     */
    fun updateWithDepositNotification(goalId: Long, amount: Double) {
        val defCurrency = preferenceUtil.getString(PreferenceUtil.DEFAULT_CURRENCY_STR, "")
        val notification = NotificationCompat.Builder(context, REMINDER_CHANNEL_ID)
            .setSmallIcon(R.drawable.ic_reminder_notification)
            .setContentTitle(context.getString(R.string.notification_deposited_title))
            .setContentText(
                context.getString(R.string.notification_deposited_desc)
                    .format(
                        NumberUtils.formatCurrency(
                            NumberUtils.roundDecimal(amount),
                            defCurrency!!
                        )
                    )
            )
            .setStyle(NotificationCompat.BigTextStyle())
            .setContentIntent(createActivityIntent())
            .addAction(
                R.drawable.ic_notification_dismiss,
                context.getString(R.string.dismiss_notification_button),
                createDismissIntent(goalId)
            )
        notificationManager.notify(goalId.toInt(), notification.build())
    }

    /**
     * Dismisses the notification for the goal.
     * @param goalId The goal id for which the notification is to be dismissed.
     */
    fun dismissNotification(goalId: Long) = notificationManager.cancel(goalId.toInt())

    // Creates a pending intent for the deposit action.
    private fun createDepositIntent(goalId: Long, amount: Double) =
        Intent(context, ReminderDepositReceiver::class.java).apply {
            putExtra(ReminderDepositReceiver.REMINDER_GOAL_ID, goalId)
            putExtra(ReminderDepositReceiver.REMINDER_DEPOSIT_AMOUNT, amount)
        }.let { intent ->
            PendingIntent.getBroadcast(
                context, goalId.toInt() + INTENT_UNIQUE_CODE,
                intent, PendingIntent.FLAG_IMMUTABLE
            )
        }

    // Creates a pending intent for the dismiss action.
    private fun createDismissIntent(goalId: Long) =
        Intent(context, ReminderDismissReceiver::class.java).apply {
            putExtra(ReminderDismissReceiver.REMINDER_GOAL_ID, goalId)
        }.let { intent ->
            PendingIntent.getBroadcast(
                context, goalId.toInt() + INTENT_UNIQUE_CODE,
                intent, PendingIntent.FLAG_IMMUTABLE
            )
        }

    // Creates a pending intent to open the main activity when the notification is clicked.
    private fun createActivityIntent() = Intent(context, MainActivity::class.java).let { intent ->
        PendingIntent.getActivity(context, 0, intent, PendingIntent.FLAG_IMMUTABLE)
    }
}