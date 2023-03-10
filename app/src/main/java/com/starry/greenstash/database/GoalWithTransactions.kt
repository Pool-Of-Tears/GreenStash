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
    fun getCurrentAmount(): Double = transactions.fold(0f.toDouble()) { acc, transaction ->
        when (transaction.type) {
            TransactionType.Deposit -> {
                acc + transaction.amount
            }
            TransactionType.Withdraw -> {
                acc - transaction.amount
            }
            else -> {
                acc
            }
        }
    }
}