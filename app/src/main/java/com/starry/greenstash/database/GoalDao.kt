package com.starry.greenstash.database

import androidx.lifecycle.LiveData
import androidx.room.*


@Dao
interface GoalDao {

    /*
     explicitly stating because we also have our own
     entity class with the same name.
    */
    @androidx.room.Transaction
    @Query("SELECT * FROM saving_goal")
    fun getAllGoals(): LiveData<List<GoalWithTransactions>>

    @Insert
    suspend fun insertGoal(goal: Goal)

    @Insert
    suspend fun insertTransaction(transaction: Transaction)

    @Query("DELETE FROM saving_goal WHERE goalId = :goalId")
    suspend fun deleteGoal(goalId: Long)

    @Delete
    suspend fun deleteTransAction(transaction: Transaction)

    @Update
    suspend fun updateGoal(goal: Goal)

}