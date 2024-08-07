package com.starry.greenstash.backup

import com.starry.greenstash.database.core.GoalWithTransactions
import com.starry.greenstash.database.goal.Goal
import com.starry.greenstash.database.goal.GoalPriority
import com.starry.greenstash.database.transaction.Transaction
import com.starry.greenstash.database.transaction.TransactionType
import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class GoalToCsvConverter {

    companion object {
        private const val CSV_DELIMITER = ","
        private val DATE_FORMAT = SimpleDateFormat("yyyy-MM-dd", Locale.US)
    }

    fun convertToCSV(goalsWithTransactions: List<GoalWithTransactions>): String {
        val headers = arrayOf(
            "Goal Title", "Goal Target Amount", "Goal Deadline", "Goal Priority", "Goal Reminder",
            "Goal Icon ID", "Transaction Type", "Transaction Date", "Transaction Amount", "Transaction Notes"
        )

        val stringBuilder = StringBuilder()

        // Write headers
        stringBuilder.append(headers.joinToString(CSV_DELIMITER))
        stringBuilder.append("\n")

        // Write data rows for all goals and their transactions
        goalsWithTransactions.forEach { goalWithTransactions ->
            val rows = goalWithTransactions.toCSVRows()
            rows.forEach { row ->
                stringBuilder.append(row.joinToString(CSV_DELIMITER))
                stringBuilder.append("\n")
            }
        }

        return stringBuilder.toString()
    }

    fun convertFromCSV(csvData: String): List<GoalWithTransactions> {
        val lines = csvData.trim().split("\n")
        if (lines.size < 2) return emptyList()

        val headers = lines[0].split(CSV_DELIMITER)
        val records = lines.drop(1).map { it.split(CSV_DELIMITER) }

        val result = mutableListOf<GoalWithTransactions>()
        var currentGoal: Goal? = null
        var currentTransactions = mutableListOf<Transaction>()

        records.forEach { record ->
            if (record[0].isNotBlank()) {
                // This is a new goal
                if (currentGoal != null) {
                    result.add(GoalWithTransactions(currentGoal, currentTransactions))
                    currentTransactions = mutableListOf()
                }
                currentGoal = createGoalFromCSVRecord(record, headers)
            } else {
                // This is a transaction for the current goal
                val transaction = createTransactionFromCSVRecord(record, headers)
                currentTransactions.add(transaction)
            }
        }

        // Add the last goal
        if (currentGoal != null) {
            result.add(GoalWithTransactions(currentGoal, currentTransactions))
        }

        return result
    }

    private fun GoalWithTransactions.toCSVRows(): List<List<String>> {
        val rows = mutableListOf<List<String>>()

        val goalRow = listOf(
            goal.title,
            goal.targetAmount.toString(),
            goal.deadline,
            goal.priority.name,
            goal.reminder.toString(),
            goal.goalIconId ?: "",
            "", "", "", ""
        )
        rows.add(goalRow)

        for (transaction in transactions) {
            val transactionRow = listOf(
                "", "", "", "", "", "",
                transaction.type.name,
                DATE_FORMAT.format(Date(transaction.timeStamp)),
                transaction.amount.toString(),
                transaction.notes
            )
            rows.add(transactionRow)
        }

        return rows
    }

    private fun createGoalFromCSVRecord(record: List<String>, headers: List<String>): Goal {
        val title = record[headers.indexOf("Goal Title")]
        val targetAmount = record[headers.indexOf("Goal Target Amount")].toDoubleOrNull() ?: throw Exception("Invalid target amount")
        val deadline = record[headers.indexOf("Goal Deadline")]
        val priority = try {
            GoalPriority.valueOf(record[headers.indexOf("Goal Priority")])
        } catch (e: IllegalArgumentException) {
            throw Exception("Invalid priority value: ${record[headers.indexOf("Goal Priority")]}")
        }
        val reminder = try {
            record[headers.indexOf("Goal Reminder")].toBoolean()
        } catch (e: Exception) {
            throw Exception("Invalid reminder value: ${record[headers.indexOf("Goal Reminder")]}")
        }
        val goalIconId = record[headers.indexOf("Goal Icon ID")].takeIf { it.isNotBlank() }

        return Goal(title, targetAmount, deadline, null, "", priority, reminder, goalIconId)
    }

    private fun createTransactionFromCSVRecord(record: List<String>, headers: List<String>): Transaction {
        val type = try {
            TransactionType.valueOf(record[headers.indexOf("Transaction Type")])
        } catch (e: IllegalArgumentException) {
            throw Exception("Invalid transaction type: ${record[headers.indexOf("Transaction Type")]}")
        }
        val dateString = record[headers.indexOf("Transaction Date")]
        val date = try {
            DATE_FORMAT.parse(dateString)?.time ?: throw Exception("Invalid date format: $dateString")
        } catch (e: ParseException) {
            throw Exception("Invalid date format: $dateString")
        }
        val amount = record[headers.indexOf("Transaction Amount")].toDoubleOrNull() ?: throw Exception("Invalid amount")
        val notes = record[headers.indexOf("Transaction Notes")]

        return Transaction(0L, type, date, amount, notes)
    }
}