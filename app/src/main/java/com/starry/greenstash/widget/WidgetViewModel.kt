package com.starry.greenstash.widget

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.starry.greenstash.database.core.GoalWithTransactions
import com.starry.greenstash.database.goal.GoalDao
import com.starry.greenstash.database.widget.WidgetDao
import com.starry.greenstash.utils.toToast
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject

class WidgetViewModel @Inject constructor(
    private val widgetDao: WidgetDao,
    private val goalDao: GoalDao
) : ViewModel() {

    fun getWidgetData(
        appWidgetId: Int,
        context: Context,
        callback: (goalItem: GoalWithTransactions) -> Unit
    ) {
        "VM $appWidgetId".toToast(context)
        viewModelScope.launch(Dispatchers.IO) {
            val widgetData = widgetDao.getWidgetData(appWidgetId)
            val goalItem = widgetData?.let { goalDao.getGoalWithTransactionById(it.goalId) }
            withContext(Dispatchers.Main) { goalItem?.let { callback(it) } }
        }
    }
}