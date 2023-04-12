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


package com.starry.greenstash.database.transaction

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey
import com.starry.greenstash.database.goal.Goal
import java.text.DateFormat
import java.util.*

enum class TransactionType {
    Deposit, Withdraw, Invalid
}

@Entity(
    tableName = "transaction", foreignKeys = [
        ForeignKey(
            entity = Goal::class,
            parentColumns = arrayOf("goalId"),
            childColumns = arrayOf("ownerGoalId"),
            onDelete = ForeignKey.CASCADE
        )
    ]
)
data class Transaction(
    @ColumnInfo(index = true) val ownerGoalId: Long,
    val type: TransactionType,
    val timeStamp: Long,
    val amount: Double,
    val notes: String,
) {
    @PrimaryKey(autoGenerate = true)
    var transactionId: Long = 0L

    fun getTransactionDate(): String {
        val date = Date(timeStamp)
        return DateFormat.getDateInstance().format(date)
    }
}