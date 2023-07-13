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


package com.starry.greenstash.database.goal

import androidx.lifecycle.LiveData
import androidx.room.*
import com.starry.greenstash.database.core.GoalWithTransactions
import kotlinx.coroutines.flow.Flow


@Dao
interface GoalDao {

    @Insert
    suspend fun insertGoal(goal: Goal): Long

    @Update
    suspend fun updateGoal(goal: Goal)

    @Query("DELETE FROM saving_goal WHERE goalId = :goalId")
    suspend fun deleteGoal(goalId: Long)

    @Transaction
    @Query("SELECT * FROM saving_goal")
    suspend fun getAllGoals(): List<GoalWithTransactions>

    @Transaction
    @Query("SELECT * FROM saving_goal")
    fun getAllGoalsAsLiveData(): LiveData<List<GoalWithTransactions>>

    @Query("SELECT * FROM saving_goal WHERE goalId = :goalId")
    suspend fun getGoalById(goalId: Long): Goal?

    @Transaction
    @Query("SELECT * FROM saving_goal WHERE goalId = :goalId")
    suspend fun getGoalWithTransactionById(goalId: Long): GoalWithTransactions?

    @Transaction
    @Query(
        "SELECT * FROM saving_goal ORDER BY " +
                "CASE WHEN :sortOrder = 1 THEN title END ASC, " +
                "CASE WHEN :sortOrder = 2 THEN title END DESC "
    )
    fun getAllGoalsByTitle(sortOrder: Int): Flow<List<GoalWithTransactions>>

    @Transaction
    @Query(
        "SELECT * FROM saving_goal ORDER BY " +
                "CASE WHEN :sortOrder = 1 THEN targetAmount END ASC, " +
                "CASE WHEN :sortOrder = 2 THEN targetAmount END DESC "
    )
    fun getAllGoalsByAmount(sortOrder: Int): Flow<List<GoalWithTransactions>>

    @Transaction
    @Query(
        "SELECT * FROM saving_goal ORDER BY " +
                "CASE WHEN :sortOrder = 1 THEN priority END ASC, " +
                "CASE WHEN :sortOrder = 2 THEN priority END DESC "
    )
    fun getAllGoalsByPriority(sortOrder: Int): Flow<List<GoalWithTransactions>>

}