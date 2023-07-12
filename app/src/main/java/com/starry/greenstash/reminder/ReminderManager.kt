package com.starry.greenstash.reminder

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.ui.ExperimentalComposeUiApi
import com.starry.greenstash.database.goal.GoalPriority
import java.util.Calendar
import java.util.Locale

@ExperimentalMaterial3Api
@ExperimentalAnimationApi
@ExperimentalComposeUiApi
@ExperimentalFoundationApi
@ExperimentalMaterialApi
class ReminderManager(private val context: Context) {

    companion object {
        const val INTENT_EXTRA_GOAL_ID = "reminder_goal_id"
        const val REMINDER_TIME = "09:30" // AM
    }

    private val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager

    /**
     * Schedule daily alarm if goal priority is [GoalPriority.High] or sets
     * weekly alarm if priority is [GoalPriority.Normal] and sets no alarm
     * if priority is [GoalPriority.Low].
     */
    fun scheduleReminder(goalId: Long, priority: GoalPriority) {
        val (hours, min) = REMINDER_TIME.split(":").map { it.toInt() }
        val calendar: Calendar = Calendar.getInstance(Locale.ENGLISH).apply {
            set(Calendar.HOUR_OF_DAY, hours)
            set(Calendar.MINUTE, min)
        }

        val alarmInterval = if (priority == GoalPriority.High) {
            AlarmManager.INTERVAL_DAY
        } else {
            AlarmManager.INTERVAL_DAY * 7
        }

        if (priority != GoalPriority.Low) {
            val reminderIntent = createReminderIntent(
                goalId = goalId,
                flags = PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
            )
            alarmManager.setRepeating(
                AlarmManager.RTC_WAKEUP,
                calendar.timeInMillis,
                alarmInterval,
                reminderIntent
            )
        }
    }

    fun stopReminder(goalId: Long) {
        val reminderIntent = createReminderIntent(
            goalId = goalId,
            flags = PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )
        alarmManager.cancel(reminderIntent)
    }

    fun isReminderSet(goalId: Long): Boolean {
        val reminderIntent = createReminderIntent(
            goalId = goalId,
            flags = PendingIntent.FLAG_NO_CREATE or PendingIntent.FLAG_IMMUTABLE
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