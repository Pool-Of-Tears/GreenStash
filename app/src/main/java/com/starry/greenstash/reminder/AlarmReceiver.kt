package com.starry.greenstash.reminder

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
                        // High priority = daily notification
                        GoalPriority.High -> {
                            reminderNotificationSender.sendNotification(it)
                        }
                        // High priority = twice a week notification
                        GoalPriority.Normal -> {
                            if (localDate.dayOfWeek == DayOfWeek.MONDAY ||
                                localDate.dayOfWeek == DayOfWeek.FRIDAY
                            ) {
                                reminderNotificationSender.sendNotification(it)
                            }
                        }

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