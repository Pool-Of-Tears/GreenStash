package com.starry.greenstash.widget

import androidx.lifecycle.ViewModel
import com.starry.greenstash.database.goal.GoalDao
import javax.inject.Inject

class WidgetViewModel @Inject constructor(private val goalDao: GoalDao) : ViewModel() {

}