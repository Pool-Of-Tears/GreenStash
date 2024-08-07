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
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Transaction
import androidx.room.Update
import com.starry.greenstash.database.core.GoalWithTransactions
import kotlinx.coroutines.flow.Flow


@Dao
interface GoalDao {
    // Insert related functions ==========================================================

    /**
     * Insert goal.
     * @param goal Goal to insert.
     * @return Id of inserted goal.
     */
    @Insert
    suspend fun insertGoal(goal: Goal): Long

    /**
     * Insert goal with transactions.
     * This method is used when restoring data from backup file.
     * @param goalsWithTransactions List of GoalWithTransactions.
     */
    @Transaction
    suspend fun insertGoalWithTransactions(goalsWithTransactions: List<GoalWithTransactions>) {
        goalsWithTransactions.forEach { goalWithTransactions ->
            // Set placeholder id.
            goalWithTransactions.goal.goalId = 0L
            // insert goal and get actual id from database.
            val goalId = insertGoal(goalWithTransactions.goal)
            // map transactions with inserted goal, and insert them into database.
            val transactionsWithGoalId =
                goalWithTransactions.transactions.map { it.copy(ownerGoalId = goalId) }
            insertTransactions(transactionsWithGoalId)
        }
    }

    // Update related functions ==========================================================

    /**
     * Update goal.
     * @param goal Goal to update.
     */
    @Update
    suspend fun updateGoal(goal: Goal)

    // Delete related functions ==========================================================

    /**
     * Delete goal by id.
     * @param goalId Id of goal.
     */
    @Query("DELETE FROM saving_goal WHERE goalId = :goalId")
    suspend fun deleteGoal(goalId: Long)

    // Get related functions ==========================================================

    /**
     * Get all unarchived goals.
     * @return List of GoalWithTransactions.
     */
    @Transaction
    @Query("SELECT * FROM saving_goal WHERE archived = 0")
    suspend fun getAllGoals(): List<GoalWithTransactions>

    /**
     * Get all unarchived goals as LiveData.
     * @return LiveData of List of GoalWithTransactions.
     */
    @Transaction
    @Query("SELECT * FROM saving_goal WHERE archived = 0")
    fun getAllGoalsAsLiveData(): LiveData<List<GoalWithTransactions>>

    /**
     * Get goal by id.
     * @param goalId Id of goal.
     * @return Goal.
     */
    @Query("SELECT * FROM saving_goal WHERE goalId = :goalId")
    suspend fun getGoalById(goalId: Long): Goal?

    /**
     * Get goal with transactions.
     * @param goalId Id of goal.
     * @return GoalWithTransactions.
     */
    @Transaction
    @Query("SELECT * FROM saving_goal WHERE goalId = :goalId")
    suspend fun getGoalWithTransactionById(goalId: Long): GoalWithTransactions?

    /**
     * Get goal with transactions as Flow.
     * @param goalId Id of goal.
     * @return Flow of GoalWithTransactions.
     */
    @Transaction
    @Query("SELECT * FROM saving_goal WHERE goalId = :goalId")
    fun getGoalWithTransactionByIdAsFlow(goalId: Long): Flow<GoalWithTransactions?>

    /**
     * Get all unarchived goals sorted by name.
     * @param sortOrder 1 for ascending, 2 for descending.
     * @return Flow of List of GoalWithTransactions.
     */
    @Transaction
    @Query(
        "SELECT * FROM saving_goal WHERE archived = 0 ORDER BY " +
                "CASE WHEN :sortOrder = 1 THEN title END ASC, " +
                "CASE WHEN :sortOrder = 2 THEN title END DESC "
    )
    fun getAllGoalsByTitle(sortOrder: Int): Flow<List<GoalWithTransactions>>

    /**
     * Get all unarchived goals sorted by target amount.
     * @param sortOrder 1 for ascending, 2 for descending.
     * @return Flow of List of GoalWithTransactions.
     */
    @Transaction
    @Query(
        "SELECT * FROM saving_goal WHERE archived = 0 ORDER BY " +
                "CASE WHEN :sortOrder = 1 THEN targetAmount END ASC, " +
                "CASE WHEN :sortOrder = 2 THEN targetAmount END DESC "
    )
    fun getAllGoalsByAmount(sortOrder: Int): Flow<List<GoalWithTransactions>>

    /**
     * Get all unarchived goals sorted by priority.
     * @param sortOrder 1 for ascending, 2 for descending.
     * @return Flow of List of GoalWithTransactions.
     */
    @Transaction
    @Query(
        "SELECT * FROM saving_goal WHERE archived = 0 ORDER BY " +
                "CASE WHEN :sortOrder = 1 THEN priority END ASC, " +
                "CASE WHEN :sortOrder = 2 THEN priority END DESC "
    )
    fun getAllGoalsByPriority(sortOrder: Int): Flow<List<GoalWithTransactions>>

    /**
     * Get all archived goals.
     * @return Flow of List of GoalWithTransactions.
     */
    @Transaction
    @Query("SELECT * FROM saving_goal WHERE archived = 1")
    fun getAllArchivedGoals(): Flow<List<GoalWithTransactions>>

    /**
     * For internal use with insertGoalWithTransaction() method only,
     * Please use Transaction Dao for transaction related operations.
     * @param transactions List of transactions.
     */
    @Insert
    suspend fun insertTransactions(
        transactions: List<com.starry.greenstash.database.transaction.Transaction>
    )
}