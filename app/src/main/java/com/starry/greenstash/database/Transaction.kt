package com.starry.greenstash.database

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey
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