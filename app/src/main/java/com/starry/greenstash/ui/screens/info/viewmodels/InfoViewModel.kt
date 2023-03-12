package com.starry.greenstash.ui.screens.info.viewmodels

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.starry.greenstash.database.GoalDao
import com.starry.greenstash.database.GoalWithTransactions
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import javax.inject.Inject

data class InfoScreenState(
    val isLoading: Boolean = true,
    val goalData: GoalWithTransactions? = null
)

@HiltViewModel
class InfoViewModel @Inject constructor(private val goalDao: GoalDao) : ViewModel() {
    var state by mutableStateOf(InfoScreenState())
    fun loadGoalData(goalId: Long) {
        viewModelScope.launch(Dispatchers.IO) {
            val goalWithTransactions = goalDao.getGoalWithTransactionById(goalId)
            delay(708L)
            state = state.copy(isLoading = false, goalData = goalWithTransactions)
        }
    }
}