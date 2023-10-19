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


package com.starry.greenstash.database.core

import androidx.annotation.Keep
import androidx.room.Embedded
import androidx.room.Relation
import com.starry.greenstash.database.goal.Goal
import com.starry.greenstash.database.transaction.Transaction
import com.starry.greenstash.database.transaction.TransactionType

@Keep
data class GoalWithTransactions(
    @Embedded val goal: Goal,
    @Relation(
        parentColumn = "goalId",
        entityColumn = "ownerGoalId"
    )
    val transactions: List<Transaction>
) {
    fun getCurrentlySavedAmount(): Double = transactions.fold(0f.toDouble()) { acc, transaction ->
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