package com.starry.greenstash.widget.configuration

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.starry.greenstash.database.core.GoalWithTransactions
import com.starry.greenstash.database.goal.GoalDao
import com.starry.greenstash.database.widget.WidgetDao
import com.starry.greenstash.database.widget.WidgetData
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject


@HiltViewModel
class WidgetConfigViewModel @Inject constructor(
    private val goalDao: GoalDao,
    private val widgetDao: WidgetDao
) : ViewModel() {
    val allGoals: LiveData<List<GoalWithTransactions>> = goalDao.getAllGoals()

    /**
     * Maps widget id with the selected saving goal id
     * and saves it into database.
     */
    fun setWidgetData(
        widgetId: Int,
        goalId: Long,
        onComplete: (goalItem: GoalWithTransactions) -> Unit
    ) {
        viewModelScope.launch(Dispatchers.IO) {
            val widgetData = WidgetData(appWidgetId = widgetId, goalId = goalId)
            widgetDao.insertWidgetData(widgetData)
            val goalItem = goalDao.getGoalWithTransactionById(goalId)
            withContext(Dispatchers.Main) { onComplete(goalItem) }
        }
    }
}