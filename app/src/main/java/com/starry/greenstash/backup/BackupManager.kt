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
import android.graphics.Bitmap
import android.util.Log
import androidx.annotation.Keep
import androidx.core.content.FileProvider
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import com.starry.greenstash.BuildConfig
import com.starry.greenstash.database.core.GoalWithTransactions
import com.starry.greenstash.database.goal.GoalDao
import com.starry.greenstash.utils.updateText
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.File
import java.time.LocalDateTime

/**
 * Handles all backup & restore related functionalities.
 * Note: Access this class using DI instead of manually initialising.
 *
 * @param context [Context] instance.
 * @param goalDao [GoalDao] instance.
 */
class BackupManager(private val context: Context, private val goalDao: GoalDao) {

    /**
     * Instance of [Gson] with custom type adaptor applied for serializing
     * and deserializing [Bitmap] fields.
     */
    private val gsonInstance = GsonBuilder()
        .registerTypeAdapter(Bitmap::class.java, BitmapTypeAdapter())
        .setDateFormat(ISO8601_DATE_FORMAT)
        .create()

    companion object {
        /** Backup schema version. */
        const val BACKUP_SCHEMA_VERSION = 1

        /** Authority for using file provider API. */
        private const val FILE_PROVIDER_AUTHORITY = "${BuildConfig.APPLICATION_ID}.provider"

        /** An ISO-8601 date format for Gson */
        private const val ISO8601_DATE_FORMAT = "yyyy-MM-dd'T'HH:mm:ss.SSSZ"

        /** Backup folder name inside cache directory. */
        private const val BACKUP_FOLDER_NAME = "backups"
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
    data class BackupJsonModel(
        val version: Int = BACKUP_SCHEMA_VERSION,
        val timestamp: Long,
        val data: List<GoalWithTransactions>
    )

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
    suspend fun createDatabaseBackup(): Intent = withContext(Dispatchers.IO) {
        log("Fetching goals from database and serialising into json...")
        val goalsWithTransactions = goalDao.getAllGoals()
        val jsonString = gsonInstance.toJson(
            BackupJsonModel(
                timestamp = System.currentTimeMillis(),
                data = goalsWithTransactions
            )
        )

        log("Creating backup json file inside cache directory...")
        val fileName = "GreenStash-Backup(${System.currentTimeMillis()}).json"
        val file = File(File(context.cacheDir, BACKUP_FOLDER_NAME).apply { mkdir() }, fileName)
        file.updateText(jsonString)
        val uri = FileProvider.getUriForFile(context, FILE_PROVIDER_AUTHORITY, file)

        log("Building and returning chooser intent for backup file.")
        return@withContext Intent(Intent.ACTION_SEND).apply {
            type = "application/json"
            addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
            putExtra(Intent.EXTRA_STREAM, uri)
            putExtra(Intent.EXTRA_SUBJECT, "Greenstash Backup")
            putExtra(Intent.EXTRA_TEXT, "Created at ${LocalDateTime.now()}")
        }.let { intent -> Intent.createChooser(intent, fileName) }

    }

    /**
     * Restores a database backup by deserializing the backup json string
     * and saving goals and transactions back into the database.
     *
     * @param jsonString a valid backup json as sting.
     * @param onFailure callback to be called if [BackupManager] failed parse the json string.
     * @param onSuccess callback to be called after backup was successfully restored.
     */
    suspend fun restoreDatabaseBackup(
        jsonString: String,
        onFailure: () -> Unit,
        onSuccess: () -> Unit
    ) = withContext(Dispatchers.IO) {

        // Parse json string.
        log("Parsing backup json file...")
        val backupData: BackupJsonModel? = try {
            gsonInstance.fromJson(jsonString, BackupJsonModel::class.java)
        } catch (exc: Exception) {
            log("Failed to parse backup json file! Err: ${exc.message}")
            exc.printStackTrace()
            null
        }

        if (backupData?.data == null) {
            withContext(Dispatchers.Main) { onFailure() }
            return@withContext
        }

        // Insert goal & transaction data into database.
        log("Inserting goals & transactions into the database...")
        goalDao.insertGoalWithTransaction(backupData.data)
        withContext(Dispatchers.Main) { onSuccess() }
    }
}