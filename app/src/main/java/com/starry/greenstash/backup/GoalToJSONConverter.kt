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

import android.graphics.Bitmap
import androidx.annotation.Keep
import com.starry.greenstash.database.core.GoalWithTransactions
import com.starry.greenstash.database.core.parseOldDeadlineToMillis
import com.starry.greenstash.database.goal.Goal
import com.starry.greenstash.database.goal.GoalPriority
import com.starry.greenstash.database.transaction.Transaction
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.intOrNull
import kotlinx.serialization.json.jsonObject
import kotlinx.serialization.json.jsonPrimitive

/**
 * Converts [GoalWithTransactions] data to JSON format and vice versa.
 */
class GoalToJSONConverter {

    // JSON serializer/deserializer.
    private val json = Json {
        ignoreUnknownKeys = true
        encodeDefaults = true
        explicitNulls = false
    }

    companion object {
        /** Backup schema version. */
        const val BACKUP_SCHEMA_VERSION = 2
    }

    /**
     * Model for backup json data, containing current schema version
     * and timestamp when backup was created.
     *
     * @param version backup schema version.
     * @param timestamp timestamp when backup was created.
     * @param data list of [GoalWithTransactions] to be backed up.
     */
    @Keep
    @Serializable
    data class BackupJsonModel(
        val version: Int = BACKUP_SCHEMA_VERSION,
        val timestamp: Long,
        val data: List<GoalWithTransactions>
    )


    // Compatibility layer for supporting old backup format where Goal.deadline was String
    // in "dd/MM/yyyy" or "yyyy/MM/dd" format.  We convert it to current model with deadline
    // as Long (epoch millis).
    //
    // Note: This Compatibility layer can be removed in future versions when we no longer
    // need to support old backups.
    @Keep
    @Serializable
    data class BackupJsonModelV1(
        val version: Int = 1,
        val timestamp: Long,
        val data: List<GoalWithTransactionsV1>
    )

    @Keep
    @Serializable
    data class GoalWithTransactionsV1(
        val goal: GoalV1,
        val transactions: List<Transaction>
    )

    @Keep
    @Serializable
    data class GoalV1(
        val title: String,
        val targetAmount: Double,
        val deadline: String, // OLD representation: "dd/MM/yyyy" or "yyyy/MM/dd"
        @Serializable(with = BitmapSerializer::class)
        val goalImage: Bitmap? = null,
        val additionalNotes: String = "",
        val priority: GoalPriority = GoalPriority.Normal,
        val reminder: Boolean = false,
        val goalIconId: String? = "Image",
        val archived: Boolean = false,
        val goalId: Long = 0L
    )

    private fun BackupJsonModelV1.toCurrentModel(): BackupJsonModel {
        val convertedData = data.map { oldItem ->
            val oldGoal = oldItem.goal
            val deadlineMillis = parseOldDeadlineToMillis(oldGoal.deadline)
            // Build current Goal (which now has deadline: Long)
            val newGoal = Goal(
                title = oldGoal.title,
                targetAmount = oldGoal.targetAmount,
                deadline = deadlineMillis,
                goalImage = oldGoal.goalImage,
                additionalNotes = oldGoal.additionalNotes,
                priority = oldGoal.priority,
                reminder = oldGoal.reminder,
                goalIconId = oldGoal.goalIconId,
                archived = oldGoal.archived
            ).apply {
                goalId = oldGoal.goalId
            }
            GoalWithTransactions(
                goal = newGoal,
                transactions = oldItem.transactions
            )
        }

        return BackupJsonModel(
            version = BACKUP_SCHEMA_VERSION,
            timestamp = this.timestamp,
            data = convertedData
        )
    }
    // End of compatibility layer. ==================================


    fun convertToJson(goalWithTransactions: List<GoalWithTransactions>): String =
        json.encodeToString(
            BackupJsonModel.serializer(),
            BackupJsonModel(timestamp = System.currentTimeMillis(), data = goalWithTransactions)
        )

    fun convertFromJson(jsonString: String): BackupJsonModel {
        // Compatibility layer for old backup format.
        // Check if version == 1 in jsonString to properly convert old deadline format
        // which was string either dd/MM/yyyy or yyyy/MM/dd, to Long (epoch millis)
        val jsonElement = json.parseToJsonElement(jsonString)
        val version = jsonElement.jsonObject["version"]?.jsonPrimitive?.intOrNull
        if (version == 1) {
            val oldModel = json.decodeFromString(BackupJsonModelV1.serializer(), jsonString)
            return oldModel.toCurrentModel()
        }
        // End of compatibility layer
        return json.decodeFromString(BackupJsonModel.serializer(), jsonString)
    }
}