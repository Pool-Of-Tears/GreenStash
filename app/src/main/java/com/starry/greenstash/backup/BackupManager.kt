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

import android.content.Context
import android.content.Intent
import android.util.Log
import androidx.core.content.FileProvider
import com.starry.greenstash.BuildConfig
import com.starry.greenstash.backup.BackupType.CSV
import com.starry.greenstash.backup.BackupType.JSON
import com.starry.greenstash.database.goal.GoalDao
import com.starry.greenstash.utils.updateText
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.File
import java.time.LocalDateTime
import java.util.Locale
import java.util.UUID

/**
 * Handles all backup & restore related functionalities.
 * Note: Access this class using DI instead of manually initialising.
 *
 * @param context [Context] instance.
 * @param goalDao [GoalDao] instance.
 */
class BackupManager(private val context: Context, private val goalDao: GoalDao) {

    companion object {

        /** Authority for using file provider API. */
        private const val FILE_PROVIDER_AUTHORITY = "${BuildConfig.APPLICATION_ID}.provider"

        /** Backup folder name inside cache directory. */
        private const val BACKUP_FOLDER_NAME = "backups"
    }

    // Converters for different backup types.
    private val goalToJsonConverter = GoalToJSONConverter()
    private val goalToCsvConverter = GoalToCSVConverter()

    /**
     * Logger function with pre-applied tag.
     */
    private fun log(message: String) {
        Log.d("BackupManager", message)
    }

    /**
     * Creates a database backup by converting goals and transaction data into json
     * then saving that json file into cache directory and retuning a chooser intent
     * for the backup file.
     *
     * @return a chooser [Intent] for newly created backup file.
     */
    suspend fun createDatabaseBackup(backupType: BackupType): Intent = withContext(Dispatchers.IO) {
        log("Fetching goals from database and serialising into ${backupType.name}...")
        val goalsWithTransactions = goalDao.getAllGoals()
        val backupString = when (backupType) {
            BackupType.JSON -> goalToJsonConverter.convertToJson(goalsWithTransactions)
            BackupType.CSV -> goalToCsvConverter.convertToCSV(goalsWithTransactions)
        }

        log("Creating a ${backupType.name} file inside cache directory...")
        val fileName = "GreenStash-(${UUID.randomUUID()}).${backupType.name.lowercase(Locale.US)}"
        val file = File(File(context.cacheDir, BACKUP_FOLDER_NAME).apply { mkdir() }, fileName)
        file.updateText(backupString)
        val uri = FileProvider.getUriForFile(context, FILE_PROVIDER_AUTHORITY, file)

        log("Building and returning chooser intent for backup file.")
        val intentType = when (backupType) {
            BackupType.JSON -> "application/json"
            BackupType.CSV -> "text/csv"
        }
        return@withContext Intent(Intent.ACTION_SEND).apply {
            type = intentType
            addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
            putExtra(Intent.EXTRA_STREAM, uri)
            putExtra(Intent.EXTRA_SUBJECT, "Greenstash Backup")
            putExtra(Intent.EXTRA_TEXT, "Created at ${LocalDateTime.now()}")
        }.let { intent -> Intent.createChooser(intent, fileName) }
    }

    /**
     * Restores a database backup by deserializing the backup json or csv string
     * and saving goals and transactions back into the database.
     *
     * @param backupString a valid backup json or csv string.
     * @param onFailure callback to be called if [BackupManager] failed parse the json string.
     * @param onSuccess callback to be called after backup was successfully restored.
     */
    suspend fun restoreDatabaseBackup(
        backupString: String,
        backupType: BackupType = BackupType.JSON,
        onFailure: () -> Unit,
        onSuccess: () -> Unit,
    ) = withContext(Dispatchers.IO) {
        log("Parsing backup file...")
        when (backupType) {
            BackupType.JSON -> restoreJsonBackup(backupString, onFailure, onSuccess)
            BackupType.CSV -> restoreCsvBackup(backupString, onFailure, onSuccess)
        }
    }

    // Restores json backup by converting json string into [BackupJsonModel] and
    // then inserting goals and transactions into the database.
    private suspend fun restoreJsonBackup(
        backupString: String,
        onFailure: () -> Unit,
        onSuccess: () -> Unit
    ) {
        val backupData = try {
            goalToJsonConverter.convertFromJson(backupString)
        } catch (exc: Exception) {
            log("Failed to parse backup json file! Err: ${exc.message}")
            exc.printStackTrace()
            null
        }

        if (backupData?.data == null) {
            withContext(Dispatchers.Main) { onFailure() }
            return
        }

        log("Inserting goals & transactions into the database...")
        goalDao.insertGoalWithTransactions(backupData.data)
        withContext(Dispatchers.Main) { onSuccess() }
    }

    // Restores csv backup by converting csv string into [GoalWithTransactions] list.
    private suspend fun restoreCsvBackup(
        backupString: String,
        onFailure: () -> Unit,
        onSuccess: () -> Unit
    ) {
        val backupData = try {
            goalToCsvConverter.convertFromCSV(backupString)
        } catch (exc: Exception) {
            log("Failed to parse backup csv file! Err: ${exc.message}")
            exc.printStackTrace()
            null
        }

        if (backupData?.data == null) {
            withContext(Dispatchers.Main) { onFailure() }
            return
        }

        log("Inserting goals & transactions into the database...")
        goalDao.insertGoalWithTransactions(backupData.data)
        withContext(Dispatchers.Main) { onSuccess() }
    }

}


/**
 * Type of backup file.
 *
 * @property JSON for JSON backup file.
 * @property CSV for CSV backup file.
 *
 * @see [BackupManager]
 */
enum class BackupType { JSON, CSV }