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


package com.starry.greenstash.reminder.receivers

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.util.Log
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.ui.ExperimentalComposeUiApi
import com.starry.greenstash.database.core.GoalWithTransactions
import com.starry.greenstash.database.goal.GoalDao
import com.starry.greenstash.database.goal.GoalPriority
import com.starry.greenstash.reminder.ReminderManager
import com.starry.greenstash.reminder.ReminderNotificationSender
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.time.DayOfWeek
import java.time.LocalDate
import java.time.LocalDateTime
import javax.inject.Inject

@ExperimentalMaterialApi
@ExperimentalFoundationApi
@ExperimentalComposeUiApi
@ExperimentalAnimationApi
@ExperimentalMaterial3Api
@AndroidEntryPoint
class AlarmReceiver : BroadcastReceiver() {

    @Inject
    lateinit var goalDao: GoalDao

    @Inject
    lateinit var reminderManager: ReminderManager

    @Inject
    lateinit var reminderNotificationSender: ReminderNotificationSender

    override fun onReceive(context: Context, intent: Intent) {
        Log.d("AlarmReceiver", "Received alarm at ${LocalDateTime.now()}")

        val coroutineScope = CoroutineScope(Dispatchers.IO)
        val localDate = LocalDate.now()

        coroutineScope.launch {
            val goalItem: GoalWithTransactions? = goalDao.getGoalWithTransactionById(
                intent.getLongExtra(ReminderManager.INTENT_EXTRA_GOAL_ID, 0L)
            )
            goalItem?.let {
                val remainingAmount = (it.goal.targetAmount - it.getCurrentlySavedAmount())
                if (remainingAmount > 0) {
                    when (goalItem.goal.priority) {
                        // High priority = daily notification.
                        GoalPriority.High -> {
                            reminderNotificationSender.sendNotification(it)
                        }
                        // Normal priority = twice a week notification.
                        GoalPriority.Normal -> {
                            if (localDate.dayOfWeek == DayOfWeek.MONDAY ||
                                localDate.dayOfWeek == DayOfWeek.FRIDAY
                            ) {
                                reminderNotificationSender.sendNotification(it)
                            }
                        }
                        // Low priority = once a week notification.
                        GoalPriority.Low -> {
                            if (localDate.dayOfWeek == DayOfWeek.SUNDAY) {
                                reminderNotificationSender.sendNotification(it)
                            }
                        }
                    }
                }
                // Reschedule reminder for next day.
                reminderManager.scheduleReminder(it.goal.goalId)
            }
        }
    }
}