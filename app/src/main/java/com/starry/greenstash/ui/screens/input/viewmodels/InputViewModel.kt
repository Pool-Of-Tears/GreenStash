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
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.State
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.starry.greenstash.R
import com.starry.greenstash.database.goal.Goal
import com.starry.greenstash.database.goal.GoalDao
import com.starry.greenstash.database.goal.GoalPriority
import com.starry.greenstash.reminder.ReminderManager
import com.starry.greenstash.ui.screens.settings.viewmodels.DateStyle
import com.starry.greenstash.utils.ImageUtils
import com.starry.greenstash.utils.PreferenceUtil
import com.starry.greenstash.utils.Utils
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.BufferedReader
import java.io.InputStreamReader
import javax.inject.Inject

data class IconItem(
    var id: String = "",
    var name: String = "",
    var image: ImageVector? = null,
)

data class IconsState(
    val searchText: String = "",
    val icons: List<List<IconItem>> = emptyList(),
    val currentIcon: IconItem? = null,
    val selectedIcon: IconItem? = null,
    val loading: Boolean = true
)

data class InputScreenState(
    val goalImageUri: Uri? = null,
    val goalTitleText: String = "",
    val targetAmount: String = "",
    val deadline: String = "",
    val additionalNotes: String = "",
    val priority: String = GoalPriority.Normal.name,
    val reminder: Boolean = false
)

@ExperimentalCoroutinesApi
@ExperimentalMaterialApi
@ExperimentalFoundationApi
@ExperimentalComposeUiApi
@ExperimentalAnimationApi
@ExperimentalMaterial3Api
@HiltViewModel
class InputViewModel @Inject constructor(
    private val goalDao: GoalDao,
    private val reminderManager: ReminderManager,
    private val preferenceUtil: PreferenceUtil
) : ViewModel() {

    var state by mutableStateOf(InputScreenState())

    // Icons state
    private val _iconState = mutableStateOf(IconsState())
    val iconState: State<IconsState> = _iconState

    private var iconSearchJob: Job? = null

    private val _showOnboardingTapTargets: MutableState<Boolean> = mutableStateOf(
        value = preferenceUtil.getBoolean(
            PreferenceUtil.INPUT_SCREEN_ONBOARDING_BOOL,
            true
        )
    )
    val showOnboardingTapTargets: State<Boolean> = _showOnboardingTapTargets

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
                priority = GoalPriority.entries.find { it.name == state.priority }!!,
                reminder = state.reminder,
                goalIconId = iconState.value.selectedIcon?.id
            )

            // Add goal into database.
            val goalId = goalDao.insertGoal(goal)
            // schedule reminder if it's enabled.
            if (goal.reminder) {
                reminderManager.scheduleReminder(goalId)
            }
        }
    }

    fun setEditGoalData(
        goalId: Long,
        onEditDataSet: (goalImage: Bitmap?, goalIconId: String?) -> Unit
    ) {
        viewModelScope.launch(Dispatchers.IO) {
            val goal = goalDao.getGoalById(goalId)!!
            withContext(Dispatchers.Main) {
                state = state.copy(
                    goalTitleText = goal.title,
                    targetAmount = goal.targetAmount.toString(),
                    deadline = goal.deadline,
                    additionalNotes = goal.additionalNotes,
                    priority = goal.priority.name,
                    reminder = goal.reminder
                )
                onEditDataSet(goal.goalImage, goal.goalIconId)
            }
        }
    }

    fun editSavingGoal(goalId: Long, context: Context) {
        viewModelScope.launch(Dispatchers.IO) {
            val goal = goalDao.getGoalById(goalId)!!
            val newGoal = Goal(
                title = state.goalTitleText,
                targetAmount = Utils.roundDecimal(state.targetAmount.toDouble()),
                deadline = state.deadline,
                goalImage = if (state.goalImageUri != null) ImageUtils.uriToBitmap(
                    uri = state.goalImageUri!!, context = context, maxSize = 1024
                ) else goal.goalImage,
                additionalNotes = state.additionalNotes,
                priority = GoalPriority.entries.find { it.name == state.priority }!!,
                reminder = state.reminder,
                goalIconId = iconState.value.selectedIcon?.id ?: goal.goalIconId
            )
            // copy id of already saved goal to update it.
            newGoal.goalId = goal.goalId
            goalDao.updateGoal(newGoal)

            // Handle possible changes made in reminders.
            if (newGoal.reminder) {
                if (!reminderManager.isReminderSet(goalId))
                    reminderManager.scheduleReminder(goalId)
            } else {
                reminderManager.stopReminder(goalId)
            }
        }
    }

    fun removeDeadLine() {
        state = state.copy(deadline = "")
    }

    fun getDateStyleValue() = preferenceUtil.getString(
        PreferenceUtil.DATE_FORMAT_STR, DateStyle.DateMonthYear.pattern
    )

    fun updateIconSearch(context: Context, search: String) {
        _iconState.value = _iconState.value.copy(searchText = search)
        iconSearchJob?.cancel()
        iconSearchJob = viewModelScope.launch(Dispatchers.IO) {
            // Add delay to avoid frequent search.
            delay(400)

            withContext(Dispatchers.Main) {
                _iconState.value = _iconState.value.copy(loading = true)
            }

            val icons = getNamesIcons(context)
                .filter { it.contains(search, ignoreCase = true) }
                .take(50)
                .map { parseIconItem(it) }

            val chunks = icons.chunked(3)
            withContext(Dispatchers.Main) {
                _iconState.value = _iconState.value.copy(icons = chunks, loading = false)
            }
        }
    }

    fun updateCurrentIcon(icon: IconItem) {
        _iconState.value = _iconState.value.copy(currentIcon = icon)
    }

    fun updateSelectedIcon(icon: IconItem) {
        _iconState.value = _iconState.value.copy(selectedIcon = icon)
    }

    private fun parseIconItem(line: String): IconItem {
        val split = line.split(",")
        val id = split[0]
        val name = split[1]
        val image = ImageUtils.createIconVector(id)

        return IconItem(id, name, image)
    }

    private fun getNamesIcons(context: Context): List<String> {
        val inputStream = context.resources.openRawResource(R.raw.icons_names)
        val reader = BufferedReader(InputStreamReader(inputStream))
        val lines = reader.readLines()
        reader.close()
        return lines
    }

    fun onboardingTapTargetsShown() {
        preferenceUtil.putBoolean(PreferenceUtil.INPUT_SCREEN_ONBOARDING_BOOL, false)
        _showOnboardingTapTargets.value = false
    }

}