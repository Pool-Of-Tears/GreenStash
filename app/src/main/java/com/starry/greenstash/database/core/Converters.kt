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

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import androidx.room.TypeConverter
import com.starry.greenstash.database.goal.GoalPriority
import com.starry.greenstash.database.transaction.TransactionType
import java.io.ByteArrayOutputStream

class Converters {

    @TypeConverter
    fun fromBitmap(bitmap: Bitmap?): ByteArray? =
        bitmap?.let { bmp ->
            ByteArrayOutputStream().apply {
                bmp.compress(Bitmap.CompressFormat.PNG, 100, this)
            }.toByteArray()
        }


    @TypeConverter
    fun toBitmap(byteArray: ByteArray?): Bitmap? =
        byteArray?.let { BitmapFactory.decodeByteArray(it, 0, it.size) }

    @TypeConverter
    fun fromTransactionType(value: TransactionType) = value.ordinal

    @TypeConverter
    fun toTransactionType(value: Int) = when (value) {
        TransactionType.Deposit.ordinal -> TransactionType.Deposit
        TransactionType.Withdraw.ordinal -> TransactionType.Withdraw
        else -> TransactionType.Invalid
    }

    @TypeConverter
    fun fromGoalPriority(value: GoalPriority) = value.value

    @TypeConverter
    fun toGoalPriority(value: Int) = when (value) {
        GoalPriority.High.value -> GoalPriority.High
        GoalPriority.Low.value -> GoalPriority.Low
        else -> GoalPriority.Normal
    }
}