package com.starry.greenstash.database

import androidx.lifecycle.LiveData
import androidx.room.*


@Dao
interface GoalDao {

    @Insert
    suspend fun insertGoal(goal: Goal)

    @Update
    suspend fun updateGoal(goal: Goal)

    @Query("DELETE FROM saving_goal WHERE goalId = :goalId")
    suspend fun deleteGoal(goalId: Long)

    /*
     explicitly stating because we also have our own
     entity class with the same name.
    */
    @androidx.room.Transaction
    @Query("SELECT * FROM saving_goal")
    fun getAllGoals(): LiveData<List<GoalWithTransactions>>

    @Query("SELECT * FROM saving_goal WHERE goalId = :goalId")
    fun getGoalById(goalId: Long): Goal

    @androidx.room.Transaction
    @Query("SELECT * FROM saving_goal WHERE goalId = :goalId")
    fun getGoalWithTransactionById(goalId: Long): GoalWithTransactions
}