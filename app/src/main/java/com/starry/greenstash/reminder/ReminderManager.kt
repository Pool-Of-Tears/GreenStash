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
import com.starry.greenstash.reminder.receivers.AlarmReceiver
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
        private const val INTENT_UNIQUE_CODE = 2456
    }

    private val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager

    /** Schedule a reminder for given goal id.*/
    fun scheduleReminder(goalId: Long) {
        val (hours, min) = REMINDER_TIME.split(":").map { it.toInt() }
        val calendarNow = Calendar.getInstance(Locale.ENGLISH)
        val calendarSet = Calendar.getInstance(Locale.ENGLISH).apply {
            set(Calendar.HOUR_OF_DAY, hours)
            set(Calendar.MINUTE, min)
            set(Calendar.SECOND, 0)
            set(Calendar.MILLISECOND, 0)
        }
        // if the time we are setting this alarm for has been
        // passed already, we'll set alarm for the next day instead.
        if (calendarSet <= calendarNow) {
            calendarSet.add(Calendar.DATE, 1)
        }
        val reminderIntent = createReminderIntent(
            goalId = goalId,
            flags = PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )
        alarmManager.set(
            AlarmManager.RTC_WAKEUP,
            calendarSet.timeInMillis,
            reminderIntent
        )
        Log.d(TAG, "Scheduled reminder for goalId=$goalId at ${calendarSet.time}")
    }


    /** Stops reminder for the given goal id */
    fun stopReminder(goalId: Long) {
        if (isReminderSet(goalId)) {
            val reminderIntent = createReminderIntent(
                goalId = goalId,
                flags = PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
            )
            Log.d(TAG, "Stopping reminder for goalId=$goalId")
            alarmManager.cancel(reminderIntent)
            reminderIntent.cancel()
        } else {
            Log.d(TAG, "Failed to stop reminder for goalId=$goalId, reminder is not set")
        }
    }

    /** Check if reminder is et for the given goalId.*/
    fun isReminderSet(goalId: Long): Boolean {
        val reminderIntent = createReminderIntent(
            goalId = goalId, flags = PendingIntent.FLAG_NO_CREATE or PendingIntent.FLAG_IMMUTABLE
        )
        return reminderIntent != null
    }

    /**
     * Schedules reminder for goals which have reminder enabled
     * but reminder for them is not scheduled already, by calling
     * the [scheduleReminder] function internally.
     */
    fun checkAndScheduleReminders(allGoals: List<GoalWithTransactions>) {
        Log.d(TAG, "Scheduling reminders for goals with reminder.")
        allGoals.forEach { goalItem ->
            val goal = goalItem.goal
            if (goal.reminder && !isReminderSet(goal.goalId)) {
                scheduleReminder(goal.goalId)
            }
        }
        Log.d(TAG, "Scheduled reminders for goals with reminder.")
    }

    private fun createReminderIntent(goalId: Long, flags: Int) =
        Intent(context.applicationContext, AlarmReceiver::class.java).apply {
            putExtra(INTENT_EXTRA_GOAL_ID, goalId)
        }.let { intent ->
            PendingIntent.getBroadcast(
                context, goalId.toInt() + INTENT_UNIQUE_CODE,
                intent, flags
            )
        }
}