package com.starry.greenstash.ui.screens.input.viewmodels

import android.content.Context
import android.graphics.Bitmap
import android.net.Uri
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.starry.greenstash.database.Goal
import com.starry.greenstash.database.GoalDao
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
                additionalNotes = state.additionalNotes
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
                    additionalNotes = goal.additionalNotes
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
                additionalNotes = state.additionalNotes
            )
            // copy id of already saved goal to update it.
            editGoal.goalId = goal.goalId
            goalDao.updateGoal(editGoal)
        }
    }

}