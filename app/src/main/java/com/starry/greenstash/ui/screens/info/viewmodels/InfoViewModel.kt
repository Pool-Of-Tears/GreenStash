package com.starry.greenstash.ui.screens.info.viewmodels

import androidx.lifecycle.ViewModel
import com.starry.greenstash.database.GoalDao
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class InfoViewModel @Inject constructor(private val goalDao: GoalDao) : ViewModel() {

}