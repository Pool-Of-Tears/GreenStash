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


package com.starry.greenstash.ui.screens.home

import androidx.compose.runtime.MutableState
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import com.starry.greenstash.database.goal.Goal
import com.starry.greenstash.database.goal.GoalDao
import com.starry.greenstash.reminder.ReminderManager
import com.starry.greenstash.ui.screens.settings.DateStyle
import com.starry.greenstash.utils.PreferenceUtil
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.launch
import javax.inject.Inject

enum class SearchBarState { OPENED, CLOSED }
enum class FilterField { Title, Amount, Priority }
enum class FilterSortType(val value: Int) { Ascending(1), Descending(2) }
enum class GoalCardStyle { Classic, Compact }
data class FilterFlowData(val filterField: FilterField, val sortType: FilterSortType)


@HiltViewModel
class HomeViewModel @Inject constructor(
    private val goalDao: GoalDao,
    private val reminderManager: ReminderManager,
    private val preferenceUtil: PreferenceUtil
) : ViewModel() {

    private val _filterFlowData: MutableState<FilterFlowData> = mutableStateOf(
        FilterFlowData(
            FilterField.entries[preferenceUtil.getInt(
                PreferenceUtil.GOAL_FILTER_FIELD_INT,
                FilterField.Title.ordinal
            )],
            FilterSortType.entries[preferenceUtil.getInt(
                PreferenceUtil.GOAL_FILTER_SORT_TYPE_INT,
                FilterSortType.Ascending.ordinal
            )]
        )
    )
    val filterFlowData: State<FilterFlowData> = _filterFlowData

    private val filterFlow = MutableStateFlow(filterFlowData.value)

    @OptIn(ExperimentalCoroutinesApi::class)
    private val goalsListFlow = filterFlow.flatMapLatest { ffData ->

        // Save the current filter combination in shared preferences
        preferenceUtil.putInt(PreferenceUtil.GOAL_FILTER_FIELD_INT, ffData.filterField.ordinal)
        preferenceUtil.putInt(PreferenceUtil.GOAL_FILTER_SORT_TYPE_INT, ffData.sortType.ordinal)

        // Fetch the goals list based on the current filter combination
        when (ffData.filterField) {
            FilterField.Title -> {
                goalDao.getAllGoalsByTitle(ffData.sortType.value)
            }

            FilterField.Amount -> {
                goalDao.getAllGoalsByAmount(ffData.sortType.value)
            }

            FilterField.Priority -> {
                goalDao.getAllGoalsByPriority(ffData.sortType.value)
            }
        }
    }
    val goalsList = goalsListFlow.asLiveData()

    private val _searchBarState: MutableState<SearchBarState> =
        mutableStateOf(value = SearchBarState.CLOSED)
    val searchBarState: State<SearchBarState> = _searchBarState

    private val _searchTextState: MutableState<String> = mutableStateOf(value = "")
    val searchTextState: State<String> = _searchTextState

    private val _showOnboardingTapTargets: MutableState<Boolean> = mutableStateOf(
        value = preferenceUtil.getBoolean(
            PreferenceUtil.HOME_SCREEN_ONBOARDING_BOOL,
            true
        )
    )
    val showOnboardingTapTargets: State<Boolean> = _showOnboardingTapTargets

    fun updateSearchWidgetState(newValue: SearchBarState) {
        _searchBarState.value = newValue
    }

    fun updateSearchTextState(newValue: String) {
        _searchTextState.value = newValue
    }

    fun updateFilterField(filterField: FilterField) {
        /**
         * For whatever reasons, updating the data of filterFlow i.e. [MutableStateFlow]
         * doesn't change/update the UI (currently selected filter buttons), so we instead
         * keep copy of current filter combination in mutable state and update it alongside
         * filterFlow with same data so we can display current filter combination in our UI.
         */
        filterFlow.value = filterFlow.value.copy(filterField = filterField)
        _filterFlowData.value = _filterFlowData.value.copy(filterField = filterField)
    }

    fun updateFilterSort(filterSortType: FilterSortType) {
        // Same comment as above.
        filterFlow.value = filterFlow.value.copy(sortType = filterSortType)
        _filterFlowData.value = _filterFlowData.value.copy(sortType = filterSortType)
    }


    fun archiveGoal(goal: Goal) {
        viewModelScope.launch(Dispatchers.IO) {
            val updatedGoal = goal.copy(archived = true)
            updatedGoal.goalId = goal.goalId
            goalDao.updateGoal(updatedGoal)
            // Stop reminder if set for this goal
            if (reminderManager.isReminderSet(goal.goalId)) {
                reminderManager.stopReminder(goal.goalId)
            }
        }
    }

    fun deleteGoal(goal: Goal) {
        viewModelScope.launch(Dispatchers.IO) {
            goalDao.deleteGoal(goal.goalId)
            // Stop reminder if set for this goal
            if (reminderManager.isReminderSet(goal.goalId)) {
                reminderManager.stopReminder(goal.goalId)
            }
        }
    }

    fun getDefaultCurrency(): String {
        return preferenceUtil.getString(PreferenceUtil.DEFAULT_CURRENCY_STR, "")!!
    }

    fun getDateStyle(): DateStyle {
        return preferenceUtil.getInt(PreferenceUtil.DATE_STYLE_INT, DateStyle.DD_MM_YYYY.ordinal)
            .let { DateStyle.entries[it] }
    }

    fun onboardingTapTargetsShown() {
        preferenceUtil.putBoolean(PreferenceUtil.HOME_SCREEN_ONBOARDING_BOOL, false)
        _showOnboardingTapTargets.value = false
    }

}
