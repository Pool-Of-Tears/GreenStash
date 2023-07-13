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
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Inject

@ExperimentalMaterial3Api
@ExperimentalAnimationApi
@ExperimentalComposeUiApi
@ExperimentalFoundationApi
@ExperimentalMaterialApi
@AndroidEntryPoint
class DateTimeChangeReceiver : BroadcastReceiver() {

    @Inject
    lateinit var goalDao: GoalDao

    override fun onReceive(context: Context, intent: Intent?) {
        if (intent?.action == Intent.ACTION_TIME_CHANGED
            || intent?.action == Intent.ACTION_TIMEZONE_CHANGED
            || intent?.action == Intent.ACTION_DATE_CHANGED
        ) {
            val coroutineScope = CoroutineScope(Dispatchers.IO)
            coroutineScope.launch {
                val allGoals = goalDao.getAllGoals()
                val reminderManager = ReminderManager(context)
                reminderManager.checkAndScheduleReminders(allGoals)
            }
        }
    }
}