package com.starry.greenstash.reminder

import android.content.Context

class ReminderManager(private val context: Context) {

    enum class ReminderFrequency { Daily, Weekly }

    companion object {
        const val REMINDER_NOTIFICATION_REQUEST_CODE = 123
    }

    fun scheduleReminder(
        goalId: Long,
        reminderTime: String,
        reminderFrequency: ReminderFrequency
    ) {
        TODO("Not yet implemented")
    }

    fun stopReminder(
        reminderId: Int = REMINDER_NOTIFICATION_REQUEST_CODE
    ) {
        TODO("Not yet implemented")
    }
}