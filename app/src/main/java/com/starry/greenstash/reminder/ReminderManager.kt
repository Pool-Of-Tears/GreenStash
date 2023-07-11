package com.starry.greenstash.reminder

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import com.starry.greenstash.database.goal.GoalReminder
import java.util.Calendar
import java.util.Locale

class ReminderManager(private val context: Context) {

    companion object {
        const val INTENT_EXTRA_GOAL_ID = "reminder_goal_id"
        const val REMINDER_TIME = "09:30" // AM
    }

    private val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager

    fun scheduleReminder(
        goalId: Long,
        reminderFreq: GoalReminder
    ) {
        val reminderIntent = createReminderIntent(goalId, PendingIntent.FLAG_UPDATE_CURRENT)

        val (hours, min) = REMINDER_TIME.split(":").map { it.toInt() }
        val calendar: Calendar = Calendar.getInstance(Locale.ENGLISH).apply {
            set(Calendar.HOUR_OF_DAY, hours)
            set(Calendar.MINUTE, min)
        }

        val alarmInterval = if (reminderFreq == GoalReminder.Weekly) {
            AlarmManager.INTERVAL_DAY * 7
        } else {
            AlarmManager.INTERVAL_DAY
        }

        alarmManager.setRepeating(
            AlarmManager.RTC_WAKEUP,
            calendar.timeInMillis,
            alarmInterval,
            reminderIntent
        )
    }

    fun stopReminder(
        goalId: Long
    ) {
        val reminderIntent = createReminderIntent(goalId, PendingIntent.FLAG_UPDATE_CURRENT)
        alarmManager.cancel(reminderIntent)
    }

    fun isReminderSet(goalId: Long): Boolean {
        val reminderIntent = createReminderIntent(
            goalId = goalId,
            PendingIntent.FLAG_NO_CREATE or PendingIntent.FLAG_IMMUTABLE
        )
        return reminderIntent != null
    }

    private fun createReminderIntent(goalId: Long, flags: Int) =
        Intent(context.applicationContext, AlarmReceiver::class.java)
            .apply { putExtra(INTENT_EXTRA_GOAL_ID, goalId) }
            .let { intent ->
                PendingIntent.getBroadcast(
                    context.applicationContext,
                    goalId.toInt(),
                    intent, flags
                )
            }
}