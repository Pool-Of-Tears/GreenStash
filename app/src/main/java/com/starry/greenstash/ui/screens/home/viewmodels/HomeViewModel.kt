package com.starry.greenstash.ui.screens.home.viewmodels

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import com.starry.greenstash.database.GoalDao
import com.starry.greenstash.database.GoalWithTransactions
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject


@HiltViewModel
class HomeViewModel @Inject constructor(private val goalDao: GoalDao) : ViewModel() {
    val allGoals: LiveData<List<GoalWithTransactions>> = goalDao.getAllGoals()
}
