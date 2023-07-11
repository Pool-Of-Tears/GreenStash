package com.starry.greenstash.reminder

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.ui.ExperimentalComposeUiApi
import com.starry.greenstash.database.goal.GoalDao
import com.starry.greenstash.database.goal.GoalReminder
import dagger.hilt.android.AndroidEntryPoint
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

    override fun onReceive(context: Context, intent: Intent) {
        val notificationSender = ReminderNotificationSender(context)
        val reminderManager = ReminderManager(context)

        val goalItem = goalDao.getGoalWithTransactionById(
            intent.getLongExtra(ReminderManager.INTENT_EXTRA_GOAL_ID, 0L)
        )
        val remainingAmount = (goalItem.goal.targetAmount - goalItem.getCurrentlySavedAmount())

        if (goalItem.goal.reminder != GoalReminder.None && remainingAmount > 0) {
            notificationSender.sendNotification(goalItem)
        } else if (goalItem.goal.reminder == GoalReminder.None
            && reminderManager.isReminderSet(goalItem.goal.goalId)
        ) {
            reminderManager.stopReminder(goalItem.goal.goalId)
        }

    }
}