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

import androidx.compose.runtime.MutableState
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import com.starry.greenstash.database.goal.Goal
import com.starry.greenstash.database.goal.GoalDao
import com.starry.greenstash.database.transaction.Transaction
import com.starry.greenstash.database.transaction.TransactionDao
import com.starry.greenstash.database.transaction.TransactionType
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

enum class FilterField { TITLE, AMOUNT, PRIORITY }
enum class FilterSortType(val value: Int) { ASCENDING(1), DESCENDING(2) }

@ExperimentalCoroutinesApi
@HiltViewModel
class HomeViewModel @Inject constructor(
    private val goalDao: GoalDao, private val transactionDao: TransactionDao
) : ViewModel() {
    private val sortFlow = MutableStateFlow(Pair(FilterField.TITLE, FilterSortType.ASCENDING))
    private val goalsListFlow = sortFlow
        .flatMapLatest {
            when (it.first) {
                FilterField.TITLE -> {
                    goalDao.getAllGoalsByTitle(it.second.value)
                }

                FilterField.AMOUNT -> {
                    goalDao.getAllGoalsByAmount(it.second.value)
                }

                FilterField.PRIORITY -> {
                    when (it.second) {
                        FilterSortType.ASCENDING -> goalDao.getAllGoalsByPriorityAsc()
                        FilterSortType.DESCENDING -> goalDao.getAllGoalsByPriorityDesc()
                    }
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

    fun updatefilter(filterField: FilterField, filterSortType: FilterSortType) {
        sortFlow.value = Pair(filterField, filterSortType)
    }

    fun deleteGoal(goal: Goal) {
        viewModelScope.launch(Dispatchers.IO) { goalDao.deleteGoal(goal.goalId) }
    }

    fun deposit(goal: Goal, amount: Double, notes: String, onGoalAchieved: () -> Unit) {
        viewModelScope.launch(Dispatchers.IO) {
            addTransaction(goal.goalId, amount, notes, TransactionType.Deposit)
            /**
             * check weather goal is achieved or not after inserting the
             * amount in goal database and call the goal achieved function
             * to show a congratulations message to the user.
             */
            val goalItem = goalDao.getGoalWithTransactionById(goal.goalId)
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
