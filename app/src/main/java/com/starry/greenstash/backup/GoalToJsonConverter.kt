package com.starry.greenstash.backup

import android.graphics.Bitmap
import androidx.annotation.Keep
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import com.starry.greenstash.database.core.GoalWithTransactions

class GoalToJsonConverter {

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

        /** An ISO-8601 date format for Gson */
        private const val ISO8601_DATE_FORMAT = "yyyy-MM-dd'T'HH:mm:ss.SSSZ"
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

    fun convertToJson(goalWithTransactions: List<GoalWithTransactions>): String = gsonInstance.toJson(
        BackupJsonModel(timestamp = System.currentTimeMillis(), data = goalWithTransactions)
    )

    fun convertFromJson(json: String): BackupJsonModel = gsonInstance.fromJson(json, BackupJsonModel::class.java)
}