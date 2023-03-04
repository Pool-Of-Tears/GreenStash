package com.starry.greenstash.ui.screens.input.viewmodels

import android.content.Context
import android.net.Uri
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.starry.greenstash.database.Goal
import com.starry.greenstash.database.GoalDao
import com.starry.greenstash.utils.Utils
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
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
                goalImage = if (state.goalImageUri != null) Utils.uriToBitmap(
                    state.goalImageUri!!, context
                ) else null,
                additionalNotes = state.additionalNotes
            )
            // Add goal into database.
            goalDao.insertGoal(goal)
        }
    }

}