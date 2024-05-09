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


package com.starry.greenstash.ui.screens.archive

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.starry.greenstash.database.goal.Goal
import com.starry.greenstash.database.goal.GoalDao
import com.starry.greenstash.reminder.ReminderManager
import com.starry.greenstash.utils.PreferenceUtil
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ArchiveViewModel @Inject constructor(
    private val goalDao: GoalDao,
    private val reminderManager: ReminderManager,
    private val preferenceUtil: PreferenceUtil
) : ViewModel() {

    val archivedGoals = goalDao.getAllArchivedGoals()

    fun deleteGoal(goal: Goal) {
        viewModelScope.launch(Dispatchers.IO) {
            goalDao.deleteGoal(goal.goalId)
            // Stop the reminder if it is set for the goal
            if (reminderManager.isReminderSet(goal.goalId)) {
                reminderManager.stopReminder(goal.goalId)
            }
        }
    }

    fun restoreGoal(goal: Goal) {
        viewModelScope.launch(Dispatchers.IO) {
            val updatedGoal = goal.copy(archived = false)
            updatedGoal.goalId = goal.goalId
            goalDao.updateGoal(updatedGoal)
            // Schedule the reminder if it was enabled for the goal.
            if (goal.reminder) {
                reminderManager.scheduleReminder(goal.goalId)
            }
        }
    }

    fun getDefaultCurrency(): String {
        return preferenceUtil.getString(PreferenceUtil.DEFAULT_CURRENCY_STR, "")!!
    }
}