package com.starry.greenstash.reminder

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import com.starry.greenstash.database.goal.GoalDao
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class AlarmReceiver : BroadcastReceiver() {

    @Inject
    lateinit var goalDao: GoalDao

    override fun onReceive(context: Context, intent: Intent?) {

    }
}