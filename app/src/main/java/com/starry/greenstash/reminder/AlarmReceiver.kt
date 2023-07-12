package com.starry.greenstash.reminder

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
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
        val coroutineScope = CoroutineScope(Dispatchers.IO)
        val notificationSender = ReminderNotificationSender(context)

        coroutineScope.launch {
            val goalItem: GoalWithTransactions? = goalDao.getGoalWithTransactionById(
                intent.getLongExtra(ReminderManager.INTENT_EXTRA_GOAL_ID, 0L)
            )
            goalItem?.let {
                val remainingAmount =
                    (goalItem.goal.targetAmount - goalItem.getCurrentlySavedAmount())

                if (goalItem.goal.priority != GoalPriority.Low && remainingAmount > 0) {
                    notificationSender.sendNotification(goalItem)
                }
            }
        }
    }
}