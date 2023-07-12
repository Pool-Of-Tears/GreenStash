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
            val goalItem = goalDao.getGoalWithTransactionById(goalId)!!
            withContext(Dispatchers.Main) { onComplete(goalItem) }
        }
    }
}