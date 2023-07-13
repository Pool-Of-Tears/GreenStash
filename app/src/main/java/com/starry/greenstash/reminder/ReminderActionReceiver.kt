package com.starry.greenstash.reminder

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent

class ReminderActionReceiver : BroadcastReceiver() {

    companion object {
        const val REMINDER_DEPOSIT_GOAL_ID = "deposit_goal_id"
        const val REMINDER_DEPOSIT_AMOUNT = "deposit_amount"
    }

    override fun onReceive(context: Context, intent: Intent) {
        // Todo
    }
}