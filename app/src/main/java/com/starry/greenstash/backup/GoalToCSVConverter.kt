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


package com.starry.greenstash.backup

import androidx.annotation.Keep
import com.starry.greenstash.database.core.GoalWithTransactions
import com.starry.greenstash.database.core.parseOldDeadlineToMillis
import com.starry.greenstash.database.goal.Goal
import com.starry.greenstash.database.goal.GoalPriority
import com.starry.greenstash.database.transaction.Transaction
import com.starry.greenstash.database.transaction.TransactionType
import java.io.StringWriter

/**
 * Converts [GoalWithTransactions] data to CSV format and vice versa.
 */
class GoalToCSVConverter {

    companion object {
        /** Backup schema version. */
        const val BACKUP_SCHEMA_VERSION = 1

        /** CSV delimiter. */
        const val CSV_DELIMITER = ","
    }

    /**
     * Model for backup CSV data, containing current schema version
     * and timestamp when backup was created.
     *
     * @param version backup schema version.
     * @param timestamp timestamp when backup was created.
     * @param data list of [GoalWithTransactions] to be backed up.
     */
    @Keep
    data class BackupCSVModel(
        val version: Int = BACKUP_SCHEMA_VERSION,
        val timestamp: Long,
        val data: List<GoalWithTransactions>
    )

    /**
     * Converts the given [GoalWithTransactions] list into a CSV string.
     *
     * @param goalWithTransactions List of [GoalWithTransactions] to convert to CSV.
     * @return A CSV-formatted string.
     */
    fun convertToCSV(goalWithTransactions: List<GoalWithTransactions>): String {
        val writer = StringWriter()
        writer.appendLine("Schema Version,$BACKUP_SCHEMA_VERSION")
        writer.appendLine("Timestamp,${System.currentTimeMillis()}")
        writer.appendLine(
            // Goal columns
            "Goal ID," +
                    "Title," +
                    "Target Amount," +
                    "Deadline," +
                    "Priority," +
                    "Reminder," +
                    "Goal Icon ID," +
                    "Archived," +
                    "Additional Notes," +
                    // Transaction columns
                    "Transaction ID," +
                    "Type," +
                    "Timestamp," +
                    "Amount," +
                    "Notes"
        )
        goalWithTransactions.forEach { goalWithTransaction ->
            val goal = goalWithTransaction.goal
            val transactions = goalWithTransaction.transactions

            println("Goal: ${goal.title}")
            println("Transactions: ${transactions.size}")

            if (transactions.isEmpty()) {
                writer.appendLine(
                    listOf(
                        goal.goalId,
                        goal.title,
                        goal.targetAmount,
                        goal.deadline,
                        goal.priority.name,
                        goal.reminder,
                        goal.goalIconId ?: "",
                        goal.archived,
                        goal.additionalNotes,
                        "",
                        "",
                        "",
                        "",
                        ""
                    ).joinToString(separator = CSV_DELIMITER)
                )
            } else {
                transactions.forEach { transaction ->
                    writer.appendLine(
                        listOf(
                            goal.goalId,
                            goal.title,
                            goal.targetAmount,
                            goal.deadline,
                            goal.priority.name,
                            goal.reminder,
                            goal.goalIconId ?: "",
                            goal.archived,
                            goal.additionalNotes,
                            transaction.transactionId,
                            transaction.type.name,
                            transaction.timeStamp,
                            transaction.amount,
                            transaction.notes
                        ).joinToString(separator = CSV_DELIMITER)
                    )
                }
            }
        }

        return writer.toString()
    }

    /**
     * Converts a CSV string back into a list of [GoalWithTransactions].
     *
     * @param csv The CSV string to convert.
     * @return A [BackupCSVModel] containing the version, timestamp, and data.
     */
    fun convertFromCSV(csv: String): BackupCSVModel {
        val lines = csv.lines()
        val version = lines[0].split(CSV_DELIMITER)[1].toInt()
        val timestamp = lines[1].split(CSV_DELIMITER)[1].toLong()
        val data = mutableListOf<GoalWithTransactions>()

        lines.drop(3).forEach { line ->
            if (line.isBlank()) return@forEach
            val columns = line.split(CSV_DELIMITER)

            // Check if version = 1 to properly convert old deadline format
            // which was string either dd/MM/yyyy or yyyy/MM/dd, to Long (epoch millis)
            val deadline = if (version == 1) {
                parseOldDeadlineToMillis(columns[3])
            } else {
                columns[3].toLong()
            }

            val goal = Goal(
                title = columns[1],
                targetAmount = columns[2].toDouble(),
                deadline = deadline,
                priority = GoalPriority.valueOf(columns[4]),
                reminder = columns[5].toBoolean(),
                goalIconId = columns[6].ifEmpty { null },
                archived = columns[7].toBoolean(),
                additionalNotes = columns[8],
                goalImage = null
            ).apply { goalId = columns[0].toLong() }

            val transaction = if (columns[9].isNotEmpty()) {
                Transaction(
                    ownerGoalId = columns[0].toLong(),
                    type = TransactionType.valueOf(columns[10]),
                    timeStamp = columns[11].toLong(),
                    amount = columns[12].toDouble(),
                    notes = columns[13]
                ).apply { transactionId = columns[9].toLong() }
            } else null

            val existingGoalWithTransactions = data.find { it.goal.goalId == goal.goalId }
            if (existingGoalWithTransactions != null && transaction != null) {
                val mutableTransactions = existingGoalWithTransactions.transactions.toMutableList()
                mutableTransactions.add(transaction)
                data[data.indexOf(existingGoalWithTransactions)] =
                    GoalWithTransactions(
                        goal = existingGoalWithTransactions.goal,
                        transactions = mutableTransactions
                    )
            } else {
                data.add(GoalWithTransactions(goal, transaction?.let { listOf(it) } ?: emptyList()))
            }
        }

        return BackupCSVModel(version, timestamp, data)
    }
}
