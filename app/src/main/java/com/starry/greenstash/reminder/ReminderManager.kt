package com.starry.greenstash.reminder

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.util.Log
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.ui.ExperimentalComposeUiApi
import com.starry.greenstash.database.core.GoalWithTransactions
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
        const val TAG = "ReminderManager"
        const val INTENT_EXTRA_GOAL_ID = "reminder_goal_id"
        const val REMINDER_TIME = "09:30" // AM
    }

    private val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager

    /**
     * Schedules reminder for next day if  priority is [GoalPriority.High] or
     * sets reminder for next week if priority is [GoalPriority.Normal] and
     * sets no reminder if priority is [GoalPriority.Low].
     */
    fun scheduleReminder(goalId: Long, priority: GoalPriority) {
        val (hours, min) = REMINDER_TIME.split(":").map { it.toInt() }

        val calendarNow = Calendar.getInstance(Locale.ENGLISH)
        val calendarSet = Calendar.getInstance(Locale.ENGLISH).apply {
            set(Calendar.HOUR_OF_DAY, hours)
            set(Calendar.MINUTE, min)
            set(Calendar.SECOND, 0)
            set(Calendar.MILLISECOND, 0)
        }

        if (priority == GoalPriority.High) {
            // if the time we are setting this alarm for has been
            // passed already, we'll set alarm for the next day instead.
            if (calendarSet <= calendarNow) {
                calendarSet.add(Calendar.DATE, 1)
            }
        } else {
            calendarSet.add(Calendar.DATE, 7)
        }

        if (priority != GoalPriority.Low) {
            val reminderIntent = createReminderIntent(
                goalId = goalId,
                flags = PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
            )
            alarmManager.set(AlarmManager.RTC_WAKEUP, calendarSet.timeInMillis, reminderIntent)
            Log.d(TAG, "Scheduled reminder for goalId=$goalId at ${calendarSet.time}")
        }
    }


    /** Stops reminder for the given goal id */
    fun stopReminder(goalId: Long) {
        val reminderIntent = createReminderIntent(
            goalId = goalId,
            flags = PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )
        Log.d(TAG, "Stopping reminder for goalId=$goalId")
        alarmManager.cancel(reminderIntent)
        reminderIntent.cancel()
    }

    /** Check if reminder is et for the given goalId.*/
    fun isReminderSet(goalId: Long): Boolean {
        val reminderIntent = createReminderIntent(
            goalId = goalId,
            flags = PendingIntent.FLAG_NO_CREATE or PendingIntent.FLAG_IMMUTABLE
        )
        return reminderIntent != null
    }

    /**
     * Stops current reminder and sets new one, used when goal
     * priority has been changed.
     */
    fun reScheduleReminder(goalId: Long, priority: GoalPriority) {
        if (isReminderSet(goalId)) {
            stopReminder(goalId)
        }
        Log.d(TAG, "Rescheduling reminder for goalId=$goalId")
        scheduleReminder(goalId, priority)
    }

    /**
     * Schedules reminder for goals which have reminder enabled
     * but reminder for themis not scheduled already, by calling
     * the [scheduleReminder] function internally.
     */
    fun checkAndScheduleReminders(allGoals: List<GoalWithTransactions>) {
        Log.d(TAG, "Scheduling reminders for goals with reminder.")
        allGoals.forEach { goalItem ->
            val goal = goalItem.goal
            if (goal.reminder && !isReminderSet(goal.goalId)) {
                scheduleReminder(goal.goalId, goal.priority)
            }
        }
        Log.d(TAG, "Scheduled reminders for goals with reminder.")
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