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
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@ExperimentalMaterialApi
@ExperimentalFoundationApi
@ExperimentalComposeUiApi
@ExperimentalAnimationApi
@ExperimentalMaterial3Api
@AndroidEntryPoint
class ReminderDismissReceiver : BroadcastReceiver() {

    @Inject
    lateinit var reminderNotificationSender: ReminderNotificationSender

    companion object {
        const val REMINDER_GOAL_ID = "reminder_dismiss_goal_id"
    }

    override fun onReceive(context: Context, intent: Intent) {
        Log.d("ReminderDismissReceiver", "Received dismiss action")

        val goalId = intent.getLongExtra(REMINDER_GOAL_ID, 0L)
        reminderNotificationSender.dismissNotification(goalId)
    }
}