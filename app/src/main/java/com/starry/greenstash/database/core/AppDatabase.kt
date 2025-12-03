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

import android.content.ContentValues
import android.content.Context
import androidx.room.AutoMigration
import androidx.room.Database
import androidx.room.OnConflictStrategy
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase
import com.starry.greenstash.database.goal.Goal
import com.starry.greenstash.database.goal.GoalDao
import com.starry.greenstash.database.transaction.Transaction
import com.starry.greenstash.database.transaction.TransactionDao
import com.starry.greenstash.database.widget.WidgetDao
import com.starry.greenstash.database.widget.WidgetData
import java.time.LocalDate
import java.time.ZoneId
import java.time.format.DateTimeFormatter


val MIGRATION_7_8 = object : Migration(7, 8) {
    override fun migrate(db: SupportSQLiteDatabase) {

        // Create new table with correct schema (deadline as INTEGER)
        db.execSQL(
            """
            CREATE TABLE IF NOT EXISTS `saving_goal_new` (
                `goalId` INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL,
                `title` TEXT NOT NULL,
                `targetAmount` REAL NOT NULL,
                `deadline` INTEGER NOT NULL,
                `goalImage` BLOB,
                `additionalNotes` TEXT NOT NULL,
                `priority` INTEGER NOT NULL DEFAULT 2,
                `reminder` INTEGER NOT NULL DEFAULT 0,
                `goalIconId` TEXT DEFAULT 'Image',
                `archived` INTEGER NOT NULL DEFAULT 0
            )
            """.trimIndent()
        )

        // Copy data from old table
        val cursor = db.query(
            """
            SELECT goalId, title, targetAmount, deadline, goalImage, additionalNotes,
                   priority, reminder, goalIconId, archived
            FROM saving_goal
            """.trimIndent()
        )

        // Migrate each row
        while (cursor.moveToNext()) {
            val goalId = cursor.getLong(0)
            val title = cursor.getString(1)
            val targetAmount = cursor.getDouble(2)
            val deadlineString = cursor.getString(3)
            val goalImage = cursor.getBlob(4)
            val additionalNotes = cursor.getString(5)
            val priorityInt = cursor.getInt(6)
            val reminder = cursor.getInt(7)
            val goalIconId = if (!cursor.isNull(8)) cursor.getString(8) else null
            val archived = cursor.getInt(9)

            val deadlineMillis = parseOldDeadlineToMillis(deadlineString)

            val values = ContentValues().apply {
                put("goalId", goalId)
                put("title", title)
                put("targetAmount", targetAmount)
                put("deadline", deadlineMillis)
                put("goalImage", goalImage)
                put("additionalNotes", additionalNotes)
                put("priority", priorityInt)
                put("reminder", reminder)
                // if old value missing, DB-level default 'Image' will be used
                put("goalIconId", goalIconId)
                put("archived", archived)
            }

            db.insert("saving_goal_new", OnConflictStrategy.REPLACE, values)
        }
        cursor.close()

        // Drop old table and rename new table
        db.execSQL("DROP TABLE saving_goal")
        db.execSQL("ALTER TABLE saving_goal_new RENAME TO saving_goal")
    }
}

// Helper function to parse old deadline string to epoch millis
fun parseOldDeadlineToMillis(raw: String?): Long {
    if (raw.isNullOrBlank()) return 0L

    return try {
        val normalized = raw.trim()

        val date: LocalDate = if (normalized.matches(Regex("""\d{4}[/\-]\d{2}[/\-]\d{2}"""))) {
            // yyyy/MM/dd (or with -)
            LocalDate.parse(
                normalized.replace('-', '/'),
                DateTimeFormatter.ofPattern("yyyy/MM/dd")
            )
        } else {
            // dd/MM/yyyy
            LocalDate.parse(
                normalized,
                DateTimeFormatter.ofPattern("dd/MM/yyyy")
            )
        }

        date.atStartOfDay(ZoneId.systemDefault()).toInstant().toEpochMilli()
    } catch (e: Exception) {
        e.printStackTrace()
        0L // Default to 0 if parsing fails, equivalent to no deadline
    }
}

@Database(
    entities = [Goal::class, Transaction::class, WidgetData::class],
    version = 8,
    exportSchema = true,
    autoMigrations = [
        AutoMigration(from = 1, to = 2),
        AutoMigration(from = 2, to = 3),
        AutoMigration(from = 3, to = 4),
        AutoMigration(from = 4, to = 5),
        AutoMigration(from = 5, to = 6),
        AutoMigration(from = 6, to = 7)
    ]
)
@TypeConverters(Converters::class)
abstract class AppDatabase : RoomDatabase() {

    abstract fun getGoalDao(): GoalDao
    abstract fun getTransactionDao(): TransactionDao
    abstract fun getWidgetDao(): WidgetDao

    companion object {

        @Volatile
        private var INSTANCE: AppDatabase? = null
        private const val DATABASE_NAME = "greenstash.db"

        fun getInstance(context: Context): AppDatabase {
            // if the INSTANCE is not null, then return it,
            // if it is, then create the database and save
            // in instance variable then return it.
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    DATABASE_NAME
                ).addMigrations(MIGRATION_7_8).build()
                INSTANCE = instance
                // return instance
                instance
            }
        }
    }

}
