package com.starry.greenstash.database

import androidx.room.Embedded
import androidx.room.Relation

data class GoalWithTransactions(
    @Embedded val goal: Goal,
    @Relation(
        parentColumn = "goalId",
        entityColumn = "ownerGoalId"
    )
    val transactions: List<Transaction>
) {
    fun getCurrentAmount() = transactions.sumOf { it.amount }
}