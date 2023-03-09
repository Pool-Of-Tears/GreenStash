package com.starry.greenstash.ui.screens.home.viewmodels

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.starry.greenstash.database.Goal
import com.starry.greenstash.database.GoalDao
import com.starry.greenstash.database.GoalWithTransactions
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Inject


@HiltViewModel
class HomeViewModel @Inject constructor(private val goalDao: GoalDao) : ViewModel() {
    val allGoals: LiveData<List<GoalWithTransactions>> = goalDao.getAllGoals()

    fun deleteGoal(goal: Goal) {
        viewModelScope.launch(Dispatchers.IO) { goalDao.deleteGoal(goal.goalId) }
    }
}
