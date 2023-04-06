package com.starry.greenstash.ui.widget

import androidx.lifecycle.ViewModel
import com.starry.greenstash.database.GoalDao
import javax.inject.Inject

class WidgetViewModel @Inject constructor(private val goalDao: GoalDao) : ViewModel() {

}