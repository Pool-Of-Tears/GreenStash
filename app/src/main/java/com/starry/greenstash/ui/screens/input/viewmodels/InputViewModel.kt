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


package com.starry.greenstash.ui.screens.input.viewmodels

import android.content.Context
import android.graphics.Bitmap
import android.net.Uri
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.starry.greenstash.database.goal.Goal
import com.starry.greenstash.database.goal.GoalDao
import com.starry.greenstash.database.goal.GoalPriority
import com.starry.greenstash.database.goal.GoalReminder
import com.starry.greenstash.utils.ImageUtils
import com.starry.greenstash.utils.Utils
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject

data class InputScreenState(
    val goalImageUri: Uri? = null,
    val goalTitleText: String = "",
    val targetAmount: String = "",
    val deadline: String = "",
    val additionalNotes: String = "",
    val goalPriority: String = GoalPriority.Normal.name
)

@HiltViewModel
class InputViewModel @Inject constructor(private val goalDao: GoalDao) : ViewModel() {

    var state by mutableStateOf(InputScreenState())
    fun addSavingGoal(context: Context) {
        viewModelScope.launch(Dispatchers.IO) {
            val goal = Goal(
                title = state.goalTitleText,
                targetAmount = Utils.roundDecimal(state.targetAmount.toDouble()),
                deadline = state.deadline,
                goalImage = if (state.goalImageUri != null) ImageUtils.uriToBitmap(
                    uri = state.goalImageUri!!, context = context, maxSize = 1024
                ) else null,
                additionalNotes = state.additionalNotes,
                priority = GoalPriority.values().find { it.name == state.goalPriority }!!,

                //TODO: Temporary, for testing
                reminder = GoalReminder.None
            )
            // Add goal into database.
            goalDao.insertGoal(goal)
        }
    }

    fun setEditGoalData(goalId: Long, onEditDataSet: (Bitmap?) -> Unit) {
        viewModelScope.launch(Dispatchers.IO) {
            val goal = goalDao.getGoalById(goalId)
            withContext(Dispatchers.Main) {
                state = state.copy(
                    goalTitleText = goal.title,
                    targetAmount = goal.targetAmount.toString(),
                    deadline = goal.deadline,
                    additionalNotes = goal.additionalNotes,
                    goalPriority = goal.priority.name
                )
                onEditDataSet(goal.goalImage)
            }
        }
    }

    fun editSavingGoal(goalId: Long, context: Context) {
        viewModelScope.launch(Dispatchers.IO) {
            val goal = goalDao.getGoalById(goalId)
            val editGoal = Goal(
                title = state.goalTitleText,
                targetAmount = Utils.roundDecimal(state.targetAmount.toDouble()),
                deadline = state.deadline,
                goalImage = if (state.goalImageUri != null) ImageUtils.uriToBitmap(
                    uri = state.goalImageUri!!, context = context, maxSize = 1024
                ) else goal.goalImage,
                additionalNotes = state.additionalNotes,
                priority = GoalPriority.values().find { it.name == state.goalPriority }!!,

                //TODO: Temporary, for testing
                reminder = GoalReminder.None
            )
            // copy id of already saved goal to update it.
            editGoal.goalId = goal.goalId
            goalDao.updateGoal(editGoal)
        }
    }

    fun removeDeadLine() {
        state = state.copy(deadline = "")
    }

}