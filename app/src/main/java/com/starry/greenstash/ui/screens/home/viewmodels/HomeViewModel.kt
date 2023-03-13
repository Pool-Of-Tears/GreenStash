package com.starry.greenstash.ui.screens.home.viewmodels

import androidx.compose.runtime.MutableState
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.starry.greenstash.database.*
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Inject

enum class SearchWidgetState {
    OPENED, CLOSED
}

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val goalDao: GoalDao, private val transactionDao: TransactionDao
) : ViewModel() {
    val allGoals: LiveData<List<GoalWithTransactions>> = goalDao.getAllGoals()

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

    fun deleteGoal(goal: Goal) {
        viewModelScope.launch(Dispatchers.IO) { goalDao.deleteGoal(goal.goalId) }
    }

    fun deposit(goal: Goal, amount: Double, notes: String) {
        viewModelScope.launch(Dispatchers.IO) {
            addTransaction(goal.goalId, amount, notes, TransactionType.Deposit)
        }
    }

    fun withdraw(goal: Goal, amount: Double, notes: String) {
        viewModelScope.launch(Dispatchers.IO) {
            addTransaction(goal.goalId, amount, notes, TransactionType.Withdraw)
        }
    }

    private fun addTransaction(
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
