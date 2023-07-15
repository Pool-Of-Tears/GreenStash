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


package com.starry.greenstash.ui.screens.home.viewmodels

import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import com.starry.greenstash.database.goal.Goal
import com.starry.greenstash.database.goal.GoalDao
import com.starry.greenstash.database.transaction.Transaction
import com.starry.greenstash.database.transaction.TransactionDao
import com.starry.greenstash.database.transaction.TransactionType
import com.starry.greenstash.reminder.ReminderManager
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject

enum class SearchWidgetState { OPENED, CLOSED }
enum class BottomSheetType { GOAL_ACHIEVED, FILTER_MENU }

enum class FilterField { Title, Amount, Priority }
enum class FilterSortType(val value: Int) { Ascending(1), Descending(2) }
data class FilterFlowData(val filterField: FilterField, val sortType: FilterSortType)

@ExperimentalMaterialApi
@ExperimentalFoundationApi
@ExperimentalComposeUiApi
@ExperimentalAnimationApi
@ExperimentalMaterial3Api
@ExperimentalCoroutinesApi
@HiltViewModel
class HomeViewModel @Inject constructor(
    private val goalDao: GoalDao,
    private val transactionDao: TransactionDao,
    private val reminderManager: ReminderManager
) : ViewModel() {

    private val _filterFlowData: MutableState<FilterFlowData> = mutableStateOf(
        FilterFlowData(
            FilterField.Title,
            FilterSortType.Ascending
        )
    )
    val filterFlowData: State<FilterFlowData> = _filterFlowData

    private val filterFlow = MutableStateFlow(filterFlowData.value)
    private val goalsListFlow = filterFlow.flatMapLatest {
        when (it.filterField) {
            FilterField.Title -> {
                goalDao.getAllGoalsByTitle(it.sortType.value)
            }

            FilterField.Amount -> {
                goalDao.getAllGoalsByAmount(it.sortType.value)
            }

            FilterField.Priority -> {
                goalDao.getAllGoalsByPriority(it.sortType.value)
            }
        }
    }
    val goalsList = goalsListFlow.asLiveData()

    private val _searchWidgetState: MutableState<SearchWidgetState> =
        mutableStateOf(value = SearchWidgetState.CLOSED)
    val searchWidgetState: State<SearchWidgetState> = _searchWidgetState

    private val _searchTextState: MutableState<String> = mutableStateOf(value = "")
    val searchTextState: State<String> = _searchTextState

    fun updateSearchWidgetState(newValue: SearchWidgetState) {
        _searchWidgetState.value = newValue
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


    fun deleteGoal(goal: Goal) {
        viewModelScope.launch(Dispatchers.IO) {
            goalDao.deleteGoal(goal.goalId)
            if (reminderManager.isReminderSet(goal.goalId)) {
                reminderManager.stopReminder(goal.goalId)
            }
        }
    }

    fun deposit(goal: Goal, amount: Double, notes: String, onGoalAchieved: () -> Unit) {
        viewModelScope.launch(Dispatchers.IO) {
            addTransaction(goal.goalId, amount, notes, TransactionType.Deposit)
            /**
             * check weather goal is achieved or not after inserting the
             * amount in goal database and call the goal achieved function
             * to show a congratulations message to the user.
             */
            val goalItem = goalDao.getGoalWithTransactionById(goal.goalId)!!
            val remainingAmount = (goal.targetAmount - goalItem.getCurrentlySavedAmount())
            if (remainingAmount <= 0f) {
                withContext(Dispatchers.Main) { onGoalAchieved() }
            }
        }
    }

    fun withdraw(goal: Goal, amount: Double, notes: String) {
        viewModelScope.launch(Dispatchers.IO) {
            addTransaction(goal.goalId, amount, notes, TransactionType.Withdraw)
        }
    }

    private suspend fun addTransaction(
        goalId: Long, amount: Double, notes: String, transactionType: TransactionType
    ) {
        val transaction = Transaction(
            ownerGoalId = goalId,
            type = transactionType,
            timeStamp = System.currentTimeMillis(),
            amount = amount,
            notes = notes
        )
        transactionDao.insertTransaction(transaction)
    }
}
