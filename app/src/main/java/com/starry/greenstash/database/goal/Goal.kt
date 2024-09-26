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


package com.starry.greenstash.database.goal

import android.graphics.Bitmap
import androidx.annotation.Keep
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.starry.greenstash.backup.BitmapSerializer
import kotlinx.serialization.Serializable

enum class GoalPriority(val value: Int) { High(3), Normal(2), Low(1) }

@Keep
@Serializable
@Entity(tableName = "saving_goal")
data class Goal(
    val title: String,
    val targetAmount: Double,
    val deadline: String,
    @Serializable(with = BitmapSerializer::class)
    val goalImage: Bitmap?,
    val additionalNotes: String,

    // Added in database schema v3
    @ColumnInfo(defaultValue = "2")
    val priority: GoalPriority,
    // Added in database schema v4
    @ColumnInfo(defaultValue = "0")
    val reminder: Boolean,
    // Added in database schema v5
    @ColumnInfo(defaultValue = "Image")
    val goalIconId: String?,
    // Added in database schema v6
    @ColumnInfo(defaultValue = "0")
    val archived: Boolean = false
) {
    @PrimaryKey(autoGenerate = true)
    var goalId: Long = 0L
}