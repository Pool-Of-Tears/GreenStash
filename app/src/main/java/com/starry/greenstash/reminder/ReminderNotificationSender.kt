package com.starry.greenstash.reminder

import android.Manifest
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.core.app.NotificationCompat
import androidx.core.content.ContextCompat
import com.starry.greenstash.MainActivity
import com.starry.greenstash.R
import com.starry.greenstash.database.core.GoalWithTransactions
import com.starry.greenstash.database.goal.GoalReminder
import com.starry.greenstash.utils.GoalTextUtils
import com.starry.greenstash.utils.PreferenceUtils
import com.starry.greenstash.utils.Utils

@ExperimentalMaterial3Api
@ExperimentalAnimationApi
@ExperimentalComposeUiApi
@ExperimentalFoundationApi
@ExperimentalMaterialApi
class ReminderNotificationSender(private val context: Context) {

    companion object {
        const val REMINDER_CHANNEL_ID = "reminder_notification_channel"
        const val REMINDER_CHANNEL_NAME = "Goal Reminders"
    }

    init {
        PreferenceUtils.initialize(context)
    }

    private val notificationManager =
        context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

    fun sendNotification(goalItem: GoalWithTransactions) {
        val goal = goalItem.goal

        val activityPendingIntent = Intent(context, MainActivity::class.java).let { intent ->
            PendingIntent.getActivity(context, 0, intent, PendingIntent.FLAG_IMMUTABLE)
        }
        val depositPendingIntent =
            Intent(context, ReminderDepositReceiver::class.java).let { intent ->
                PendingIntent.getBroadcast(
                    context, ReminderDepositReceiver.REMINDER_DEPOSIT_INTENT_ID,
                    intent, PendingIntent.FLAG_IMMUTABLE
                )
            }

        val notification = NotificationCompat.Builder(context, REMINDER_CHANNEL_ID)
            .setSmallIcon(R.drawable.ic_reminder_notification)
            .setContentTitle("${goal.reminder.name} reminder for ${goal.title}")
            .setContentText(context.getString(R.string.reminder_notification_desc))
            .setStyle(NotificationCompat.BigTextStyle())
            .setContentIntent(activityPendingIntent)

        val remainingAmount = (goal.targetAmount - goalItem.getCurrentlySavedAmount())
        val defCurrency = PreferenceUtils.getString(PreferenceUtils.DEFAULT_CURRENCY, "")

        if (goal.deadline.isNotEmpty() && goal.deadline.isNotBlank()) {
            val calculatedDays = GoalTextUtils.calcRemainingDays(goal)
            if (goal.reminder == GoalReminder.Daily) {
                val amountDay = remainingAmount / calculatedDays.remainingDays
                notification.addAction(
                    R.drawable.ic_notification_deposit,
                    "${context.getString(R.string.deposit_button).uppercase()} $defCurrency${
                        Utils.formatCurrency(Utils.roundDecimal(amountDay))
                    }",
                    depositPendingIntent
                )
            } else {
                val amountWeek = remainingAmount / (calculatedDays.remainingDays / 7)
                notification.addAction(
                    R.drawable.ic_notification_deposit,
                    "${context.getString(R.string.deposit_button).uppercase()} $defCurrency${
                        Utils.formatCurrency(Utils.roundDecimal(amountWeek))
                    }",
                    depositPendingIntent
                )
            }
        }
        notificationManager.notify(1, notification.build())
    }

    fun hasNotificationPermission() = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
        ContextCompat.checkSelfPermission(
            context, Manifest.permission.POST_NOTIFICATIONS
        ) == PackageManager.PERMISSION_GRANTED
    } else {
        true
    }
}